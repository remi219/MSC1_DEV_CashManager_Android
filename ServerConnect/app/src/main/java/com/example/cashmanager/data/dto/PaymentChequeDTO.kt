package com.example.cashmanager.data.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Cheque information to be sent to the API
 */
class PaymentChequeDTO (@SerializedName("customerId") val customerId: Int,
                        @SerializedName("cheque_id") val id: String,
                        @SerializedName("cheque_value") val value: Double,
                        @SerializedName("order_id") val orderId: Int
    ) : Serializable