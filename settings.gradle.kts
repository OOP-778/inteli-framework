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

include("scoreboard")
include("config")
include("commons")
include("hologram")
include("test-plugin")
include("adapters")
include("task")
include("item")
include("plugin")
include("command")
include("command-bukkit")
include("recipe")
include("packet-injector")
include("message")
include("menu")
include("task-bukkit")
