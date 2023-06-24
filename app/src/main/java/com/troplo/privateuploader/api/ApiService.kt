package com.troplo.privateuploader.api

import com.troplo.privateuploader.data.model.Chat
import com.troplo.privateuploader.data.model.Gallery
import com.troplo.privateuploader.data.model.LoginRequest
import com.troplo.privateuploader.data.model.LoginResponse
import com.troplo.privateuploader.data.model.User
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

private const val BASE_URL = "http://192.168.0.12:34582/api/v3/"
val client = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    })
    .build()


val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()


interface TpuApiService {
    @GET("chats")
    fun getChats(
        @Header("Authorization") token: String
    ): Call<List<Chat>>

    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>


    @GET("user")
    fun getUser(
        @Header("Authorization") token: String
    ): Call<User>

    @GET("gallery")
    fun getGallery(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("search") search: String = "",
        @Query("textMetadata") textMetadata: Boolean = true,
        @Query("filter") filter: String = "all",
        @Query("sort") sort: String = "\"newest\""
    ): Call<Gallery>
}

object TpuApi {
    val retrofitService: TpuApiService by lazy {
        retrofit.create(TpuApiService::class.java)
    }
}