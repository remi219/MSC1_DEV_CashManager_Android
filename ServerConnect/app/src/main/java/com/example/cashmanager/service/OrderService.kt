package com.example.cashmanager.service

import com.example.cashmanager.data.dto.OrderDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderService {

    /**
     * Create an order
     */
    @POST("order/{userId}")
    fun createUserOrder(@Path("userId") userId : Int, @Body order : OrderDTO) : Call<OrderDTO>
}