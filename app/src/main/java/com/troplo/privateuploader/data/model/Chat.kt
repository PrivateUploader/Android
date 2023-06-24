package com.troplo.privateuploader.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Chat(
  @field:Json(name = "id") val id: Int,
  @field:Json(name = "name") val name: String,
  @field:Json(name = "users") val users: List<User>,
  @field:Json(name = "recipient") val recipient: User?,
  @field:Json(name = "icon") val icon: String?,
  @field:Json(name = "type") val type: String?,
  @field:Json(name = "createdAt") val createdAt: String?,
  @field:Json(name = "updatedAt") val updatedAt: String?,
  @field:Json(name = "legacyUserId") val legacyUserId: String?,
  @field:Json(name = "user") val user: User?,
  @field:Json(name = "legacyUser") val legacyUser: User?,
  @field:Json(name = "association") val association: ChatAssociation?,
  @field:Json(name = "messages") val messages: List<Message>?,
  @field:Json(name = "unread") val unread: String?,
  @field:Json(name = "typers") val typers: List<Typing>?
)