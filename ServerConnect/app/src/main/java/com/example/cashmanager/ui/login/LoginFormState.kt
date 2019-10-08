package com.example.cashmanager.ui.login

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val ipError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)
