package com.example.cashmanager.ui.payment

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
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
import com.example.cashmanager.data.dto.PaymentCardDTO
import com.example.cashmanager.data.dto.PaymentChequeDTO
import com.example.cashmanager.data.model.Cart
import com.example.cashmanager.data.model.PaymentMode
import com.example.cashmanager.service.PaymentService
import com.example.cashmanager.ui.connectionStatus.ConnectionStatusFragment
import okhttp3.ResponseBody
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.NumberFormat

class PaymentActivity : AppCompatActivity(), NfcAdapter.ReaderCallback,
    ConnectionStatusFragment.OnFragmentInteractionListener {

    private var NFCscanActive : Boolean = false
    private lateinit var billTextView: TextView
    private lateinit var statusTextView : TextView
    private lateinit var scanChequeBtn : Button
    private lateinit var scanNFCBtn : Button

    private lateinit var cart : Cart
    private lateinit var paymentMode : PaymentMode
    private val activity = this
    private val paymentAPI : PaymentService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        billTextView = findViewById(R.id.bill_total)
        statusTextView = findViewById(R.id.payment_status_label)
        scanChequeBtn = findViewById(R.id.scan_cheque_btn)
        scanNFCBtn = findViewById(R.id.scan_nfc_btn)

        cart = intent.getSerializableExtra("cart") as Cart? ?: Cart()

        val format = NumberFormat.getCurrencyInstance()
        billTextView.text = resources.getString(R.string.bill_total, format.format(cart.billTotal))

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
                sendChequePayment(result.contents)
            } else {
                statusTextView.text = resources.getString(R.string.scan_failed)
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
        sendCardPayment(message)
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

    /**
     * Send the scanned cheque data to the payment API
     * @param content: Cheque content
     */
    private fun sendChequePayment(content : Any) {
        try {
            val call = paymentAPI.postChequePayment(PaymentChequeDTO("toto", cart.billTotal))

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    statusTextView.text = resources.getString(R.string.cheque_authorized)
                    statusTextView.setBackgroundColor(
                        ContextCompat.getColor(activity, R.color.colorSuccess)
                    )
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    statusTextView.text = resources.getString(R.string.cheque_refused)
                    statusTextView.setBackgroundColor(
                        ContextCompat.getColor(activity, R.color.colorFailure)
                    )
                    Toast.makeText(
                        activity,
                        resources.getString(R.string.cheque_refused),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (ex : Exception) {
            Toast.makeText(
                activity,
                resources.getString(R.string.api_call_failed),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Send the scanned NFC card data to the payment API
     * @param content: Cheque content
     */
    private fun sendCardPayment(content : Any) {
        val call = paymentAPI.postNFCPayment(PaymentCardDTO("toto"))

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                statusTextView.text = resources.getString(R.string.card_accepted)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                statusTextView.text = resources.getString(R.string.card_refused)
                Toast.makeText(activity, resources.getString(R.string.cheque_refused), Toast.LENGTH_SHORT).show()
            }
        })
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

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
