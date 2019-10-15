package com.example.cashmanager.data.model

import java.io.Serializable

class Cart : Serializable {
    var products: MutableList<Pair<Product, Int>> = mutableListOf()

    /**
     * Total value of the cart
     */
    val billTotal: Float
        get() {
            var total = 0f
            for(product in products)
                total += product.first.price * product.second
            return total
        }

    val size: Int
        get() = products.size

    fun reset() {
        products.clear()
    }
}