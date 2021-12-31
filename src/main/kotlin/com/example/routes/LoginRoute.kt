package com.example.routes

import com.example.data.requests.AccountRequest
import com.example.data.responses.BasicResponse
import com.example.data.responses.LogInResponse
import com.example.database.NotesDB
import com.example.utils.TokenManager
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.logInRoute(db: NotesDB, tokenManager: TokenManager) {

    route("/login") {
        post {

            withContext(Dispatchers.IO) {

                val request = try {
                    call.receive<AccountRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        LogInResponse(false, e.localizedMessage ?: "Received wrong parameter.", null)
                    )
                    return@withContext
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        LogInResponse(false, e.localizedMessage ?: "Something went wrong.", null)
                    )
                    return@withContext
                }

                if (db.checkPasswordForEmail(request.email, request.password)) {
                    val token = tokenManager.generateJWTToken(request.email)
                    call.respond(LogInResponse(true, "User logged in successfully", token))
                } else {
                    call.respond(LogInResponse(false, "Invalid username or password.", null))
                }
            }
        }
    }

}