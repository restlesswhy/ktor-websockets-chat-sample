package com.jetbrains.handson.chat.server

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import java.util.*
import kotlin.random.Random

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    install(WebSockets)
    routing {
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket("/chat") {
            println("Adding user!")
            val thisConnection = Connection(this)
            connections += thisConnection


            try {
                send("You are connected! There are ${connections.count()} users here.")
                while (true) {
                    send(("Число загадано. У вас 7 попыток.\nВведите предполагаемое число: "))
                    val x = Random.nextInt(100)
                    var gameTry = 0
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()

                        if (receivedText.toInt() < x) {
                            send("Слишком мало")
                        } else if (receivedText.toInt() > x) {
                            send("Слишком много")
                        } else if (receivedText.toInt() == x) {
                            send("Правильно! Ты угадал за ${gameTry + 1} шагов!")
                            break
                        }
                        if (gameTry == 6) {
                            send("Ваши попытки закончились!")
                            break
                        }
                        gameTry++
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
}


//
//fun gameProcess(): Boolean {
//    val scan = Scanner(System.`in`)
//
//    while (gameTry <= 7) {
//        val clientInput = scan.nextInt()
//        if (clientInput > x) {
//            println("Слишком много! Попробуй ещё!")
//        } else if (clientInput < x) {
//            println("Слишком мало! Попробуй ещё!")
//        } else if (clientInput == x) {
//            return true
//        }
//        gameTry++
//    }
//    return false
//}
//
//fun main() {
//    println("Число загадано. У вас 7 попыток.\nВведите предполагаемое число: ")
//    val game = gameProcess()
//    if (game) {
//        println("Верно! Ты угадал за $gameTry ходов!\nИгра закончена.")
//    } else {
//        println("Прости, ты не угадал! Игра закончена!")
//    }
//}
//
