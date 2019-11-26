package com.example.cashmanager.service

import com.example.cashmanager.data.dto.OrderDTO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderService {

    /**
     * Create an order for the given userId
     */
    @POST("order/{userId}")
    fun createUserOrder(@Path("userId") userId : Int, @Body order : OrderDTO) : Call<OrderDTO>

    @DELETE("order/{id}")
    fun deleteOrder(@Path("id") id : Int) : Call<ResponseBody>
}