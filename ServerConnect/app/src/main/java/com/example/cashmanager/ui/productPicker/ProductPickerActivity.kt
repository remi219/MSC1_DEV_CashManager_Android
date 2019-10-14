package com.example.cashmanager.ui.productPicker

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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
        setResult(Activity.RESULT_OK, intent)
    }

    fun cancel(v : View) {
        setResult(Activity.RESULT_OK, Intent())
    }
}
