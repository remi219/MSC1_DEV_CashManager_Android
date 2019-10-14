package com.example.cashmanager.ui.productPicker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cashmanager.R
import com.example.cashmanager.data.model.Product
import java.text.NumberFormat

class ProductPickerAdapter(private val products: List<Product>) : RecyclerView.Adapter<ProductPickerAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ProductViewHolder(inflater, parent)
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    class ProductViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.row_product_picker, parent, false)) {

        private var productView: TextView? = null
        private var priceView: TextView? = null

        init {
            productView = itemView.findViewById(R.id.product_name)
            priceView = itemView.findViewById(R.id.individual_price)
        }

        fun bind(product: Product) {
            val format = NumberFormat.getCurrencyInstance()

            productView?.text = product.description
            priceView?.text = format.format(product.price)
        }
    }
}