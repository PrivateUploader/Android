package com.troplo.privateuploader.data.model

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Keep
data class Friend(
    @field:Json(name = "status") val status: String,
    @field:Json(name = "user") val user: PartialUser?,
    @field:Json(name = "otherUser") val otherUser: PartialUser?,
    @field:Json(name = "userId") val userId: Int,
    @field:Json(name = "otherUserId") val otherUserId: Int,
    @field:Json(name = "createdAt") val createdAt: String,
    @field:Json(name = "updatedAt") val updatedAt: String,
    @field:Json(name = "id") val id: Int,
)

@JsonClass(generateAdapter = true)
@Keep
data class FriendRequest(
    @Json(name = "friend")
    val friend: Friend?,
    @Json(name = "id")
    val id: Int,
    @Json(name = "status")
    val status: String,
)

@JsonClass(generateAdapter = true)
@Keep
data class FriendNicknameRequest(
    @Json(name = "nickname")
    val nickname: String
)