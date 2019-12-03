package com.example.cashmanager.ui.payment

import android.app.PendingIntent
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.nfc.tech.NfcA
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import com.example.cashmanager.R
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.cashmanager.data.dto.PaymentCardDTO
import com.example.cashmanager.data.dto.PaymentChequeDTO
import com.example.cashmanager.data.model.Cart
import com.example.cashmanager.data.model.ChequeData
import com.example.cashmanager.data.model.PaymentMode
import com.example.cashmanager.service.OrderService
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

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var billTextView: TextView
    private lateinit var statusTextView : TextView
    private lateinit var scanChequeBtn : Button
    private lateinit var scanNFCBtn : Button
    private lateinit var backToRegisterBtn : Button
    private lateinit var cancelOpBtn : Button
    private lateinit var progressView : FrameLayout

    private lateinit var cart : Cart
    private var userId = 0
    private var orderId : Int = 0
    private lateinit var paymentMode : PaymentMode
    private lateinit var paymentAPI : PaymentService
    private lateinit var orderAPI : OrderService
    private var nfcEnabled = false
    private val activity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        billTextView = findViewById(R.id.bill_total)
        statusTextView = findViewById(R.id.payment_status_label)
        scanChequeBtn = findViewById(R.id.scan_barcode_btn)
        scanNFCBtn = findViewById(R.id.scan_nfc_btn)
        backToRegisterBtn = findViewById(R.id.back_register_btn)
        cancelOpBtn = findViewById(R.id.cancel_op_btn)
        progressView = findViewById(R.id.progress_view)

        val prefs = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        paymentAPI = ServiceBuilder.createService(PaymentService::class.java, prefs.getString("token", ""))
        orderAPI = ServiceBuilder.createService(OrderService::class.java, prefs.getString("token", ""))
        userId = prefs.getInt("userId", 0)

        cart = intent.getSerializableExtra("cart") as Cart? ?: Cart()
        orderId = intent.getIntExtra("orderId", 0)

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
        if (nfcEnabled)
            nfcAdapter?.disableForegroundDispatch(this)
        super.onPause()
    }

    override fun onResume() {
        println("On Resume")
        if (nfcEnabled)
            nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray)
        super.onResume()
    }

    override fun onNewIntent(intent: Intent) {
        println("Foreground dispatch detected")
        val nfcTag : Tag?  = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        println(nfcTag.toString())

        super.onNewIntent(intent)
    }

    /**
     * Set the page as loading
     * @param isLoading If the page is busy
     */
    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            progressView.visibility = View.VISIBLE
            scanChequeBtn.isEnabled = false
            scanNFCBtn.isEnabled = false
            cancelOpBtn.isEnabled = false
        } else {
            progressView.visibility = View.GONE
            scanChequeBtn.isEnabled = true
            scanNFCBtn.isEnabled = true
            cancelOpBtn.isEnabled = true
        }
    }

    /**
     * Check if nfc is available and enabled, then initialize NFC filter if necessary
     */
    private fun nfcCheck() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        val nfcAndroidAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAndroidAdapter == null) {
            val builder = AlertDialog.Builder(this)
            nfcEnabled = false
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
            nfcEnabled = false
            with(builder)
            {
                setIcon(android.R.drawable.ic_dialog_alert)
                setTitle(R.string.nfc_payment)
                setMessage(R.string.nfc_not_enable)
                setPositiveButton(R.string.ok){ _, _ ->}
                show()
            }
        } else if (!nfcEnabled) {
            nfcEnabled = true
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
                    NdefFormatable::class.java.name,
                    Ndef::class.java.name
                )
            )
            Toast.makeText(this, resources.getText(R.string.NFC_available), Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this, resources.getText(R.string.NFC_available), Toast.LENGTH_SHORT).show()
        }
    }


    /***
     * Start scanning activity
     */
    fun scanQRcode(v : View) {
        run {
            IntentIntegrator(this@PaymentActivity).initiateScan()
        }
    }

    /**
     * Start NFC scanning
     */
    fun scanNFC(v : View) {
        nfcCheck()
    }

    /**
     * Send the scanned cheque data to the payment API
     * @param content: Cheque content
     */
    private fun sendChequePayment(content : String) {
        loading(true)
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
            loading(false)
            return
        }

        // Check if value is correct
        if (round(chequeData.value * 100) / 100 != round(cart.billTotal * 100) / 100) {
            val format = NumberFormat.getCurrencyInstance()
            statusTextView.text = resources.getString(R.string.cheque_invalid_value, format.format(chequeData.value))
            statusTextView.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorFailure))
            loading(false)
            return
        }

        // Call to the server
        try {
            val call = paymentAPI.postChequePayment(PaymentChequeDTO(userId, chequeData.id, cart.billTotal, orderId))
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>
                ) {
                    statusTextView.text = resources.getString(R.string.cheque_authorized)
                    statusTextView.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorSuccess))
                    backToRegisterBtn.isEnabled = true
                    loading(false)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    statusTextView.text = resources.getString(R.string.cheque_refused)
                    statusTextView.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorFailure))
                    Toast.makeText(
                        activity,
                        resources.getString(R.string.cheque_refused),
                        Toast.LENGTH_SHORT
                    ).show()
                    loading(false)
                }
            })
        } catch (ex : Exception) {
            ex.printStackTrace()
            Toast.makeText(
                activity,
                resources.getString(R.string.api_call_failed),
                Toast.LENGTH_SHORT
            ).show()
            statusTextView.text = resources.getText(R.string.api_connection_failed)
            statusTextView.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorFailure))
        }
        loading(false)
    }

    /**
     * Send the scanned NFC card data to the payment API
     * @param cardId: Card id
     */
    private fun sendCardPayment(cardId : String) {
        val call = paymentAPI.postNFCPayment(PaymentCardDTO(userId, cardId, orderId))

        loading(true)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                statusTextView.text = resources.getString(R.string.card_accepted)
                backToRegisterBtn.isEnabled = true
                loading(false)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                statusTextView.text = resources.getString(R.string.card_refused)
                Toast.makeText(activity, resources.getString(R.string.cheque_refused), Toast.LENGTH_SHORT).show()
                loading(false)
            }
        })
    }

    /***
     * Return to Cash register activity when the payment is successful
     */
    fun backToRegister(v : View) {
        finish()
        finish()
    }

    /***
     * Cancel the current operation by deleting the order using the API
     */
    fun cancelOperation(v: View) {
        if (orderId > 0) {
            orderAPI.deleteOrder(orderId).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    println(response.raw())
                    println(response.isSuccessful)
                    onBackPressed()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    println("Failed to delete order")
                    onBackPressed()
                }
            })
        }
        else {
            println(orderId)
            onBackPressed()
        }

    }

    override fun onFragmentInteraction(uri: Uri) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
