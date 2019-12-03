package com.example.cashmanager.data.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Cheque information to be sent to the API
 */
class PaymentChequeDTO (val customerId: Int,
                        val id: String,
                        val chequeValue: Double,
                        val orderId: Int
    ) : Serializable