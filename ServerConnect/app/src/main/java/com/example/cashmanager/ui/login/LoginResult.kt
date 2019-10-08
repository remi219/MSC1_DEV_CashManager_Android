package com.example.cashmanager.ui.login

/**
 * Authentication result : success (server details) or error message.
 */
data class LoginResult(
    val success: ServerDataView? = null,
    val error: Int? = null
)
