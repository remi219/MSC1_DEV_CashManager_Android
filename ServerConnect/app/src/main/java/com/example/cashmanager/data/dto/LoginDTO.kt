package com.example.cashmanager.data.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class LoginDTO(@SerializedName("username") val username: String,
               @SerializedName("password") val password: String
) : Serializable {
    private val id: Int? = null
}