package com.example.plugins

import io.ktor.auth.*
import io.ktor.util.*
import io.ktor.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.utils.Constants.CLAIM_EMAIL_ID
import com.example.utils.TokenManager
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureSecurity(tokenManager: TokenManager) {

    authentication {
        jwt {
            realm = tokenManager.myRealm
            verifier(
                tokenManager.verifier
            )
            validate { credential ->
                if (credential.payload.getClaim(CLAIM_EMAIL_ID).asString().isNotEmpty()) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

}