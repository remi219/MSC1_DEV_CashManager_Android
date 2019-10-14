package com.example.cashmanager.service.repository

import com.example.cashmanager.data.model.Product
import retrofit2.Call
import retrofit2.http.GET

interface ProductService {

    @GET("/products")
    fun availableProducts(): Call<List<Product>>
}