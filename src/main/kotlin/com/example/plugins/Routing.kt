package com.example.plugins

import com.example.database.NotesDB
import com.example.notification.model.OneSignalServiceImpl
import com.example.routes.logInRoute
import com.example.routes.noteRoutes
import com.example.routes.registrationRoute
import com.example.utils.TokenManager
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.response.*
import org.koin.ktor.ext.inject

fun Application.configureRouting(tokenManager: TokenManager) {

    val notesDB by inject<NotesDB>()

    val httpClient by inject<HttpClient>()
    val apiKey = environment.config.property("onesignal.api_key").getString()
    val service = OneSignalServiceImpl(httpClient, apiKey)

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        registrationRoute(notesDB)
        logInRoute(notesDB, tokenManager)
        noteRoutes(notesDB,service)
    }
}
