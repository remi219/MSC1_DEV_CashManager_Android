package com.example.cashmanager.service

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(val accessToken: String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = ""
        val request =
            chain.request().newBuilder().addHeader("Authorization", "Bearer $accessToken").build()
        return chain.proceed(request)
    }
}