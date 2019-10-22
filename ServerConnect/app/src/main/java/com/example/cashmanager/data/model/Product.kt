package com.example.cashmanager.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Product(@SerializedName("id") val id: Int,
              @SerializedName("libelle") val description: String,
              @SerializedName("prix") val price: Double)
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