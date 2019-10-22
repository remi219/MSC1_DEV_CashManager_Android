package com.example.cashmanager.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface StatusService {

    /**
     * Test to check the status of the API
     */
    @GET("/status")
    fun connectionStatus() : Call<ResponseBody>
}