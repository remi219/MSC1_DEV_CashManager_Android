package com.example.cashmanager.ui.payment

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.cashmanager.R
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import java.nio.charset.Charset
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.cashmanager.data.model.PaymentMode

class PaymentActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private var NFCscanActive : Boolean = false
    private lateinit var statusTextView : TextView
    private lateinit var scanChequeBtn : Button
    private lateinit var scanNFCBtn : Button

    private lateinit var paymentMode : PaymentMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        statusTextView = findViewById(R.id.payment_status_label)
        scanChequeBtn = findViewById(R.id.scan_cheque_btn)
        scanNFCBtn = findViewById(R.id.scan_nfc_btn)

        paymentMode = intent.getSerializableExtra("paymentMode") as PaymentMode
        if (paymentMode == PaymentMode.CHEQUE) {
            scanNFCBtn.visibility = View.GONE
            scanChequeBtn.visibility = View.VISIBLE
        }
        else {
            scanChequeBtn.visibility = View.GONE
            scanNFCBtn.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if(result != null){
            // Todo: send data to the api
            Toast.makeText(this, "Todo: waiting answer from the server", Toast.LENGTH_SHORT).show()

            if(result.contents != null){
                // Todo: => payment success !
                statusTextView.text = resources.getString(R.string.cheque_authorized)
                statusTextView.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.colorSuccess))
                Toast.makeText(this, resources.getString(R.string.cheque_authorized), Toast.LENGTH_SHORT).show()
            } else {
                statusTextView.text = resources.getString(R.string.cheque_refused)
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

    /***
     * Start scanning activity
     */
    fun scanQRcode(v: View) {
        run {
            IntentIntegrator(this@PaymentActivity).initiateScan()
        }
    }

    fun scanNFC(v: View) {
        NFCscanActive = true
    }

    fun sendChequePayment() {
        // Todo:
    }
    
    fun sendCardPayment() {
        // Todo:
    }

    /***
     * Return to Cash register activity when the payment is successful
     */
    fun backToRegister(v : View) {
        setResult(RESULT_OK, Intent())
        onBackPressed()
    }

    /***
     * Cancel the current operation
     */
    fun cancelOperation(v: View) {
        // todo: define what it's suppose to do ?
        //setResult(RESULT_CANCELED, Intent())
    }
}
