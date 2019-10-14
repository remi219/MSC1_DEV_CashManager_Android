package com.example.cashmanager.ui.payment

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.cashmanager.R
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if(result != null){

            if(result.contents != null){
                // Todo: => payment success !
                // txtValue.text = result.contents
            } else {
                // txtValue.text = "scan failed"
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun scanQRcode(v: View) {
        run {
            IntentIntegrator(this@PaymentActivity).initiateScan()
        }
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
