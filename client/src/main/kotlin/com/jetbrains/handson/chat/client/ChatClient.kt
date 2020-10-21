package com.jetbrains.handson.chat.client

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*

fun main() {
    val client = HttpClient {
        install(WebSockets)
    }
    runBlocking {
        client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/chat") {
            val messageOutputRoutine = launch {
                try {
                    for (message in incoming) {
                        message as? Frame.Text ?: continue
                        println(message.readText())
                    }
                } catch (e: Exception) {
                    println("Error while receiving: " + e.localizedMessage)
                }
            }

            val userInputRoutine = launch {
                while (true) {
                    val message = readLine() ?: ""
                    if (message.equals("exit", true)) return@launch
                    try {
                        outgoing.send(Frame.Text(message))
                    } catch (e: Exception) {
                        println("Error while sending: " + e.localizedMessage)
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