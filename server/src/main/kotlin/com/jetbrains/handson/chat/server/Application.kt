package com.jetbrains.handson.chat.server

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.routing.routing
import io.ktor.websocket.WebSockets


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    install(WebSockets)
    routing {
        chatRoute()
    }
}
