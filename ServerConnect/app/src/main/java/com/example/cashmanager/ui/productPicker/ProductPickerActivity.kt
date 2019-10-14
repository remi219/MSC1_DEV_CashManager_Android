package com.example.cashmanager.ui.productPicker

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashmanager.R
import com.example.cashmanager.data.model.Product

class ProductPickerActivity : AppCompatActivity() {

    var isBusy : Boolean = true

    lateinit var productList : RecyclerView

    lateinit var availableProducts : MutableList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_picker)

        val activity = this
        //

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
        val intent = Intent().apply {
            //putExtra("cart", productList);
        }
        setResult(RESULT_OK, intent)
        onBackPressed()
    }

    fun cancel(v : View) {
        setResult(RESULT_OK, Intent())
        onBackPressed()
    }
}
