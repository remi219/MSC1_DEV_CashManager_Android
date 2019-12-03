package com.example.cashmanager.service

import android.text.TextUtils
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Build service with authtoken or not
 */
class ServiceBuilder {
    companion object {
        // Url when using the emulator
        // private val baseUrl : String = "http://10.0.2.2:8080"
        // Url of the computer on the local network
        private val baseUrl : String = "http://192.168.43.212:8080"

        private val httpClient = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .connectTimeout(3, TimeUnit.SECONDS)

        private val builder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())


        /**
         * Create a service without jwt token
         * @param serviceClass: service to create
         */
        fun <T> createService(serviceClass: Class<T>): T {
            return createService(serviceClass, null)
        }

        /**
         * Create a service with jwt token
         * @param serviceClass: service to create
         * @param authToken: authentication token
         */
        fun <T> createService(serviceClass: Class<T>, authToken: String?): T {
            if (!TextUtils.isEmpty(authToken)) {
                val interceptor = AuthInterceptor(authToken)

                if (!httpClient.interceptors().contains(interceptor)) {
                    httpClient.addInterceptor(interceptor)
                }
            }
            val retrofit = builder.client(httpClient.build()).build()
            return retrofit.create(serviceClass)
        }
    }
}