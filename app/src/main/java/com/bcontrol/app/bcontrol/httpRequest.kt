package com.bcontrol.app.bcontrol

import android.util.Log
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

fun postJson(url: String, json: String): MyHttpResponse {
    Log.d("DEBUG", "Login with params $json")
    val urlObject = URL(url)
    val connection = urlObject.openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.setRequestProperty("Content-Type", "application/json; utf-8")
    connection.setRequestProperty("Accept", "application/json")
    connection.doOutput = true

    val requestBody = json.toByteArray(StandardCharsets.UTF_8)
    connection.setRequestProperty("Content-Length", requestBody.size.toString())

    val outputStream = DataOutputStream(connection.outputStream)
    outputStream.write(requestBody)
    outputStream.flush()
    outputStream.close()

    val responseCode = connection.responseCode
    val inputStream =
        BufferedReader(InputStreamReader(connection.inputStream, StandardCharsets.UTF_8))
    val responseBody = inputStream.use(BufferedReader::readText)
    Log.d("DEBUG", "Login response is ($responseCode): $responseBody")

    var res: MyHttpResponse = MyHttpResponse(responseCode, responseBody)
    return res
}