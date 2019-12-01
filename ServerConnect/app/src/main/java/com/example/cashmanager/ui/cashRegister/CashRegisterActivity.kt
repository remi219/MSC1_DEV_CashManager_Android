package com.example.cashmanager.ui.cashRegister

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashmanager.R
import com.example.cashmanager.data.dto.ProductQuantityDTO
import com.example.cashmanager.data.dto.ProductWrapperDTO
import com.example.cashmanager.data.model.Cart
import com.example.cashmanager.data.model.PaymentMode
import com.example.cashmanager.service.CustomerService
import com.example.cashmanager.service.ProductService
import com.example.cashmanager.service.ServiceBuilder
import com.example.cashmanager.ui.bill.BillActivity
import com.example.cashmanager.ui.productPicker.ProductPickerActivity
import kotlinx.android.synthetic.main.activity_cash_register.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.text.NumberFormat


class CashRegisterActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val PICK_PRODUCTS_REQUEST = 1
    private val format = NumberFormat.getCurrencyInstance()
    private lateinit var productAPI : ProductService
    private lateinit var customerAPI : CustomerService

    var cart: Cart = Cart()
    private var paymentMode : PaymentMode = PaymentMode.CHEQUE
    private lateinit var paymentModeTitle : List<String>
    private lateinit var prefs : SharedPreferences

    private var userId : Int = 0

    lateinit var noArticleTextview : TextView
    lateinit var cartRecyclerView : RecyclerView
    lateinit var totalTextView : TextView
    lateinit var proceedButton : TextView
    lateinit var paymentSpinner : Spinner
    lateinit var progressView : FrameLayout
    lateinit var resetBtn : Button
    lateinit var proceedBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cash_register)

        noArticleTextview = findViewById(R.id.no_article_textview)
        cartRecyclerView = findViewById(R.id.articles_list)
        totalTextView = findViewById(R.id.total_textview)
        proceedButton = findViewById(R.id.proceed_btn)
        paymentSpinner = findViewById(R.id.payment_spinner)
        progressView = findViewById(R.id.progress_view)
        resetBtn = findViewById(R.id.reset_cart_btn)
        proceedBtn = findViewById(R.id.proceed_btn)

        paymentModeTitle = listOf(resources.getString(R.string.payment_mode_cheque), resources.getString(R.string.payment_mode_nfc))

        prefs = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        productAPI = ServiceBuilder.createService(ProductService::class.java, prefs.getString("token", ""))
        customerAPI = ServiceBuilder.createService(CustomerService::class.java, prefs.getString("token", ""))
        userId  = prefs.getInt("userId", 0)

        paymentSpinner.onItemSelectedListener = this
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentModeTitle)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        paymentSpinner.adapter = aa

        totalTextView.text = resources.getString(R.string.bill_total, format.format(0))
        proceedButton.isEnabled = false

        reloadCustomerCart()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                val builder = AlertDialog.Builder(this)
                with(builder)
                {
                    setIcon(android.R.drawable.ic_dialog_alert)
                    setTitle(R.string.logout)
                    setMessage(R.string.logout_message)
                    setPositiveButton(R.string.ok){ _, _ ->
                        val pref = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
                        val editor = pref.edit()
                        editor.remove("jwt")
                        editor.apply()
                        finish()
                    }
                    setNegativeButton(R.string.cancel, null)
                    show()
                }
            }
        }

        return super.onOptionsItemSelected(item)
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
     * Set the page as loading
     * @param isLoading If the page is busy
     */
    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            progressView.visibility = View.VISIBLE
            resetBtn.isEnabled = false
            proceedBtn.isEnabled = false
        } else {
            progressView.visibility = View.GONE
            resetBtn.isEnabled = true
            proceedBtn.isEnabled = true
        }
    }

    /**
     * Load the customer's cart from the API
     */
    fun reloadCustomerCart() {
        loading(true)
        customerAPI.getCart(userId).enqueue(object: Callback<List<ProductQuantityDTO>> {
            override fun onResponse(call: Call<List<ProductQuantityDTO>>, response: Response<List<ProductQuantityDTO>>) {
                loading(false)
                cart = Cart()
                response.body()?.forEach {
                    cart.products.add(Pair(it.product, it.quantity))
                }
            }

            override fun onFailure(call: Call<List<ProductQuantityDTO>>, t: Throwable) {
                loading(false)
            }
        })
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
     * Request the api to clear the customer content
     */
    fun reset(v: View) {
        // Todo: clear the list of products in the cart
        noArticleTextview.visibility = View.VISIBLE
        cartRecyclerView.visibility = View.GONE
        cart.reset()
        proceedButton.isEnabled = false
        Toast.makeText(this, R.string.cart_reset_toast, Toast.LENGTH_SHORT).show()

        customerAPI.resetCart(userId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println("Could not clear cart API side")
            }
        })
    }

    // Spinner
    override fun onNothingSelected(parent: AdapterView<*>?) { }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        paymentMode = if (position == 0) PaymentMode.CHEQUE else PaymentMode.NFC
    }

    /**
     * Proceed to the next activity, displaying the bill total.
     * Transmit the cart content as Serializable to the next activity
     * Save the cart content online
     */
    fun proceed(v: View) {
        if (cart.products.size < 1) {
            Toast.makeText(this, R.string.cart_empty, Toast.LENGTH_SHORT).show()
            return
        }

        loading(true)

        val intent = Intent(this, BillActivity::class.java)
        intent.putExtra("cart", cart as Serializable)
        intent.putExtra("paymentMode", paymentMode)

//        if (userId.isEmpty()) {
//            loading(false)
//            return
//        }

        val products: MutableList<ProductWrapperDTO> = mutableListOf()

        for (p in cart.products)
            products.add(ProductWrapperDTO(p.first.id, p.second))

        // Todo: replace
        productAPI.addProducts(userId, products).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                loading(false)
                startActivity(intent)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@CashRegisterActivity, R.string.cart_saving_failed, Toast.LENGTH_SHORT).show()
                t.printStackTrace()
                loading(false)
                // Todo: stay on page if failed
                startActivity(intent)
            }
        })
    }
}
