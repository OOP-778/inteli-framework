rootProject.name = "inteli-framework"
gradle.rootProject {
    beforeEvaluate {
        // Download lib if not present
        val file = File("lib")
        if (!file.exists() || file.listFiles().isEmpty()) {
            file.mkdirs()
            println("== NMS FAT JAR NOT FOUND... DOWNLOADING ==")
            downloadFile("https://nms.honeybeedev.com/", File(file, "fat-nms.jar"))
        }
    }
}

fun downloadFile(url: String, out: java.io.File) {
    var readableChannelForHttpResponseBody: java.nio.channels.ReadableByteChannel? = null
    var fileChannelForDownloadedFile: java.nio.channels.FileChannel? = null
    try {
        // Define server endpoint
        val robotsUrl: java.net.URL = java.net.URL(url)
        val urlConnection: java.net.HttpURLConnection = robotsUrl.openConnection() as java.net.HttpURLConnection
        urlConnection.requestMethod = "GET"
        urlConnection.doInput = true
        urlConnection.addRequestProperty(
            "User-Agent",
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)"
        )

        // Get a readable channel from url connection
        readableChannelForHttpResponseBody = java.nio.channels.Channels.newChannel(urlConnection.inputStream)

        // Create the file channel to save file
        val fosForDownloadedFile = java.io.FileOutputStream(out)
        fileChannelForDownloadedFile = fosForDownloadedFile.channel

        // Save the body of the HTTP response to local file
        fileChannelForDownloadedFile.transferFrom(readableChannelForHttpResponseBody, 0, Long.MAX_VALUE)
    } catch (ioException: java.io.IOException) {
        println("IOException occurred while contacting server.")
        ioException.printStackTrace()
    } finally {
        if (readableChannelForHttpResponseBody != null) {
            try {
                readableChannelForHttpResponseBody.close()
            } catch (ioe: java.io.IOException) {
                println("Error while closing response body channel")
            }
        }
        if (fileChannelForDownloadedFile != null) {
            try {
                fileChannelForDownloadedFile.close()
            } catch (ioe: java.io.IOException) {
                println("Error while closing file channel for downloaded file")
            }
        }
    }
}

include("commons")
include("adapters")
include("platform")
include("command")

include("message")
include("message:config")
project(":message:config").name = "message-config"

include("config")
include("config:node")
include("config:property")
include("config:file")
project(":config:node").name = "config-node"
project(":config:property").name = "config-property"
project(":config:file").name = "config-file"
include("animation")

include("dependency")
include("dependency:common")
project(":dependency:common").name = "dependency-common"

include("task")
include("bukkit")
include("event")

include("bukkit:command")
project(":bukkit:command").name = "bukkit-command"

include("bukkit:event")
project(":bukkit:event").name = "bukkit-event"

include("bukkit:task")
project(":bukkit:task").name = "bukkit-task"

include("bukkit:entity")
project(":bukkit:entity").name = "bukkit-entity"

include("bukkit:packet")
project(":bukkit:packet").name = "bukkit-packet"

include("bukkit:scoreboard")
project(":bukkit:scoreboard").name = "bukkit-scoreboard"

include("bukkit:bukkit-entity:armorstand")
project(":bukkit:bukkit-entity:armorstand").name = "bukkit-entity-armorstand"

include("bukkit:bukkit-entity:commons")
project(":bukkit:bukkit-entity:commons").name = "bukkit-entity-commons"

include("bukkit:bukkit-entity:tracker")
project(":bukkit:bukkit-entity:tracker").name = "bukkit-entity-tracker"

include("bukkit:bukkit-entity:hologram")
project(":bukkit:bukkit-entity:hologram").name = "bukkit-entity-hologram"

include("bukkit:bukkit-entity:bukkit-entity-hologram:animation")
project(":bukkit:bukkit-entity:bukkit-entity-hologram:animation").name = "bukkit-entity-hologram-animation"

include("bukkit:nbt")
project(":bukkit:nbt").name = "bukkit-nbt"

include("bukkit:message")
project(":bukkit:message").name = "bukkit-message"

include("bukkit:test")
project(":bukkit:test").name = "bukkit-test"

include("bukkit:item")
project(":bukkit:item").name = "bukkit-item"

include("menu")
include("menu:menu-config")
include("menu:navigator")
include("menu:navigator:config")
project(":menu:navigator").name = "menu-navigator"
project(":menu:navigator:config").name = "menu-navigator-config"
