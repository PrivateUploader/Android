package com.troplo.privateuploader.api

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.troplo.privateuploader.BuildConfig
import com.troplo.privateuploader.R
import com.troplo.privateuploader.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow

enum class ThemeOption {
    Dark, Light, System, AMOLED
}

class SessionManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    val theme = MutableStateFlow(getTheme())

    companion object {
        const val USER_TOKEN = "token"
    }

    fun saveAuthToken(token: String?) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun getLastChatId(): Int {
        return prefs.getInt("lastChatId", 0)
    }

    fun setFolder(folder: String) {
        val editor = prefs.edit()
        editor.putString("folder", folder)
        editor.apply()
    }

    fun setLastChatId(id: Int) {
        val editor = prefs.edit()
        editor.putInt("lastChatId", id)
        editor.apply()
    }

    fun setUserCache(user: User?) {
        val editor = prefs.edit()
        editor.putString("user", Gson().toJson(user))
        editor.apply()
    }

    fun getUserCache(): User? {
        // Can crash if the backend schema has changed, this will be refreshed with new data provided the app is updated
        return try {
            val user = prefs.getString("user", null)
            if (user != null) {
                Gson().fromJson(user, User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getInstanceURL(): String {
        return prefs.getString("instanceURL", BuildConfig.SERVER_URL) ?: BuildConfig.SERVER_URL
    }

    fun setInstanceURL(url: String) {
        val editor = prefs.edit()
        editor.putString("instanceURL", url)
        editor.apply()
    }

    // Cached FCM token to avoid re-registering
    fun getFCMToken(): String? {
        return prefs.getString("fcmToken", null)
    }

    fun setFCMToken(token: String?) {
        val editor = prefs.edit()
        editor.putString("fcmToken", token)
        editor.apply()
    }

    fun getDebugMode(): Boolean {
        return prefs.getBoolean("debugMode", BuildConfig.DEBUG)
    }

    fun setDebugMode(debug: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean("debugMode", debug)
        editor.apply()
    }

    fun getTheme(): ThemeOption {
        val theme = prefs.getString("theme", ThemeOption.System.name)
        return if (theme == null) {
            ThemeOption.System
        } else {
            ThemeOption.valueOf(theme)
        }
    }

    fun setTheme(theme: ThemeOption) {
        val editor = prefs.edit()
        editor.putString("theme", theme.name)
        editor.apply()
        this.theme.value = theme
    }

    fun setColor(color: String) {
        val editor = prefs.edit()
        editor.putString("color", color)
        editor.apply()
    }

    fun getColor(): String? {
        return prefs.getString("color", null)
    }
}