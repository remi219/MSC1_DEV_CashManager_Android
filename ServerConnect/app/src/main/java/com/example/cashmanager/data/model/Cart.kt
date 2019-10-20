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

    /**
     * Set the product quantity. Cannot be negative or above 999
     */
    fun setQuantity(position: Int, quantity: Int) {
        var qty: Int = 0
        if (quantity > 999)
            qty = 999
        else if (quantity > 0)
            qty = quantity
        products[position] = products[position].copy(second = qty)
    }

    fun addProduct(position: Int) {
        val qty: Int = products[position].second
        products[position] = products[position].copy(second = if (qty > 99) 99 else qty + 1)
    }

    fun substractProduct(position: Int) {
        val qty: Int = products[position].second
        products[position] = products[position].copy(second = if (qty == 0) 0 else qty - 1)
    }

    /**
     * Return a list of products excluding those with a zero quantity
     */
    fun getSelectedProducts(): MutableList<Pair<Product, Int>> {
        val productList = mutableListOf<Pair<Product, Int>>()

        for(pair in products) {
            if (pair.second > 0)
                productList.add(pair.copy())
        }
        return productList
    }

    /**
     * Prefill the cart with an existing cart
     * @param cart: Pre-existing cart
     */
    fun prefillQuantity(cart : Cart) {
        for (i in 0 until cart.size) {
            for (j in 0 until size) {
                println(cart.products[i].first.description + " " + products[j].first.description)
                if (cart.products[i].first == products[j].first)
                    products[j] = cart.products[i].copy()
            }
        }
    }
}