package com.example.cashmanager.data.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Cheque information to be sent to the API
 */
class PaymentChequeDTO (@SerializedName("cheque_id") val id: String,
                        @SerializedName("cheque_value") val value: Double
    ) : Serializable