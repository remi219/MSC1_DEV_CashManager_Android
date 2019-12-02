package com.example.cashmanager.data.model

data class ServerData(
    val username: String,
    val password: String
) {
    var jwt: String? = null
    var id: Int = 0
}
