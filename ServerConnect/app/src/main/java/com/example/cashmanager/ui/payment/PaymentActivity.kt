package com.example.cashmanager.ui.payment

import android.app.PendingIntent
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.nfc.NfcAdapter
import android.nfc.NfcAdapter.ACTION_TECH_DISCOVERED
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.nfc.tech.NfcA
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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.cashmanager.data.dto.PaymentCardDTO
import com.example.cashmanager.data.dto.PaymentChequeDTO
import com.example.cashmanager.data.model.Cart
import com.example.cashmanager.data.model.ChequeData
import com.example.cashmanager.data.model.PaymentMode
import com.example.cashmanager.service.PaymentService
import com.example.cashmanager.service.ServiceBuilder
import com.example.cashmanager.ui.connectionStatus.ConnectionStatusFragment
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.NumberFormat
import kotlin.math.round

class PaymentActivity : AppCompatActivity(),
    ConnectionStatusFragment.OnFragmentInteractionListener {

    private lateinit var pendingIntent : PendingIntent
    private lateinit var intentFiltersArray : Array<IntentFilter>
    private lateinit var techListsArray : Array<Array<String>>

    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var billTextView: TextView
    private lateinit var statusTextView : TextView
    private lateinit var scanChequeBtn : Button
    private lateinit var scanNFCBtn : Button
    private lateinit var backToRegisterBtn : Button
    private lateinit var cancelOpBtn : Button

    private lateinit var cart : Cart
    private lateinit var paymentMode : PaymentMode
    private lateinit var paymentAPI : PaymentService
    private val activity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        billTextView = findViewById(R.id.bill_total)
        statusTextView = findViewById(R.id.payment_status_label)
        scanChequeBtn = findViewById(R.id.scan_cheque_btn)
        scanNFCBtn = findViewById(R.id.scan_nfc_btn)
        backToRegisterBtn = findViewById(R.id.back_register_btn)
        cancelOpBtn = findViewById(R.id.cancel_op_btn)

        val prefs = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        paymentAPI = ServiceBuilder.createService(PaymentService::class.java, prefs.getString("token", ""))

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
            nfcCheck()
        }

        // See https://stackoverflow.com/questions/21307898/how-to-proactive-read-nfc-tag-without-intent
        // https://developer.android.com/guide/topics/connectivity/nfc/advanced-nfc
        val intent = Intent(this, this.javaClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val ndef = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        try {
            // Handles all MIME based dispatches. You should specify only the ones that you need.
            ndef.addDataType("*/*")
        } catch (e: IntentFilter.MalformedMimeTypeException) {
            throw RuntimeException("failed to add MIME type", e);
        }
        // Use no intent filters to accept all MIME types
        intentFiltersArray = Array(1) {ndef}

        // The tech list array can be set to null to accept all types of tag
        techListsArray = arrayOf(
            arrayOf(
                IsoDep::class.java.name,
                NfcA::class.java.name,
                NdefFormatable::class.java.name
            )
        )
    }

    /**
     * On qr code read activity reader
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if(result != null){
            println(result.contents)
            if(result.contents != null){
                sendChequePayment(result.contents)
            } else {
                statusTextView.text = resources.getString(R.string.scan_failed)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onPause() {
        println("On Pause")
        nfcAdapter.disableForegroundDispatch(this)
        super.onPause()
    }

    override fun onResume() {
        println("On Resume")
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray)
        super.onResume()
    }

    override fun onNewIntent(intent: Intent) {
        println("Foreground dispatch")
        val nfcTag : Tag?  = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        println(nfcTag.toString())
        super.onNewIntent(intent)
    }

    /**
     * Check if nfc is available and enabled
     */
    private fun nfcCheck() {
        val nfcAndroidAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAndroidAdapter == null) {
            val builder = AlertDialog.Builder(this)
            with(builder)
            {
                setIcon(android.R.drawable.ic_dialog_alert)
                setTitle(R.string.nfc_payment)
                setMessage(R.string.nfc_not_supported)
                setPositiveButton(R.string.ok){ _, _ -> finish() }
                show()
            }
        } else if (!nfcAndroidAdapter.isEnabled) {
            val builder = AlertDialog.Builder(this)
            with(builder)
            {
                setIcon(android.R.drawable.ic_dialog_alert)
                setTitle(R.string.nfc_payment)
                setMessage(R.string.nfc_not_enable)
                setPositiveButton(R.string.ok){ _, _ ->}
                show()
            }
        } else
            return
    }


    /***
     * Start scanning activity
     */
    fun scanQRcode(v: View) {
        run {
            IntentIntegrator(this@PaymentActivity).initiateScan()
        }
    }

    /**
     * Start NFC scanning
     */
    fun scanNFC(v: View) {
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray)
    }

    /**
     * Send the scanned cheque data to the payment API
     * @param content: Cheque content
     */
    private fun sendChequePayment(content : String) {
        statusTextView.text = resources.getString(R.string.pending_authorization)
        statusTextView.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPending))
        val chequeData : ChequeData?
        val gson = Gson()

        // Check if data are valid
        try {
            chequeData = gson.fromJson(content, ChequeData::class.java)
        }
        catch (ex: Exception) {
            println(ex.message)
            statusTextView.text = resources.getText(R.string.cheque_unreadable)
            statusTextView.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorFailure))
            return
        }

        // Check if value is correct
        if (round(chequeData.value * 100) / 100 != round(cart.billTotal * 100) / 100) {
            val format = NumberFormat.getCurrencyInstance()
            statusTextView.text = resources.getString(R.string.cheque_invalid_value, format.format(chequeData.value))
            statusTextView.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorFailure))
            return
        }

        // Call to the server
        try {
            val call = paymentAPI.postChequePayment(PaymentChequeDTO(chequeData.id, cart.billTotal))
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    statusTextView.text = resources.getString(R.string.cheque_authorized)
                    statusTextView.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorSuccess))
                    backToRegisterBtn.isEnabled = true
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    statusTextView.text = resources.getString(R.string.cheque_refused)
                    statusTextView.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorFailure))
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
            statusTextView.text = resources.getText(R.string.api_connection_failed)
            statusTextView.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorFailure))
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
                backToRegisterBtn.isEnabled = true
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
        finish()
    }

    /***
     * Cancel the current operation
     */
    fun cancelOperation(v: View) {
        // todo: define what it's suppose to do ?
        //setResult(RESULT_CANCELED, Intent())
        onBackPressed()
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
