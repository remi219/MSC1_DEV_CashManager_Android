package com.example.cashmanager.ui.bill

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashmanager.R
import com.example.cashmanager.data.model.Cart
import com.example.cashmanager.ui.payment.PaymentActivity
import com.example.cashmanager.ui.productPicker.ProductPickerAdapter
import kotlinx.android.synthetic.main.activity_bill.*
import java.text.NumberFormat

class BillActivity : AppCompatActivity() {

    lateinit var cart : Cart

    lateinit var cartRecyclerView : RecyclerView
    lateinit var totalTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill)

        cartRecyclerView = findViewById(R.id.product_list)
        totalTextView = findViewById(R.id.bill_total)

        cart = intent.getSerializableExtra("cart") as Cart? ?: Cart()
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
        startActivity(intent)
    }

    fun cancel(v: View) {
        onBackPressed()
    }
}
