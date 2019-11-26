package com.example.cashmanager.data.dto

import com.example.cashmanager.data.model.Cart
import com.example.cashmanager.data.model.Product
import java.util.*

/***
 * DTO for order object
 */
class OrderDTO(cart: Cart) {
    var id: Int? = null

    var total: Double? = null

    var poid: Double? = null

    var payementDate: Date? = null

    var orderStatusDto: OrderStatusDTO? = null

    var payementDto: PaymentDTO? = null

    var productDtos: MutableList<Product>? = null

    init {
        total = cart.billTotal
        orderStatusDto = OrderStatusDTO("pending")
        productDtos = mutableListOf()
        for (products in cart.products) {
            for (i in 1..products.second)
                productDtos?.add(products.first)
        }
    }
}

