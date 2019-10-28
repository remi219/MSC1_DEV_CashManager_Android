package com.example.cashmanager.ui.productPicker

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashmanager.R
import com.example.cashmanager.data.model.Cart
import com.example.cashmanager.data.model.Product
import com.example.cashmanager.service.ProductService
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class ProductPickerActivity : AppCompatActivity() {

    val activity = this
    var fullCart: Cart = Cart()
    val productAPI : ProductService by inject()

    lateinit var productRecyclerView : RecyclerView
    lateinit var progressBar : ProgressBar

    lateinit var cart: Cart
    lateinit var availableProducts : MutableList<Product>
    var productList : MutableList<Pair<Product, Int>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_picker)

        productRecyclerView = findViewById(R.id.productPicker_recyclerView)
        progressBar = findViewById(R.id.progressBar)

        cart = intent.getSerializableExtra("cart") as Cart? ?: Cart()

        loadProductList()
    }

    /**
     * Load the list of available products from the API
     */
    fun loadProductList() {
        progressBar.visibility = View.VISIBLE
        val call = productAPI.availableProducts()

        call.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                availableProducts = response.body() as MutableList<Product>
                for (product in availableProducts)
                    fullCart.products.add(Pair(product, 0))
                setProductAdapter()
                progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                t.printStackTrace()
                println(t.message)
                println(call.request().url())
                println(call.request().method())

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
                progressBar.visibility = View.GONE
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
