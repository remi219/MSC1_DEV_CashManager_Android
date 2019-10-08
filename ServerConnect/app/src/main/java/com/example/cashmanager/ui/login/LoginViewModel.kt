package com.example.cashmanager.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.example.cashmanager.data.LoginRepository
import com.example.cashmanager.data.Result

import com.example.cashmanager.R

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(ip: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.login(ip, password)

        if (result is Result.Success) {
            _loginResult.value =
                LoginResult(success = ServerDataView(displayIp = result.data.ip))
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(ip: String, password: String) {
        if (!isIpValid(ip)) {
            _loginForm.value = LoginFormState(ipError = R.string.invalid_ip)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder ip validation check
    private fun isIpValid(ip: String): Boolean {
        return Patterns.IP_ADDRESS.matcher(ip).matches()
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 4
    }
}
