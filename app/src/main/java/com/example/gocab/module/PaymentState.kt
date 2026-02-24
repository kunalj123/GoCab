package com.example.gocab.module

import com.example.gocab.config.RazorpayConfig

sealed class PaymentState {

    data object Idle : PaymentState()
    data class Success(val paymentId : String?): PaymentState()
    data class Error(val message : String?): PaymentState()
}