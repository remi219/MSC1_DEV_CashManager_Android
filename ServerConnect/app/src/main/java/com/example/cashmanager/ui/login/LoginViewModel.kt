package com.example.cashmanager.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.example.cashmanager.R
import com.example.cashmanager.data.LoginRepository
import com.example.cashmanager.data.Result

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String, context: Context) {
        val result = loginRepository.login(username, password)
        val pref = context.applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val editor = pref.edit()

        if (result is Result.Success) {
            _loginResult.value =
                LoginResult(success = ServerDataView(displayUsername = result.data.username))

            editor.putString("jwt", result.data.jwt)
            editor.putInt("userId", result.data.id)
            editor.apply()
        } else {
            // Remove when login is working
            editor.putInt("userId", 1)
            editor.apply()
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUsernameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isUsernameValid(username: String): Boolean {
        //return Patterns.EMAIL_ADDRESS.matcher(username).matches()
        return true
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 4
    }
}
