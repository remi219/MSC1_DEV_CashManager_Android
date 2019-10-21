package com.example.cashmanager.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface PaymentService {

    @GET("/payments/cheque")
    fun postChequePayment() : Call<ResponseBody>

    @GET("/payments/nfc")
    fun postNFCPayment() : Call<ResponseBody>
}