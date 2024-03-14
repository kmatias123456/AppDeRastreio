package com.example.androidessncial.server

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

object InterfaceObjectRetrofit {

    /*
    * Endereco Do Servidor
    */
    private var BASE_URL = "http://192.168.10.150:80/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val apiServiceRetrofit: ApiServiceRetrofit = retrofit.create(
        ApiServiceRetrofit::class.java
    )
}

interface ApiServiceRetrofit {
    /*
    * Caminho Do Servidor
    */
    @POST("arrayUm.php")
    fun postLocation(@Body request: ApiSendRetrofit) : Call<ApiResponseRetrofit>
}