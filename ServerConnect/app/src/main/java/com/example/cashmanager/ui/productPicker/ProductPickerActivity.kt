package com.example.cashmanager.ui.productPicker

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashmanager.R
import com.example.cashmanager.data.model.Cart
import com.example.cashmanager.data.model.Product
import java.io.Serializable

class ProductPickerActivity : AppCompatActivity() {

    var isBusy : Boolean = true

    lateinit var productList : RecyclerView

    lateinit var availableProducts : MutableList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_picker)

        val activity = this

        productList = findViewById(R.id.productPicker_recyclerView)

        LoadProductList()

        productList.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ProductPickerAdapter(availableProducts)
        }
    }

    /**
     * Load the list of available products from the API
     */
    fun LoadProductList() {
        // Todo: load from API
        availableProducts = mutableListOf(
            Product(1, "Water bottle", 0.5f),
            Product(2, "Soda bottle", 1f),
            Product(3, "Ice Tea bottle", 1.25f),
            Product(4, "Coffee bottle", 1.5f),
            Product(5, "Orange juice bottle", 0.75f),
            Product(5, "Orange juice bottle", 0.75f),
            Product(5, "Orange juice bottle", 0.75f),
            Product(5, "Orange juice bottle", 0.75f),
            Product(5, "Orange juice bottle", 0.75f),
            Product(5, "Toto", 99.995644f)
        )
        isBusy = false
    }

    /**
     * Set the selected products to the cart
     */
    fun addProductsToCart(v : View) {
        val cart = Cart()
        cart.products = mutableListOf(
            Pair(Product(1, "Toto", 2f), 1),
            Pair(Product(2, "Tata", 5f), 3),
            Pair(Product(3, "Titi", 6f), 5)
        )

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
