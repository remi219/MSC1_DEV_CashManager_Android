package com.example.cashmanager.ui.bill

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashmanager.R
import com.example.cashmanager.data.dto.OrderDTO
import com.example.cashmanager.data.model.Cart
import com.example.cashmanager.data.model.PaymentMode
import com.example.cashmanager.service.OrderService
import com.example.cashmanager.ui.payment.PaymentActivity
import com.example.cashmanager.ui.productPicker.ProductPickerAdapter
import kotlinx.android.synthetic.main.activity_bill.*
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.NumberFormat
import kotlin.reflect.typeOf

class BillActivity : AppCompatActivity() {

    lateinit var cart : Cart
    lateinit var paymentMode : PaymentMode
    private val orderAPI : OrderService by inject()

    lateinit var cartRecyclerView : RecyclerView
    lateinit var totalTextView : TextView
    lateinit var paymentLabel : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill)

        cartRecyclerView = findViewById(R.id.product_list)
        totalTextView = findViewById(R.id.bill_total)
        paymentLabel = findViewById(R.id.payment_mode)

        cart = intent.getSerializableExtra("cart") as Cart? ?: Cart()
        paymentMode = intent.getSerializableExtra("paymentMode") as PaymentMode
        if (paymentMode == PaymentMode.CHEQUE)
            paymentLabel.text = resources.getString(R.string.payment_mode_cheque)
        else
            paymentLabel.text = resources.getString(R.string.payment_mode_nfc)
        // Todo; error handling if cart is false
        val format = NumberFormat.getCurrencyInstance()
        totalTextView.text = resources.getString(R.string.bill_total, format.format(cart.billTotal))

        val activity = this
        cartRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = BillAdapter(cart.products, activity)
        }
    }

    fun goToPayment(v: View) {
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra("paymentMode", paymentMode)

        orderAPI.createUserOrder(1, OrderDTO()).enqueue(object : Callback<OrderDTO> {
            override fun onResponse(call: Call<OrderDTO>, response: Response<OrderDTO>) {
                startActivity(intent)
            }

            override fun onFailure(call: Call<OrderDTO>, t: Throwable) {
                startActivity(intent)
                //Toast.makeText(this, "Could not make order", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun cancel(v: View) {
        onBackPressed()
    }
}
