package com.example.cashmanager

import com.example.cashmanager.data.LoginDataSource
import com.example.cashmanager.data.Result
import com.example.cashmanager.data.model.ServerData
import org.junit.Test
import org.junit.Assert.*

class LoginTest {

    private val lds : LoginDataSource = LoginDataSource()

    @Test
    fun testLoginSuccess() {
        val ipTest = "120.120.10.10"
        val pwdTest = "pwdTest"
        val res : Result<ServerData> = lds.login(ipTest, pwdTest)
        assertEquals("SUCCESS", res)
    }

    @Test
    fun testLoginFailWrongIp() {
        val ipTest = "wrongip"
        val pwdTest = "pwdTest"
        val res : Result<ServerData> = lds.login(ipTest, pwdTest)
        assertEquals("Error logging in", res)
    }

    @Test
    fun testLoginFailToShortPwd() {
        val ipTest = "120.120.10.10"
        val pwdTest = "123"
        val res : Result<ServerData> = lds.login(ipTest, pwdTest)
        assertEquals("ERROR", res)
    }
}