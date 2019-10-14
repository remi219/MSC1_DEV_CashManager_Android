package com.example.cashmanager.ui.bill

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.cashmanager.R
import com.example.cashmanager.ui.payment.PaymentActivity

class BillActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill)
    }

    fun goToPayment(v: View) {
        val intent = Intent(this, PaymentActivity::class.java)
        startActivity(intent)
    }

    fun cancel(v: View) {
        onBackPressed()
    }
}
