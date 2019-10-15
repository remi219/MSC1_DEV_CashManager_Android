package com.example.cashmanager.ui.cashRegister

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashmanager.R
import com.example.cashmanager.data.model.Cart
import com.example.cashmanager.data.model.PaymentMode
import com.example.cashmanager.ui.bill.BillActivity
import com.example.cashmanager.ui.productPicker.ProductPickerActivity
import java.io.Serializable
import java.text.NumberFormat

class CashRegisterActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val PICK_PRODUCTS_REQUEST = 1
    private val format = NumberFormat.getCurrencyInstance()

    var cart: Cart = Cart()
    private var paymentMode : PaymentMode = PaymentMode.CHEQUE
    private val paymentModeTitle = listOf("cheque", "card")

    lateinit var noArticleTextview : TextView
    lateinit var cartRecyclerView : RecyclerView
    lateinit var totalTextView: TextView
    lateinit var proceedButton : TextView
    lateinit var paymentSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cash_register)

        noArticleTextview = findViewById(R.id.no_article_textview)
        cartRecyclerView = findViewById(R.id.articles_list)
        totalTextView = findViewById(R.id.total_textview)
        proceedButton = findViewById(R.id.proceed_btn)
        paymentSpinner = findViewById(R.id.payment_spinner)

        paymentSpinner.onItemSelectedListener = this
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentModeTitle)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        paymentSpinner.adapter = aa

        totalTextView.text = resources.getString(R.string.bill_total, format.format(0))
        proceedButton.isEnabled = false
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val activity = this
        // Check which request we're responding to
        if (requestCode == PICK_PRODUCTS_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                // Get cart content from product picker activity
                val cartFromPicker = data?.getSerializableExtra("cart") as? Cart?
                this.cart = cartFromPicker ?: Cart()
                totalTextView.text = resources.getString(R.string.bill_total, format.format(cart.billTotal))

                proceedButton.isEnabled = cart.size > 0
                if (cart.size == 0) {
                    noArticleTextview.visibility = View.VISIBLE
                    cartRecyclerView.visibility = View.GONE
                } else {
                    noArticleTextview.visibility = View.GONE
                    cartRecyclerView.visibility = View.VISIBLE
                    cartRecyclerView.apply {
                        layoutManager = LinearLayoutManager(activity)
                        adapter = CashRegisterAdapter(cart.products, activity)
                    }
                }
            }
        } else {
            cart = Cart()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * Open the Product picker activity to add item to cart
     */
    fun openProductPicker(v: View) {
        val intent = Intent(this, ProductPickerActivity::class.java).apply {
            putExtra("cart", cart as Serializable)
        }
        startActivityForResult(intent, PICK_PRODUCTS_REQUEST)
    }

    /**
     * Reset the content of the cart.
     */
    fun reset(v: View) {
        // Todo: clear the list of products in the cart
        noArticleTextview.visibility = View.VISIBLE
        cartRecyclerView.visibility = View.GONE
        cart.reset()
        proceedButton.isEnabled = false
        Toast.makeText(this, R.string.cart_reset_toast, Toast.LENGTH_SHORT).show()
    }

    /**
     * Proceed to the next activity, displaying the bill total.
     * Transmit the cart content as Serializable to the next activity
     */
    fun proceed(v: View) {
        // Todo: disable if no product
        val intent = Intent(this, BillActivity::class.java)
        intent.putExtra("cart", cart as Serializable)
        intent.putExtra("paymentMode", paymentMode)
        startActivity(intent)
    }

    // Spinner
    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        paymentMode = if (position == 0) PaymentMode.CHEQUE else PaymentMode.NFC
    }

}
