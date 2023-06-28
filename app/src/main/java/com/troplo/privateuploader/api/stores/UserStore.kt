package com.troplo.privateuploader.api.stores

import android.content.Context
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.SocketHandler
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.data.model.SettingsPayload
import com.troplo.privateuploader.data.model.StatusPayload
import com.troplo.privateuploader.data.model.User
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URISyntaxException


object UserStore {
    var user: MutableStateFlow<User?> = MutableStateFlow(null)
    var cachedUsers: MutableStateFlow<List<User>> = MutableStateFlow(listOf())

    fun initializeUser(context: Context) {
        try {
            if (SessionManager(context).getUserCache() != null) {
                println("User cache ${SessionManager(context).getUserCache()}")
                user.value = SessionManager(context).getUserCache()
            }
            CoroutineScope(
                Dispatchers.IO
            ).launch {
                user.value = TpuApi.retrofitService.getUser().execute().body()
                SessionManager(context).setUserCache(user.value)
                FriendStore.initializeFriends()
            }

            val socket = SocketHandler.getSocket()

            socket?.on("userSettingsUpdate") {
                val jsonArray = it[0] as JSONObject
                val payload = jsonArray.toString()
                val settings = SocketHandler.gson.fromJson(payload, SettingsPayload::class.java)
                user.value = user.value?.copy(
                    description = settings.description ?: user.value?.description ?: "",
                    storedStatus = settings.storedStatus ?: user.value?.storedStatus ?: "",
                    email = settings.email ?: user.value?.email ?: "",
                    discordPrecache = settings.discordPrecache ?: user.value?.discordPrecache ?: false,
                    itemsPerPage = settings.itemsPerPage ?: user.value?.itemsPerPage ?: 12,
                    language = settings.language ?: user.value?.language ?: "en",
                    excludedCollections = settings.excludedCollections ?: user.value?.excludedCollections ?: listOf(),
                    insights = settings.insights ?: user.value?.insights ?: "friends"
                )
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun getUser(): User? {
        return user.value
    }

    fun resetUser() {
        user.value = null
    }

    fun getUserProfile(username: String): Deferred<User?> {
        // TODO: Fix crash
        /*val cachedUser = cachedUsers.value.find { it.username == username }
        if (cachedUser != null) {
            return CompletableDeferred(cachedUser)
        }*/
        return CoroutineScope(Dispatchers.IO).async {
            val response = TpuApi.retrofitService.getUserProfile(username).execute()
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    cachedUsers.value = cachedUsers.value.plus(user)
                    return@async user
                }
            }
            return@async null
        }
    }
}