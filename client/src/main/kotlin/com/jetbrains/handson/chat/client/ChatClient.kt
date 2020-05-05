package com.jetbrains.handson.chat.client

import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    val client = HttpClient {
        install(WebSockets)
    }
    runBlocking {
        client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/chat") {
            val messageOutputRoutine = GlobalScope.launch {
                try {
                    for (message in incoming) {
                        message as? Frame.Text ?: continue
                        println(message.readText())
                    }
                } catch (e: Exception) {
                    println(e.localizedMessage)
                }
            }

            val userInputRoutine = GlobalScope.launch {
                while (true) {
                    val message = readLine() ?: ""
                    if (message.equals("exit", true)) return@launch
                    try {
                        outgoing.send(Frame.Text(message))
                    } catch (e: Exception) {
                        println(e.localizedMessage)
                        return@launch
                    }
                }
            }
            userInputRoutine.join()
            messageOutputRoutine.cancelAndJoin()
        }
    }
    client.close()
    println("Connection closed. Goodbye!")
}