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
    var fullCart: Cart = Cart()

    lateinit var productRecyclerView : RecyclerView

    lateinit var availableProducts : MutableList<Product>
    var productList : MutableList<Pair<Product, Int>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_picker)

        val activity = this

        productRecyclerView = findViewById(R.id.productPicker_recyclerView)

        LoadProductList()

        val cart = intent.getSerializableExtra("cart") as Cart? ?: Cart()
        fullCart.prefillQuantity(cart)

        productRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ProductPickerAdapter(fullCart)
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
            Product(6, "Apple juice bottle", 0.69f),
            Product(7, "Pear juice bottle", 1.12f),
            Product(8, "Strawberry juice bottle", 0.99f),
            Product(9, "Banana juice bottle", 0.95f),
            Product(10, "Toto", 99.995644f)
        )
        for (product in availableProducts)
            fullCart.products.add(Pair(product, 0))
        isBusy = false
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
