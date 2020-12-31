package de.uriegel.firemusic

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPInputStream

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun httpGet(urlString: String): String {
    return withContext(Dispatchers.IO) {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("Accept-Encoding", "gzip")
        connection.responseCode
        val inStream = GZIPInputStream(connection.inputStream)
        return@withContext readStream(inStream)
    }
}

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun httpPost(urlString: String, psk: String, data: String): String {
    return withContext(Dispatchers.IO) {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("X-Auth-PSK", psk)
        connection.doInput = true
        val writer = BufferedWriter(OutputStreamWriter(connection.outputStream))
        writer.write(data)
        writer.close()
        connection.responseCode
        return@withContext readStream(connection.inputStream)
    }
}

private fun readStream(inString: InputStream): String {
    val response = StringBuffer()
    val reader = BufferedReader(InputStreamReader(inString))
    var line: String?
    while (reader.readLine().also { line = it } != null)
        response.append(line)
    reader.close()
    return response.toString()
}

