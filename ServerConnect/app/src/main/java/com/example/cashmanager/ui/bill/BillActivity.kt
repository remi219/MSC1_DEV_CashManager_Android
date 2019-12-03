package com.example.cashmanager.ui.bill

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashmanager.R
import com.example.cashmanager.data.dto.OrderDTO
import com.example.cashmanager.data.model.Cart
import com.example.cashmanager.data.model.PaymentMode
import com.example.cashmanager.service.OrderService
import com.example.cashmanager.service.ServiceBuilder
import com.example.cashmanager.ui.cashRegister.CashRegisterAdapter
import com.example.cashmanager.ui.payment.PaymentActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat

class BillActivity : AppCompatActivity() {

    private val PAYMENT_REQUEST = 123

    lateinit var cart : Cart
    lateinit var paymentMode : PaymentMode
    private lateinit var orderAPI : OrderService

    lateinit var cartRecyclerView : RecyclerView
    lateinit var totalTextView : TextView
    lateinit var paymentTextView : TextView
    lateinit var nbItemTextView : TextView
    lateinit var progressView : FrameLayout
    lateinit var goToPaymentBtn : Button
    lateinit var cancelBtn : Button

    lateinit var prefs : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill)

        cartRecyclerView = findViewById(R.id.product_list)
        totalTextView = findViewById(R.id.bill_total)
        nbItemTextView = findViewById(R.id.nb_item)
        paymentTextView = findViewById(R.id.payment_mode)
        progressView = findViewById(R.id.progress_view)
        goToPaymentBtn = findViewById(R.id.proceed_btn)
        cancelBtn = findViewById(R.id.cancel_btn)

        prefs = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        orderAPI = ServiceBuilder.createService(OrderService::class.java, prefs.getString("token", ""))

        cart = intent.getSerializableExtra("cart") as Cart? ?: Cart()
        paymentMode = intent.getSerializableExtra("paymentMode") as PaymentMode
        if (paymentMode == PaymentMode.CHEQUE)
            paymentTextView.text = resources.getString(R.string.payment_mode_cheque)
        else
            paymentTextView.text = resources.getString(R.string.payment_mode_nfc)
        // Todo; error handling if cart is false
        val format = NumberFormat.getCurrencyInstance()
        totalTextView.text = resources.getString(R.string.bill_total, format.format(cart.billTotal))
        nbItemTextView.text = resources.getQuantityString(R.plurals.items, cart.products.size, cart.products.size)

        cartRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@BillActivity)
            adapter = BillAdapter(cart.products, this@BillActivity)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        if (requestCode == PAYMENT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                finish()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            progressView.visibility = View.VISIBLE
            cancelBtn.isEnabled = false
            goToPaymentBtn.isEnabled = false
        } else {
            progressView.visibility = View.GONE
            cancelBtn.isEnabled = true
            goToPaymentBtn.isEnabled = true
        }
    }

    /**
     * Proceed to the payment page
     */
    fun goToPayment(v: View) {
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra("cart", cart)
        intent.putExtra("paymentMode", paymentMode)
        goToPaymentBtn.isEnabled = false

        sendOrder(intent)
    }

    /**
     * Send the order the API and send order id to the next page
     */
    private fun sendOrder(intent: Intent) {
        loading(true)

        val userId = prefs.getInt("userId", 0)
        val order = OrderDTO(cart)

        orderAPI.createUserOrder(userId, order).enqueue(object: Callback<OrderDTO> {
            override fun onResponse(call: Call<OrderDTO>, response: Response<OrderDTO>) {
                intent.putExtra("orderId", response.body()?.id ?: 0)
                if (response.body()?.id != null && response.body()!!.id!! > 0)
                    Toast.makeText(this@BillActivity, R.string.order_created, Toast.LENGTH_SHORT).show()
                loading(false)
                startActivityForResult(intent, PAYMENT_REQUEST)
            }

            override fun onFailure(call: Call<OrderDTO>, t: Throwable) {
                startActivity(intent)
                Toast.makeText(this@BillActivity, "Could not make order", Toast.LENGTH_SHORT).show()
                loading(false)
                t.printStackTrace()
            }
        })
    }

    fun cancel(v: View) {
        onBackPressed()
    }
}
