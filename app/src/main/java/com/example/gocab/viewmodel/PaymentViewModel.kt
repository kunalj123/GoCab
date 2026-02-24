package com.example.gocab.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.gocab.config.RazorpayConfig
import com.example.gocab.module.PaymentState
import com.razorpay.Checkout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor() : ViewModel(){

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState : StateFlow<PaymentState> = _paymentState.asStateFlow()


    fun startPayment(activity : Activity, amount : Double , description : String = "RidePayment" ){


        try{

            val options = JSONObject().apply {
                put("name", "GoCab")
                put("description", description)
                put("currency", "INR")
                put("amount", (amount * 100).toLong())

                put("prefill", JSONObject().apply {
                    put("email", "test@example.com")
                    put("contact", "9876543210")
                })

                put("theme", JSONObject().apply {
                    put("color", "#3399cc")
                })

                Log.d("PAY_DEBUG", "Amount in rupees = $amount")
                Log.d("PAY_DEBUG", "Amount in paise = ${(amount * 100).toLong()}")



            }

            val checkout = Checkout()
            checkout.setKeyID(RazorpayConfig.KEY_ID)
            checkout.open(activity,options)



        }catch(e : Exception){
            _paymentState.value = PaymentState.Error(
                e.message ?: "Payment initialization failed"
            )
        }

    }

    fun onPaymentSuccess(paymentId : String?){
        _paymentState.value = PaymentState.Success(paymentId)
    }

    fun onPaymentError(message : String?){
        _paymentState.value = PaymentState.Error(message)
    }
}