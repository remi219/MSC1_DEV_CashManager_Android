package com.example.cashmanager.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.cashmanager.R
import com.example.cashmanager.data.dto.UserDTO
import com.example.cashmanager.service.LoginService
import com.example.cashmanager.service.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Activity used for the creation of a new user account
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var usernameEditText : EditText
    private lateinit var firstNameEditText : EditText
    private lateinit var lastNameEditText : EditText
    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText : EditText
    private lateinit var repeatPasswordEditText : EditText
    private lateinit var errorTextView : TextView
    private lateinit var registerBtn : Button

    private var user : UserDTO = UserDTO()

    private lateinit var loginService: LoginService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        usernameEditText = findViewById(R.id.username_textview)
        firstNameEditText = findViewById(R.id.firstname_textview)
        lastNameEditText = findViewById(R.id.lastname_textview)
        emailEditText = findViewById(R.id.email_editText)
        passwordEditText = findViewById(R.id.password_editText)
        repeatPasswordEditText = findViewById(R.id.password_repeat_editText)
        errorTextView = findViewById(R.id.error_textView)
        registerBtn = findViewById(R.id.register_btn)

        loginService = ServiceBuilder.createService(LoginService::class.java)
    }

    /***
     * Check if user params are valids
     * @return if the user param are ok
     */
    private fun checkRegister(): Boolean {
        if (usernameEditText.text.isNullOrBlank()) {
            errorTextView.text = resources.getText(R.string.error_no_username)
            return false
        }
        else if (passwordEditText.text.isNullOrBlank() || repeatPasswordEditText.text.isNullOrBlank()) {
            errorTextView.text = resources.getText(R.string.error_no_password)
            return false
        }
        else if (repeatPasswordEditText.text.toString() != passwordEditText.text.toString()) {
            errorTextView.text = resources.getText(R.string.error_repeat_password)
            return false
        }
        errorTextView.text = ""
        return true
    }

    /***
     * Request the API for the creation of a user
     */
    fun register(v: View) {
        if (!checkRegister())
            return

        val call = loginService.register(user)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, respose: Response<String>) {
                Toast.makeText(this@RegisterActivity, resources.getText(R.string.account_created), Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, resources.getText(R.string.username_unavailable), Toast.LENGTH_SHORT).show()
            }
        })
    }
}
