package com.example.cashmanager.data.dto

import com.example.cashmanager.data.model.Product

/**
 * Dto to retreive customer's cart content
 */
data class ProductQuantityDTO(val product: Product, val quantity: Int)