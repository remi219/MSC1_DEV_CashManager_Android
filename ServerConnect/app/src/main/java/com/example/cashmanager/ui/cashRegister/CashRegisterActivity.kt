package com.example.cashmanager.ui.cashRegister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.cashmanager.R
import com.example.cashmanager.ui.bill.BillActivity
import com.example.cashmanager.ui.productPicker.ProductPickerActivity

class CashRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cash_register)
    }

    fun openProductPicker(v: View) {
        val intent = Intent(this, ProductPickerActivity::class.java)
        startActivity(intent)
    }

    /**
     * Reset the content of the cart
     */
    fun reset(v: View) {
        // Todo: clear the list of products in the cart
    }

    /**
     * Proceed to the next activity, displaying the bill total
     */
    fun proceed(v: View) {
        val intent = Intent(this, BillActivity::class.java)
        startActivity(intent)
    }
}
