package com.example.routes

import com.example.data.model.Note
import com.example.data.requests.AddOwnerRequest
import com.example.data.requests.DeleteNoteRequest
import com.example.data.responses.BasicResponse
import com.example.data.responses.NoteResponse
import com.example.database.NotesDB
import com.example.notification.OneSignalService
import com.example.notification.model.Notification
import com.example.notification.model.NotificationMessage
import com.example.utils.Constants.CLAIM_EMAIL_ID
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.noteRoutes(db: NotesDB, service: OneSignalService) {

    route("/getNotes") {
        authenticate {
            get {
                val principal = call.principal<JWTPrincipal>()
                val email = principal!!.payload.getClaim(CLAIM_EMAIL_ID).asString()
                try {
                    val notes = db.getNotesForUser(email)
                    call.respond(HttpStatusCode.OK, NoteResponse(true, notes))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, NoteResponse(false))
                }
            }
        }
    }

    route("/addNote") {
        authenticate {
            post {
                val note = try {
                    call.receive<Note>()
                } catch (e: ContentTransformationException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        BasicResponse(false, e.localizedMessage ?: "Received wrong parameter.")
                    )
                    return@post
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        BasicResponse(false, e.localizedMessage ?: "Something went wrong.")
                    )
                    return@post
                }

                if (db.saveNote(note)) {
                    call.respond(
                        HttpStatusCode.OK, BasicResponse(true, "Note saved successfully.")
                    )
                } else {
                    call.respond(
                        HttpStatusCode.Conflict, BasicResponse(false, "Something went wrong.")
                    )
                }
            }
        }
    }

    route("/deleteNote") {
        authenticate {
            delete {
                val principal = call.principal<JWTPrincipal>()
                val email = principal!!.payload.getClaim(CLAIM_EMAIL_ID).asString()
                val request = try {
                    call.receive<DeleteNoteRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        BasicResponse(false, e.localizedMessage ?: "Received wrong parameter.")
                    )
                    return@delete
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        BasicResponse(false, e.localizedMessage ?: "Something went wrong.")
                    )
                    return@delete
                }

                if (db.deleteNoteForUser(email, request.noteId)) {
                    call.respond(
                        HttpStatusCode.OK, BasicResponse(true, "Note deleted successfully.")
                    )
                } else {
                    call.respond(
                        HttpStatusCode.Conflict, BasicResponse(false, "Something went wrong.")
                    )
                }
            }
        }
    }

    route("/addOwnerToNote") {
        authenticate {
            post {

                val principal = call.principal<JWTPrincipal>()
                val email = principal!!.payload.getClaim(CLAIM_EMAIL_ID).asString()

                val request = try {
                    call.receive<AddOwnerRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        BasicResponse(false, e.localizedMessage ?: "Received wrong parameter.")
                    )
                    return@post
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        BasicResponse(false, e.localizedMessage ?: "Something went wrong.")
                    )
                    return@post
                }
                if (!db.checkIfUserExists(request.owner)) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        BasicResponse(false, "No user with this E-Mail exists.")
                    )
                    return@post
                }
                if (db.isOwnerOfNote(request.noteID, request.owner)) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        BasicResponse(false, "This user is already an owner of this note.")
                    )
                    return@post
                }
                if (db.addOwnerToNote(request.noteID, request.owner)) {

                    if (request.wantToSendNotification) {
                        val notification = Notification(
                            listOf(request.owner),
                            "push",
                            NotificationMessage(en = "New Note Received"),
                            NotificationMessage(en = "$email sent you note"),
                            OneSignalService.ONESIGNAL_APP_ID
                        )

                        if (service.sendNotification(notification)) {
                            call.respond(
                                HttpStatusCode.OK,
                                BasicResponse(true, "${request.owner} can now see this note & Notification sent.")
                            )
                        } else {
                            call.respond(
                                HttpStatusCode.OK,
                                BasicResponse(true, "${request.owner} can now see this note.")
                            )
                        }
                    }

                } else {
                    call.respond(HttpStatusCode.Conflict)
                }
            }
        }
    }

}