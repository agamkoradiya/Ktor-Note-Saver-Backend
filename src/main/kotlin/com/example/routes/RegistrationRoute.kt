package com.example.routes

import com.example.data.model.User
import com.example.data.requests.AccountRequest
import com.example.data.responses.BasicResponse
import com.example.database.NotesDB
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.registrationRoute(db: NotesDB) {

    post("/register") {
        withContext(Dispatchers.IO) {
            val request = try {
                call.receive<AccountRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    BasicResponse(false, e.localizedMessage ?: "Received wrong parameter.")
                )
                return@withContext
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    BasicResponse(false, e.localizedMessage ?: "Something went wrong.")
                )
                return@withContext
            }

            val isUserExists = db.checkIfUserExists(request.email)

            if (!isUserExists) {
                if (db.registerUser(User(request.email, request.hashedPassword()))) {
                    call.respond(HttpStatusCode.OK, BasicResponse(true, "Successfully account created!"))
                } else {
                    call.respond(HttpStatusCode.OK, BasicResponse(false, "An unknown error occurred."))
                }
            } else {
                call.respond(HttpStatusCode.OK, BasicResponse(false, "A user with that MailId already exists"))
            }
        }
    }
}