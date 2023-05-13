package com.bcontrol.app.bcontrol

import android.util.Log
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
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
    var res = MyHttpResponse(666, "")
    try{
        val inputStream =
            BufferedReader(InputStreamReader(connection.inputStream, StandardCharsets.UTF_8))
        val responseBody = inputStream.use(BufferedReader::readText)
        Log.d("POST", "Post Response is ($responseCode): $responseBody")

        res = MyHttpResponse(responseCode, responseBody)
    } catch (e: IOException) {
        Log.e("ERROR", "Error reading response: ${e.message}")
        // Realiza las acciones necesarias en caso de error, por ejemplo:
        // res = MyHttpResponse(-1, "Error reading response")
    } finally {
        connection.disconnect()
    }
    return res
}

fun putJson(url: String, json: String): MyHttpResponse {
    Log.d("DEBUG", "Login with params $json")
    val urlObject = URL(url)
    val connection = urlObject.openConnection() as HttpURLConnection
    connection.requestMethod = "PUT"
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
    var res = MyHttpResponse(666, "")
    try{
        val inputStream =
            BufferedReader(InputStreamReader(connection.inputStream, StandardCharsets.UTF_8))
        val responseBody = inputStream.use(BufferedReader::readText)
        Log.d("POST", "Post Response is ($responseCode): $responseBody")

        res = MyHttpResponse(responseCode, responseBody)
    } catch (err:java.lang.Error) {
        Log.d("DEBUG", "Error was $err")
    }
    return res
}

fun getJson(url: String): MyHttpResponse {
    Log.d("DEBUG", "Get $url")
    val urlObject = URL(url)
    val connection = urlObject.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.setRequestProperty("Content-Type", "application/json; utf-8")
    connection.setRequestProperty("Accept", "application/json")

    val responseCode = connection.responseCode
    var res = MyHttpResponse(666, "")
    try{
        val inputStream = BufferedReader(InputStreamReader(connection.inputStream, StandardCharsets.UTF_8))
        val responseBody = inputStream.use(BufferedReader::readText)
        Log.d("GET","Get Response is: $responseBody")

        res = MyHttpResponse(responseCode, responseBody)
    } catch (err:java.lang.Error) {
        Log.d("DEBUG", "Error was $err")
    }
    return res
}