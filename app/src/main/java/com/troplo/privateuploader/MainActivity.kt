package com.troplo.privateuploader

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.SocketHandler
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.TpuConfig
import com.troplo.privateuploader.data.model.User
import com.troplo.privateuploader.databinding.ActivityMainBinding
import com.troplo.privateuploader.ui.login.LoginActivity
import io.socket.client.Socket
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val token = SessionManager(this).fetchAuthToken()
        var user: User? = null
        if(token != null)  {
            TpuApi.retrofitService.getUser(token).enqueue(object : retrofit2.Callback<User> {
                override fun onResponse(call: retrofit2.Call<User>, response: retrofit2.Response<User>) {
                    if (response.body()?.username != null) {
                        println("User is logged in")
                        user = response.body()!!
                    } else {
                        println("User is not logged in")
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
                override fun onFailure(call: retrofit2.Call<User>, t: Throwable) {
                    println("User is not logged in")
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            })
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        SocketHandler.setSocket(token)
        SocketHandler.establishConnection()
        val tpuSocket = SocketHandler.getSocket()
        println(tpuSocket.connected())
        tpuSocket.on(Socket.EVENT_CONNECT) {
            println("Connected to TPU Server")
        }
        tpuSocket.on(Socket.EVENT_DISCONNECT) {
            println("Disconnected from TPU Server")
        }
        tpuSocket.emit("echo", "com.troplo.privateuploader.data.model.Message from TPUKt")
        println("Sent message to TPU Server")
        SocketHandler.listeners()

        // Initialize the notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel.
            val name = getString(R.string.channel_communications)
            val descriptionText = getString(R.string.channel_communications_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel("communications", name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_gallery, R.id.navigation_collections, R.id.navigation_settings))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        // set navigation_settings to user profile picture
        if(user != null) {
            // set the
        }
    }

}