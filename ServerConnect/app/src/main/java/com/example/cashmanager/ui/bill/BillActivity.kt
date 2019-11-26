package com.example.cashmanager.ui.bill

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
import com.example.cashmanager.ui.payment.PaymentActivity
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat

class BillActivity : AppCompatActivity() {

    lateinit var cart : Cart
    lateinit var paymentMode : PaymentMode
    private lateinit var orderAPI : OrderService

    lateinit var cartRecyclerView : RecyclerView
    lateinit var totalTextView : TextView
    lateinit var paymentTextView : TextView
    lateinit var nbItemTextView : TextView
    lateinit var progressView : FrameLayout
    lateinit var goToPaymentBtn : Button

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

    /**
     * Proceed to the payment page
     */
    fun goToPayment(v: View) {
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra("cart", cart)
        intent.putExtra("paymentMode", paymentMode)
        goToPaymentBtn.isEnabled = false

        progressView.visibility = View.VISIBLE

        sendOrder(intent)
    }

    /**
     * Send the order the API and send order id to the next page
     */
    private fun sendOrder(intent: Intent) {
        val userId  = prefs.getString("token", "") ?: ""
        val order = OrderDTO(cart)

//        if (userId.isEmpty()) {
//            progressView.visibility = View.GONE
//            return
//        }
        orderAPI.createUserOrder(userId, order).enqueue(object : Callback<OrderDTO> {
            override fun onResponse(call: Call<OrderDTO>, response: Response<OrderDTO>) {
                startActivity(intent)
                intent.putExtra("orderId", response.body()?.id ?: 0)
                progressView.visibility = View.GONE
                goToPaymentBtn.isEnabled = true
            }

            override fun onFailure(call: Call<OrderDTO>, t: Throwable) {
                startActivity(intent)
                progressView.visibility = View.GONE
                Toast.makeText(this@BillActivity, "Could not make order", Toast.LENGTH_SHORT).show()
                goToPaymentBtn.isEnabled = true
            }
        })
    }

    fun cancel(v: View) {
        onBackPressed()
    }
}
