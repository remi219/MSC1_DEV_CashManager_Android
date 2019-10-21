package com.example.cashmanager.ui.cashRegister

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cashmanager.R
import com.example.cashmanager.data.model.Product
import java.text.NumberFormat

class CashRegisterAdapter(private val products: MutableList<Pair<Product, Int>>, private val context: Context)
    : RecyclerView.Adapter<CashRegisterAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ProductViewHolder(inflater, parent, context)
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position].first, products[position].second)
    }

    class ProductViewHolder(inflater: LayoutInflater, parent: ViewGroup, private val context: Context) : RecyclerView.ViewHolder(inflater.inflate(
        R.layout.row_cash_register_view, parent, false)) {

        private var productView: TextView? = null
        private var priceView: TextView? = null
        private var removeBtn: ImageButton? = null

        init {
            productView = itemView.findViewById(R.id.product_name)
            priceView = itemView.findViewById(R.id.total_price)
            removeBtn = itemView.findViewById(R.id.remove_btn)
        }

        fun bind(product: Product, quantity: Int) {
            val format = NumberFormat.getCurrencyInstance()

            productView?.text = product.description
            priceView?.text = context.resources.getString(
                R.string.full_price,
                format.format(product.price),
                quantity,
                format.format(product.price * quantity))
            removeBtn?.setOnClickListener {
                // Todo
            }
        }
    }
}