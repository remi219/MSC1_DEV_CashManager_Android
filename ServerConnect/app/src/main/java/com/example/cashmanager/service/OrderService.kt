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
     * @param userId user id
     */
    @POST("order/{userId}")
    fun createUserOrder(@Path("userId") userId : Int, @Body order : OrderDTO) : Call<OrderDTO>

    /**
     * Delete the selected order
     * @param id Order id
     */
    @DELETE("order/deleteOrder/{id}")
    fun deleteOrder(@Path("id") id : Int) : Call<ResponseBody>
}