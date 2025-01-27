package com.example.cashmanager.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.cashmanager.R
import com.example.cashmanager.data.dto.CustomerDTO
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
    private lateinit var progressView: FrameLayout

    private var user : CustomerDTO = CustomerDTO()

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
        progressView = findViewById(R.id.progress_view)

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

        enableComponents(false)
        user = CustomerDTO(usernameEditText.text.toString(),
            passwordEditText.text.toString(),
            emailEditText.text.toString(),
            firstNameEditText.text.toString(),
            lastNameEditText.text.toString())

        val call = loginService.register(user)

        call.enqueue(object : Callback<CustomerDTO> {
            override fun onResponse(call: Call<CustomerDTO>, respose: Response<CustomerDTO>) {
                Toast.makeText(this@RegisterActivity, resources.getText(R.string.account_created), Toast.LENGTH_LONG).show()
                enableComponents(true)
                finish()
            }

            override fun onFailure(call: Call<CustomerDTO>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, resources.getText(R.string.username_unavailable), Toast.LENGTH_SHORT).show()
                enableComponents(true)
            }
        })
    }

    private fun enableComponents(isEnabled: Boolean) {
        progressView.visibility = if (isEnabled) View.GONE else View.VISIBLE
        usernameEditText.isEnabled = isEnabled
        firstNameEditText.isEnabled = isEnabled
        lastNameEditText.isEnabled = isEnabled
        emailEditText.isEnabled = isEnabled
        passwordEditText.isEnabled = isEnabled
        repeatPasswordEditText.isEnabled = isEnabled
        registerBtn.isEnabled = isEnabled
    }
}
