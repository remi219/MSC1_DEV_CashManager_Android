package com.example.cashmanager.ui.payment

import android.app.Activity
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.cashmanager.R
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import java.nio.charset.Charset
import android.widget.Toast

class PaymentActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    var NFCscanActive : Boolean = false

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

    override fun onTagDiscovered(tag: Tag?) {
        if (!NFCscanActive)
            return

        println("Detected tag !")

        // convert tag to NDEF tag
        val ndef = Ndef.get(tag)
        ndef?.connect()
        val ndefMessage = ndef?.ndefMessage ?: ndef?.cachedNdefMessage
        val message = ndefMessage?.toByteArray()
            ?.toString(Charset.forName("UTF-8")) ?: ""
        //val id = tag?.id?.toString(Charset.forName("ISO-8859-1")) ?: ""
        //val id = bytesToHexString(tag?.id) ?: ""
        ndef?.close()
        //val data = mapOf(kId to id, kContent to message, kError to "", kStatus to "reading")
        val mainHandler = Handler(Looper.getMainLooper())

        this.runOnUiThread {
            Toast.makeText(this, "NFC detected", Toast.LENGTH_SHORT).show()
        }
        if (message != "")
            println(message)
        else
            println("No message")
    }

    fun scanQRcode(v: View) {
        run {
            IntentIntegrator(this@PaymentActivity).initiateScan()
        }
    }

    fun scanNFC(v: View) {
        NFCscanActive = true
    }

    fun backToRegister(v : View) {
        setResult(Activity.RESULT_OK, Intent())
    }

    fun cancelOperation(v: View) {
        setResult(Activity.RESULT_CANCELED, Intent())
    }
}
