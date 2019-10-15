package com.example.cashmanager

import com.example.cashmanager.data.LoginDataSource
import com.example.cashmanager.data.Result
import com.example.cashmanager.data.model.ServerData
import org.junit.Test
import org.junit.Assert.*

class LoginTest {
    @Test
    fun testLoginSuccess() {
        val ipTest = "120.120.10.10"
        val pwdTest = "pwdTest"
        val res : Result<ServerData> = LoginDataSource.login(ipTest, pwdTest)
        assertEquals("SUCCESS", res)
    }

    @Test
    fun testLoginFail() {

    }
}