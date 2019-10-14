package com.example.cashmanager.ui.productPicker

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.cashmanager.R

class ProductPickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_picker)
    }

    /**
     * Load the list of available products from the API
     */
    fun LoadProductList() {
        // productList = ...
    }

    /**
     * Set the selected products to the cart
     */
    fun addProductsToCart(v : View) {
        val intent = Intent().apply {
            //putExtra("cart", productList);
        }
        setResult(RESULT_OK, intent)
    }

    fun cancel(v : View) {
        setResult(RESULT_OK, Intent())
    }
}
