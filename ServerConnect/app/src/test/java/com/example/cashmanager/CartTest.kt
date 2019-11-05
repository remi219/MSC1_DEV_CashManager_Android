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

    @Test
    fun testAddProduct2() {
        cart.products.add(Pair(Product(1, "TestProduct", "34.50".toDouble()), 1))
        cart.addProduct(0)
        cart.addProduct(0)
        cart.addProduct(0)
        assertEquals(TRUE, cart.products[0].second == 4)
    }

    @Test
    fun testSubtractProducts() {
        cart.products.add(Pair(Product(1, "TestProduct", "34.50".toDouble()), 3))
        cart.subtractProduct(0)
        cart.subtractProduct(0)
        assertEquals(TRUE, cart.products[0].second == 1)
        cart.subtractProduct(0)
        cart.subtractProduct(0)
        cart.subtractProduct(0)
        cart.subtractProduct(0)
        assertEquals(TRUE, cart.products[0].second == 0)
    }

    @Test
    fun testGetSelectedProducts() {
        cart.products.add(Pair(Product(1, "TestProduct", "34.50".toDouble()), 25))
        cart.products.add(Pair(Product(2, "TestProduct", "34.50".toDouble()), 1))
        cart.products.add(Pair(Product(3, "TestProduct", "34.50".toDouble()), 0))
        cart.products.add(Pair(Product(4, "TestProduct", "34.50".toDouble()), -15))

        val selectedCart = cart.getSelectedProducts()
        assertEquals(TRUE, selectedCart.size == 2)
    }

    @Test
    fun testPrefill() {
        cart.products.add(Pair(Product(1, "TestProduct", "34.50".toDouble()), 25))
        cart.products.add(Pair(Product(2, "TestProduct", "34.50".toDouble()), 13))

        val myCart = Cart()
        myCart.products.add(Pair(Product(1, "TestProduct", "34.50".toDouble()), 0))
        myCart.products.add(Pair(Product(2, "TestProduct", "34.50".toDouble()), 0))
        myCart.prefillQuantity(cart)
        assertEquals(TRUE, myCart.products[0].second == 25)
        assertEquals(TRUE, myCart.products[1].second == 13)
    }
}