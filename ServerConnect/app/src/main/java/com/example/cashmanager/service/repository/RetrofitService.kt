package com.example.cashmanager.service.repository

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitService {

//    lateinit var service

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost:4242")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ProductService::class.java)
    }
}