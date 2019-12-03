package com.example.cashmanager.service

import com.example.cashmanager.data.dto.CustomerDTO
import com.example.cashmanager.data.dto.ProductQuantityDTO
import com.example.cashmanager.data.dto.ProductWrapperDTO
import com.example.cashmanager.data.dto.ProductWrapperListDTO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface CustomerService {

    /**
     * Get data about the given user
     * @param id user id
     */
    @GET("/customer/{id}")
    fun getCustomer(@Path("id") id: Int) : Call<CustomerDTO>

    /**
     * Get saved customer's cart from the server
     */
    @GET("/customer/{id}/cart")
    fun getCart(@Path("id") id: Int) : Call<List<ProductQuantityDTO>>

    /**
     * Set the list of products to the customer cart
     * Overwrite the existing cart
     */
    @POST("/customer/{id}/cart")
    fun setCart(@Path("id") id: Int, @Body products: ProductWrapperListDTO) : Call<ResponseBody>

    /**
     * Clear the customer cart
     */
    @DELETE("/customer/{id}/cart")
    fun resetCart(@Path("id") id: Int) : Call<ResponseBody>
}