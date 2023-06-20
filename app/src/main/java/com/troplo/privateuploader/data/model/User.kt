package com.troplo.privateuploader.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
  @field:Json(name = "id") val id: String,
  @field:Json(name = "username") val username: String,
  @field:Json(name = "avatar") val avatar: String
)