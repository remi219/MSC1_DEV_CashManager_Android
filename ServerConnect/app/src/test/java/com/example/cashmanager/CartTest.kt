package com.example.cashmanager

import com.example.cashmanager.data.model.Product
import com.example.cashmanager.data.model.Cart
import org.junit.Test
import org.junit.Assert.*
import java.lang.Boolean.TRUE

class CartTest {
    private val cart : Cart = Cart()

    @Test
    fun testAddProduct(){
        val initialCartSize: Int = cart.products.size
        cart.products.add(Pair(Product(1, "TestProduct", "34.50".toDouble()), 1))
        val newCartSize: Int = cart.products.size
        assertEquals(initialCartSize + 1, newCartSize)
    }

    @Test
    fun testReset() {
        cart.products.add(Pair(Product(1, "TestProduct", "34.50".toDouble()), 1))
        cart.reset()
        assertEquals(TRUE, cart.products.isEmpty())
    }
}