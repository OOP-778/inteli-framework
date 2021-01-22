import base64
import json
import os
import shutil
import zipfile
from glob import glob
from multiprocessing import Process as pc
from pathlib import Path

import requests

script_location = Path(__file__).absolute().parent


class NmsHandler(object):
    def __init__(self):
        self.jarPath = None
        self.updating = False

    @property
    def isUpdating(self):
        return self.updating

    @property
    def getJarPath(self):
        return self.jarPath

    def init(self):
        if needs_update() and os.path.isdir(os.path.join(script_location, "out")):
            os.remove(os.path.join(script_location, "out"))

        self.updating = True
        print(self.updating)
        print(self.isUpdating)
        print("Gathering Minecraft Versions")

        out_location = os.path.join(str(script_location), "out")
        if not os.path.exists(out_location):
            os.makedirs(out_location)

        # Fetch all jars
        versions = fetch_minecraft_versions()

        print("Found " + str(len(versions)) + " versions!")
        processes = []

        print("Downloading...")
        for version in versions:
            processes.append(pc(target=download_artifact, args=[version]))

        for process in processes:
            process.start()

        for process in processes:
            process.join()

        print("Extracting all in one folder")

        classes_location = replace_seperators(out_location + "@classes@")
        if not os.path.isdir(classes_location):
            os.mkdir(classes_location)

        path, dirs, files = next(os.walk(classes_location))
        file_count = len(files)

        if file_count == 0:
            processes = []
            jars = scan_directory(out_location)
            for jar in jars:
                processes.append(pc(target=extract_jar_to, args=[jar, classes_location]))

            for process in processes:
                process.start()

            for process in processes:
                process.join()

        print("Moving out CB & NMS classes...")
        zipFolder = os.path.join(out_location, "tozip")
        if not os.path.isdir(zipFolder):
            os.mkdir(zipFolder)

            shutil.copytree(replace_seperators(classes_location + "net@md_5"),
                            replace_seperators(zipFolder + "@net@md_5"))
            shutil.copytree(replace_seperators(classes_location + "net@minecraft@data"),
                            replace_seperators(zipFolder + "@net@minecraft@data"))
            shutil.copytree(replace_seperators(classes_location + "net@minecraft@util"),
                            replace_seperators(zipFolder + "@net@minecraft@util"))
            shutil.copytree(replace_seperators(classes_location + "net@minecraft@world"),
                            replace_seperators(zipFolder + "@net@minecraft@world"))
            shutil.copytree(replace_seperators(classes_location + "org@bukkit@craftbukkit"),
                            replace_seperators(zipFolder + "@org@bukkit@craftbukkit"))
            shutil.copytree(replace_seperators(classes_location + "com@mojang"),
                            replace_seperators(zipFolder + "@com@mojang"))

            processes = []

            for dir in os.listdir(replace_seperators(classes_location + "@net@minecraft@server")):
                processes.append(pc(target=copy_folder_to,
                                    args=[replace_seperators(classes_location + "net@minecraft@server@" + dir),
                                          replace_seperators(zipFolder + "@net@minecraft@server@" + dir)]))

            for process in processes:
                process.start()

            for process in processes:
                process.join()

        if os.path.isfile(replace_seperators(out_location + "@all-nms.jar")):
            os.remove(replace_seperators(out_location + "@all-nms.jar"))

        print("Creating Jar...")
        create_jar_archive(replace_seperators(out_location + "@tozip"),
                           replace_seperators(out_location + "@all-nms.jar"))
        print("Jar created.")

        self.updating = False
        self.jarPath = replace_seperators(out_location + "@all-nms.jar")


# Get all jars in the destination
def scan_directory(directory):
    archives = [y for x in os.walk(directory) for y in glob(os.path.join(x[0], '*.jar'))]
    return archives


# Create jar of a directory
def create_jar_archive(source_directory, archive_name):
    shutil.make_archive(archive_name, 'zip', source_directory)
    os.rename(archive_name + ".zip", archive_name)


def extract_jar_to(jar, destination):
    archive = zipfile.ZipFile(jar, 'r')
    try:
        archive.extractall(destination)
    except OSError:
        pass


# Downloading Artifacts
def download_artifact(data):
    r = requests.get(data["downloadUrl"], stream=True)

    file_location = os.path.join(str(script_location), "out", data["identifier"] + ".jar")
    if os.path.isfile(file_location):
        return

    block_size = 4024

    with open(file_location, 'wb+') as file:
        for chunk in r.iter_content(block_size):
            file.write(chunk)


def replace_seperators(path: str):
    return path.replace("@", os.path.sep)


def copy_folder_to(src, dest):
    shutil.copytree(src, dest)


def fetch_minecraft_versions():
    response = requests.get("https://repo.codemc.io/service/rest/v1/search?repository=nms&group=org.spigotmc")
    data = json.loads(response.text)["items"]

    to_process = []

    for item in data:
        for asset in item["assets"]:
            if asset["contentType"] == "application/java-archive" and "sources" not in asset[
                "downloadUrl"] and "shaded" not in asset["downloadUrl"] and "api" not in asset["downloadUrl"]:
                to_process.append({
                    "identifier": str(base64.b64encode(bytes(item["version"], encoding="utf-8"))[1:]).replace("\'", ""),
                    "downloadUrl": asset["downloadUrl"],
                    "version": item["version"]
                })

    return to_process


# Check if version exists
def needs_update():
    versions = fetch_minecraft_versions()

    found = 0
    for data in versions:
        file_location = os.path.join(str(script_location), "out", data["identifier"] + ".jar")
        if os.path.isfile(file_location):
            found += 1

    return found != len(versions)
