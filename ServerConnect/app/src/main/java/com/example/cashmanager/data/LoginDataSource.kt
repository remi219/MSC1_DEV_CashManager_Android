package com.example.cashmanager.data

import android.util.Log
import com.example.cashmanager.data.model.ServerData
import java.io.IOException
import android.R.id.edit
import android.content.Context
import android.content.SharedPreferences
import com.example.cashmanager.service.LoginService
import com.example.cashmanager.service.ServiceBuilder


class LoginDataSource() {

    fun login(ip: String, password: String): Result<ServerData> {
        try {
            Log.d("ip/pwd = ", "$ip $password")
            val fakeServer = ServerData(java.util.UUID.randomUUID().toString(), "fake_password")
            return Result.Success(fakeServer)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
//        val pref = context.applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
//        val editor = pref.edit()
//
//        editor.remove("jwt")
//        editor.apply()
    }
}

