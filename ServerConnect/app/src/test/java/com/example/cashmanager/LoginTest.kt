package com.example.cashmanager

import com.example.cashmanager.data.LoginDataSource
import com.example.cashmanager.data.Result
import com.example.cashmanager.data.model.ServerData
import org.junit.Test
import org.junit.Assert.*

class LoginTest {
<<<<<<< HEAD

    private val lds : LoginDataSource = LoginDataSource()

=======
>>>>>>> 998a948474d01905fdd32284a3b40f90fd7a76e8
    @Test
    fun testLoginSuccess() {
        val ipTest = "120.120.10.10"
        val pwdTest = "pwdTest"
<<<<<<< HEAD
        val res : Result<ServerData> = lds.login(ipTest, pwdTest)
=======
        val res : Result<ServerData> = LoginDataSource.login(ipTest, pwdTest)
>>>>>>> 998a948474d01905fdd32284a3b40f90fd7a76e8
        assertEquals("SUCCESS", res)
    }

    @Test
<<<<<<< HEAD
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
=======
    fun testLoginFail() {

>>>>>>> 998a948474d01905fdd32284a3b40f90fd7a76e8
    }
}