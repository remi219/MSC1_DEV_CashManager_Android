package com.example.cashmanager.data

import android.util.Log
import com.example.cashmanager.data.model.ServerData
import java.io.IOException

class LoginDataSource {

    fun login(ip: String, password: String): Result<ServerData> {
        try {
            // TODO: server authentication
            Log.d("ip/pwd = ", "$ip $password")
            val fakeServer = ServerData(java.util.UUID.randomUUID().toString(), "fake_password")
            return Result.Success(fakeServer)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}

