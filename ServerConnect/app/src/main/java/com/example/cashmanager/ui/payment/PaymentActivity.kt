package com.example.cashmanager.ui.payment

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.cashmanager.R

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
    }

    fun scanQRcode(v: View) {

    }

    fun scanNFC(v: View) {

    }

    fun backToRegister(v : View) {
        setResult(Activity.RESULT_OK, Intent())
    }

    fun cancelOperation(v: View) {
        setResult(Activity.RESULT_CANCELED, Intent())
    }
}
