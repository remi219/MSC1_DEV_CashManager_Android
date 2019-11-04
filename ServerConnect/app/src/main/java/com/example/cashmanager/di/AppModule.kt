package com.example.cashmanager.di

import com.example.cashmanager.service.*
import okhttp3.Interceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.Response
import java.util.concurrent.TimeUnit


private const val baseURl = "http://10.0.2.2:8080"

private val okHttpClient = OkHttpClient.Builder()
    .readTimeout(3, TimeUnit.SECONDS)
    .connectTimeout(3, TimeUnit.SECONDS)
    .build()

private val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(baseURl)
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build()

private val statusAPI = retrofit.create(StatusService::class.java)
private val productAPI = retrofit.create(ProductService::class.java)
private val paymentAPI = retrofit.create(PaymentService::class.java)
private val orderAPI = retrofit.create(OrderService::class.java)
private val loginAPI = retrofit.create(LoginService::class.java)

val appModule = module {
    single {statusAPI}
    single {productAPI}
    single {paymentAPI}
    single {orderAPI}
    single {loginAPI}
}