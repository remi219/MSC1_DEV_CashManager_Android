package com.example.cashmanager.ui.cashRegister

import android.app.Activity
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cashmanager.R
import com.example.cashmanager.data.model.Product
import com.example.cashmanager.ui.bill.BillActivity
import com.example.cashmanager.ui.productPicker.ProductPickerActivity



class CashRegisterActivity : AppCompatActivity() {

    lateinit var productList: MutableMap<Product, Int>

    lateinit var noArticleTextview : TextView
    lateinit var cartRecyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cash_register)

        noArticleTextview = findViewById(R.id.no_article_textview)
        cartRecyclerView = findViewById(R.id.articles_list)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                // OR
                // String returnedResult = data.getDataString();

                // Todo: retrieve selected articles from cart
                // No added product
                noArticleTextview.visibility = View.VISIBLE
                cartRecyclerView.visibility = View.GONE
                // Added products
                noArticleTextview.visibility = View.GONE
                cartRecyclerView.visibility = View.VISIBLE

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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
        noArticleTextview.visibility = View.VISIBLE
        cartRecyclerView.visibility = View.GONE
    }

    /**
     * Proceed to the next activity, displaying the bill total
     */
    fun proceed(v: View) {
        // Todo: disable if no product
        val intent = Intent(this, BillActivity::class.java)
        startActivity(intent)
    }
}
