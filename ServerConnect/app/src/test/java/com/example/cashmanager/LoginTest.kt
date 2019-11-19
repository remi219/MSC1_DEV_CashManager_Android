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
        val username = "user@test.com"
        val pwdTest = "pwdTest"

        val res : Result<ServerData> = lds.login(username, pwdTest)

        assertNotNull(res)
    }

    @Test
    fun testLoginFailWrongIp() {
        val username = "invalid_username"
        val pwdTest = "pwdTest"
        val res : Result<ServerData> = lds.login(username, pwdTest)
        assertNotNull(res)
    }

    @Test
    fun testLoginFailToShortPwd() {
        val username = "user@test.com"
        val pwdTest = "123"
        val res : Result<ServerData> = lds.login(username, pwdTest)
        assertNotNull(res)
    }
}