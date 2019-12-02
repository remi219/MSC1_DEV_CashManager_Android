package com.example.cashmanager.data.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Card information to be sent to the API
 */
class PaymentCardDTO(@SerializedName("customerId") val customerId: Int,
                     @SerializedName("card_id") val id: String,
                     @SerializedName("order_id") val orderId: Int
    ) : Serializable