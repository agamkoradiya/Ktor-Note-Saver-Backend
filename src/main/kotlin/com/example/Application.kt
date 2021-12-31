package com.example

import com.example.di.koinModule
import io.ktor.application.*
import com.example.plugins.*
import com.example.utils.TokenManager
import org.koin.core.logger.Level
import org.koin.ktor.ext.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {

    install(Koin) {
        slf4jLogger(Level.ERROR)
        modules(koinModule)
    }

    val tokenManager = TokenManager(environment)

    configureSerialization()
    configureSecurity(tokenManager)
    configureRouting(tokenManager)
}
