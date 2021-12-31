package com.example.di

import com.example.database.NotesDB
import com.example.database.NotesDBImpl
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.server.engine.*
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.environmentProperties
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val koinModule = module {
    single {
        KMongo.createClient()
            .coroutine
            .getDatabase("Notes_Database")
    }
    single<NotesDB> {
        NotesDBImpl(get())
    }

    single<HttpClient> {
        HttpClient(CIO) {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    }
}