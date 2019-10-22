package com.example.cashmanager.service

import com.example.cashmanager.data.dto.PaymentCardDTO
import com.example.cashmanager.data.dto.PaymentChequeDTO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.POST

interface PaymentService {

    @POST("/payement/cheque")
    fun postChequePayment(data: PaymentChequeDTO) : Call<ResponseBody>

    @POST("/payement/nfc")
    fun postNFCPayment(data: PaymentCardDTO) : Call<ResponseBody>
}