package com.example.cashmanager

import com.example.cashmanager.data.model.Product
import com.example.cashmanager.data.model.Cart
import org.junit.Test
import org.junit.Assert.*

class CartTest {
    private val cart : Cart = Cart()

    @Test
    fun testReset() {
        val products: MutableList<Pair<Product, Int>> = mutableListOf()
        products.add(Pair(Product(1, "TestProduct", "34.50".toFloat()), 1))
        cart.reset()
        assert(products.isEmpty())
    }
}