package com.jetbrains.handson.chat.server

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.routing.Route
import io.ktor.websocket.webSocket
import java.util.*


fun Route.chatRoute() {
    val connections = Collections.synchronizedSet(LinkedHashSet<Connection>())

    webSocket("/chat") {
        println("Adding user!")
        val thisConnection = Connection(this)
        connections += thisConnection
        try {
            outgoing.send(Frame.Text("You are connected! There are ${connections.count()} users here."))
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                val textWithUsername = "[${thisConnection.name}]: $receivedText"
                connections.forEach {
                    it.session.outgoing.send(Frame.Text(textWithUsername))
                }
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        } finally {
            println("Removing $thisConnection!")
            connections -= thisConnection
        }
    }
}

