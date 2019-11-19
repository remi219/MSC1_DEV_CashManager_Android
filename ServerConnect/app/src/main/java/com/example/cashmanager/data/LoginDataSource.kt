package com.example.cashmanager.data

import android.util.Log
import com.example.cashmanager.data.model.ServerData
import java.io.IOException
import com.example.cashmanager.data.dto.LoginDTO
import com.example.cashmanager.service.LoginService
import com.example.cashmanager.service.ServiceBuilder
import retrofit2.Response


class LoginDataSource {

    fun login(username: String, password: String): Result<ServerData> {
        try {
            val serverData = ServerData(username, password)
            val loginAPI = ServiceBuilder.createService(LoginService::class.java)
            val response: Response<String>  = loginAPI.login(LoginDTO(username, password)).execute()
            serverData.jwt = response.body()
            Log.d("username/pwd = ", "$username $password")
            return Result.Success(serverData)
        } catch (e: Throwable) {
            e.printStackTrace()
            return Result.Error(IOException("Error logging in", e))
        }
    }

    /**
     * Logout by clearing the jwt from the shared preferences
     * We need to get the context somehow
     */
    fun logout() {
//        val pref = context.applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
//        val editor = pref.edit()
//        editor.remove("jwt")
//        editor.apply()
    }
}
