package com.example.cashmanager.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast

import com.example.cashmanager.R
import com.example.cashmanager.data.dto.CustomerDTO
import com.example.cashmanager.data.dto.LoginDTO
import com.example.cashmanager.service.LoginService
import com.example.cashmanager.service.PaymentService
import com.example.cashmanager.service.ServiceBuilder
import com.example.cashmanager.ui.cashRegister.CashRegisterActivity
import com.example.cashmanager.ui.cashRegister.CashRegisterAdapter
import com.example.cashmanager.ui.payment.PaymentActivity
import com.example.cashmanager.ui.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    private lateinit var loginAPI : LoginService
    private lateinit var editor : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val register = findViewById<Button>(R.id.register)
        val loading = findViewById<ProgressBar>(R.id.loading)

        val prefs = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        editor = prefs.edit()
        loginAPI = ServiceBuilder.createService(LoginService::class.java)

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)
        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer
            login.isEnabled = loginState.isDataValid
            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })
        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer
            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)
            val intent = Intent(this, CashRegisterActivity::class.java)
            startActivity(intent)
        })
        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }
        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }
            login.setOnClickListener {
                // loginViewModel.login(username.text.toString(), password.text.toString(), this@LoginActivity)
                login(username.text.toString(), password.text.toString())
            }
        }

        register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateUiWithUser(model: ServerDataView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayUsername
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    private fun login(username: String, password : String) {
        loading.visibility = View.VISIBLE
        val call= loginAPI.login(LoginDTO(username, password))

        try {
            call.enqueue(object: retrofit2.Callback<CustomerDTO> {
                override fun onResponse(call: Call<CustomerDTO>, response: Response<CustomerDTO>) {
                    loading.visibility = View.GONE
                    if (response.isSuccessful) {
                        editor.putInt("userId", response.body()?.id ?: 0)
                        editor.commit()
                        val intent = Intent(this@LoginActivity, CashRegisterActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this@LoginActivity, R.string.login_success, Toast.LENGTH_SHORT).show()
                    } else {
                        println(call.request().body().toString())
                        println(response.raw())
                        println("Login response unsuccessful")
                        Toast.makeText(this@LoginActivity, R.string.login_failed, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CustomerDTO>, t: Throwable) {
                    println("Login failed")
                    loading.visibility = View.GONE
                    Toast.makeText(this@LoginActivity, R.string.login_failed, Toast.LENGTH_SHORT).show()
                }
            })
        } catch (ex: Exception) {
            println("Login exception")
            ex.printStackTrace()
        }

    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
