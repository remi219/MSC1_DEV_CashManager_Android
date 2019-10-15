package com.example.cashmanager.ui.productPicker

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cashmanager.R
import com.example.cashmanager.data.model.Cart
import java.text.NumberFormat

class ProductPickerAdapter(private val fullCart : Cart) : RecyclerView.Adapter<ProductPickerAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ProductViewHolder(inflater, parent)
    }

    override fun getItemCount() = fullCart.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(fullCart, position)
    }

    class ProductViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.row_product_picker, parent, false)) {

        private var productView: TextView? = null
        private var priceView: TextView? = null
        private var quantityView: EditText? = null

        init {
            productView = itemView.findViewById(R.id.product_name)
            priceView = itemView.findViewById(R.id.individual_price)
            quantityView = itemView.findViewById(R.id.quantity)
        }

        fun bind(fullCart: Cart, position: Int) {
            val format = NumberFormat.getCurrencyInstance()

            productView?.text = fullCart.products[position].first.description
            priceView?.text = format.format(fullCart.products[position].first.price)
            quantityView?.setText(fullCart.products[position].second.toString())

            quantityView?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    try {
                        fullCart.setQuantity(position, p0.toString().toInt())
                    } catch(ex: Exception) {
                        fullCart.setQuantity(position, 0)
                    }
                }
            })
        }
    }
}