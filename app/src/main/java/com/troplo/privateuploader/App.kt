package com.troplo.privateuploader

import android.app.Application
import androidx.startup.AppInitializer
import io.wax911.emojify.EmojiManager
import io.wax911.emojify.initializer.EmojiInitializer

class TpuApp : Application() {

    /**
     * Application scope bound emojiManager, you could keep a reference to this object in a
     * dependency injector framework like as a singleton in `Hilt`, `Dagger` or `Koin`
     */
    /*internal val emojiManager: EmojiManager by lazy {
        // should already be initialized if we haven't disabled initialization in manifest
        // see: https://developer.android.com/topic/libraries/app-startup#disable-individual
        AppInitializer.getInstance(this)
            .initializeComponent(EmojiInitializer::class.java)
    }*/
}