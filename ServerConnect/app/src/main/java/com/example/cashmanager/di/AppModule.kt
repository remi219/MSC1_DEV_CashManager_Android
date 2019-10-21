package com.example.cashmanager.di

import com.example.cashmanager.service.OrderService
import com.example.cashmanager.service.PaymentService
import com.example.cashmanager.service.ProductService
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private val baseURl = "http://localhost:4242"
private val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(baseURl)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

private val productAPI = retrofit.create(ProductService::class.java)
private val paymentAPI = retrofit.create(PaymentService::class.java)
private val orderAPI = retrofit.create(OrderService::class.java)

val appModule = module {
    single {productAPI}
    single {paymentAPI}
    single {orderAPI}
}