package com.troplo.privateuploader.data.model

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Keep
data class ChatAssociation(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "chatId") val chatId: Int,
    @field:Json(name = "userId") val userId: Int,
    @field:Json(name = "rank") val rank: String,
    @field:Json(name = "lastRead") val lastRead: Int,
    @field:Json(name = "notifications") val notifications: String,
    @field:Json(name = "legacyUserId") val legacyUserId: Int,
    @field:Json(name = "tpuUser") val tpuUser: User?,
    @field:Json(name = "legacyUser") val legacyUser: User?,
    @field:Json(name = "user") val user: PartialUser,
) {
    val isOwner = rank == "owner"
    val isAdmin = rank == "admin" || rank == "owner"
}

@JsonClass(generateAdapter = true)
@Keep
data class ReadReceiptEvent(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "chatId") val chatId: Int,
    @field:Json(name = "lastRead") val lastRead: Int,
    @field:Json(name = "user") val user: PartialUserSocket
)