package com.example.cashmanager.data.model

import java.io.Serializable

class Product(val id: Int,
              val description: String,
              val price: Float)
: Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Product

        if (id != other.id) return false
        if (description != other.description) return false
        if (price != other.price) return false

        return true
    }
}