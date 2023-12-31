package com.troplo.privateuploader.data.model

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Keep
data class LoginRequest(
    @field:Json(name = "email") val email: String,
    @field:Json(name = "password") val password: String,
    @field:Json(name = "code") val code: String,
)

@JsonClass(generateAdapter = true)
@Keep
data class RegisterRequest(
    @field:Json(name = "username") val username: String,
    @field:Json(name = "password") val password: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "inviteKey") val inviteKey: String = ""
)