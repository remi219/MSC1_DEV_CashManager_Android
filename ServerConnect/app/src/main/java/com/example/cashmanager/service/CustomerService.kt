package com.example.cashmanager.service

import com.example.cashmanager.data.dto.CustomerDTO
import com.example.cashmanager.data.dto.ProductWrapperDTO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface CustomerService {

    /**
     * Get data about the given user
     * @param id user id
     */
    @GET("customer/{id}")
    fun getCustomer(@Path("id") id: Int) : Call<CustomerDTO>

    /**
     * Set the list of products to the customer cart
     * Overwrite the existing cart
     */
    @POST("customer/{id}/cart")
    fun setCart(@Path("id") id: Int, @Body products: List<ProductWrapperDTO>) : Call<ResponseBody>

    /**
     * Clear the customer cart
     */
    @DELETE("customer/{id}/cart")
    fun resetCart(@Path("id") id: Int) : Call<ResponseBody>
}