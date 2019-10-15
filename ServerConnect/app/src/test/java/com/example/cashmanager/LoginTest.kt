package com.example.cashmanager

import com.example.cashmanager.data.LoginDataSource
import org.junit.Test
import org.junit.Assert.*

class LoginTest {
    @Test
    fun testLoginSuccess() {
        val ipTest = "120.120.10.10"
        val pwdTest = "pwdTest"
        val loginStatus = LoginDataSource.login(ipTest, pwdTest)
        assertEquals("SUCCESS", loginStatus)
    }

    @Test
    fun testLoginFail() {

    }
}