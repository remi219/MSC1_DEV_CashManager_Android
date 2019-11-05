package com.example.cashmanager.ui.productPicker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
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
        private var quantityView: TextView? = null
        private var addBtn: Button? = null
        private var subBtn: Button? = null

        init {
            productView = itemView.findViewById(R.id.product_name)
            priceView = itemView.findViewById(R.id.individual_price)
            quantityView = itemView.findViewById(R.id.quantity)
            addBtn = itemView.findViewById(R.id.addBtn)
            subBtn = itemView.findViewById(R.id.subBtn)
        }

        fun bind(fullCart: Cart, position: Int) {
            val format = NumberFormat.getCurrencyInstance()

            productView?.text = fullCart.products[position].first.description
            priceView?.text = format.format(fullCart.products[position].first.price)
            quantityView?.text = fullCart.products[position].second.toString()

            addBtn?.setOnClickListener {
                fullCart.addProduct(position)
                quantityView?.text = fullCart.products[position].second.toString()
            }

            subBtn?.setOnClickListener {
                fullCart.subtractProduct(position)
                quantityView?.text = fullCart.products[position].second.toString()
            }
        }
    }
}