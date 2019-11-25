package com.example.cashmanager.ui.productPicker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashmanager.R
import com.example.cashmanager.data.model.Cart
import com.example.cashmanager.data.model.Product
import com.example.cashmanager.service.ProductService
import com.example.cashmanager.service.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult


class ProductPickerActivity : AppCompatActivity() {

    val activity = this
    var fullCart: Cart = Cart()
    private lateinit var productAPI : ProductService

    private lateinit var productRecyclerView : RecyclerView
    lateinit var progressBar : ProgressBar
    private lateinit var scanBtn : Button
    private lateinit var cancelBtn : Button
    private lateinit var addToCartButton : Button

    lateinit var cart: Cart
    lateinit var availableProducts : MutableList<Product>
    var productList : MutableList<Pair<Product, Int>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_picker)

        productRecyclerView = findViewById(R.id.productPicker_recyclerView)
        progressBar = findViewById(R.id.progressBar)
        scanBtn = findViewById(R.id.scan_barcode_btn)
        cancelBtn = findViewById(R.id.cancelButton)
        addToCartButton = findViewById(R.id.addToCartButton)

        val prefs = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        productAPI = ServiceBuilder.createService(ProductService::class.java, prefs.getString("token", ""))

        cart = intent.getSerializableExtra("cart") as Cart? ?: Cart()

        productRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        loadProductList()
    }

    /**
     * On qr code read activity reader
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if(result != null){
            println("Contents: " + result.contents + " " + result.contents.toIntOrNull())
            if(result.contents.toIntOrNull() != null){
                try {
                    addProduct(result.contents.toInt())
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Toast.makeText(this, resources.getString(R.string.invalid_barcode), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, resources.getString(R.string.invalid_barcode), Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            scanBtn.isEnabled = false
            cancelBtn.isEnabled = false
            addToCartButton.isEnabled = false
        } else {
            progressBar.visibility = View.GONE
            scanBtn.isEnabled = true
            cancelBtn.isEnabled = true
            addToCartButton.isEnabled = true
        }
    }

    /***
     * Start scanning activity
     */
    fun scanQRcode(v : View) {
        run {
            IntentIntegrator(this@ProductPickerActivity).initiateScan()
        }
    }

    /**
     * Load the list of available products from the API
     */
    private fun loadProductList() {
        loading(true)
        val call = productAPI.availableProducts()

        call.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                availableProducts = response.body() as MutableList<Product>
                for (product in availableProducts)
                    fullCart.products.add(Pair(product, 0))
                setProductAdapter()
                loading(false)
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                t.printStackTrace()
//                println(t.message)
//                println(call.request().url())
//                println(call.request().method())

                availableProducts = mutableListOf(
                    Product(1, "Water bottle", 0.5),
                    Product(2, "Soda bottle", 1.0),
                    Product(3, "Ice Tea bottle", 1.25),
                    Product(4, "Coffee bottle", 1.5),
                    Product(5, "Orange juice bottle", 0.75),
                    Product(6, "Apple juice bottle", 0.69),
                    Product(7, "Pear juice bottle", 1.12),
                    Product(8, "Item with a very very very very long name on several line", 0.99),
                    Product(9, "Banana juice bottle", 0.95),
                    Product(10, "Toto", 99.995644)
                )
                for (product in availableProducts)
                    fullCart.products.add(Pair(product, 0))
                setProductAdapter()
                Toast.makeText(activity, "API unavailable, loading mocked products instead", Toast.LENGTH_SHORT).show()
                loading(false)
            }
        })
    }

    fun setProductAdapter() {
        fullCart.prefillQuantity(cart)

        productRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ProductPickerAdapter(fullCart)
        }
    }

    /**
     * Add a product from a barcode
     * @param productId id collected from barcode
     */
    private fun addProduct(productId: Int) {
        for (i in 0 until fullCart.size) {
            if (productId == fullCart.products[i].first.id) {
                fullCart.addProduct(i)
                // Refresh recycler view
                productRecyclerView.adapter?.notifyItemChanged(i)
                Toast.makeText(this,
                    resources.getString(R.string.added_product, fullCart.products[i].first.description),
                    Toast.LENGTH_SHORT)
                    .show()
                return
            }
        }
        Toast.makeText(this, resources.getString(R.string.product_not_found), Toast.LENGTH_SHORT).show()
    }

    /**
     * Set the selected products to the cart
     */
    fun addProductsToCart(v : View) {
        val cart = Cart()
        cart.products = fullCart.getSelectedProducts()

        val intent = Intent().apply {
            putExtra("cart", cart as Serializable)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    fun cancel(v : View) {
        onBackPressed()
    }
}
