package com.example.cashmanager.service

import com.example.cashmanager.data.dto.CustomerDTO
import com.example.cashmanager.data.dto.LoginDTO
import com.example.cashmanager.data.dto.UserDTO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {

    /**
     * Login on the REST API
     */
    @POST("/login")
    fun login(@Body login: LoginDTO) : Call<CustomerDTO>

    @POST("/customer")
    fun register(@Body customer: CustomerDTO) : Call<CustomerDTO>
}