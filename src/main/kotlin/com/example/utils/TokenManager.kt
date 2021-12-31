package com.example.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.utils.Constants.CLAIM_EMAIL_ID
import io.ktor.application.*

class TokenManager(environment: ApplicationEnvironment) {

    private val secret = environment.config.property("jwt.secret").getString()
    private val issuer = environment.config.property("jwt.issuer").getString()
    private val audience = environment.config.property("jwt.audience").getString()
    val myRealm = environment.config.property("jwt.realm").getString()
    private val algorithm = Algorithm.HMAC256(secret)

    fun generateJWTToken(email: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim(CLAIM_EMAIL_ID, email)
            .sign(algorithm)
    }

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

}