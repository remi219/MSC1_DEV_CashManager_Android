package com.example.cashmanager.service

import com.example.cashmanager.data.dto.ProductWrapperDTO
import com.example.cashmanager.data.model.Product
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ProductService {

    /**
     * Get the list of all available products
     */
    @GET("/products")
    fun availableProducts(): Call<List<Product>>

    /**
     * Post the list of products in the user cart
     */
    @POST("/products/{id}")
    fun addProducts(@Path("id") userId: Int, @Body products : List<ProductWrapperDTO> )
}