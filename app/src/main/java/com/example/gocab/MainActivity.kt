package com.example.gocab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.gocab.navgraph.AuthNavGraph
import com.example.gocab.ui.theme.GoCabTheme
import com.example.gocab.viewmodel.PaymentViewModel
import com.google.android.libraries.places.api.Places
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity(), PaymentResultListener {

    private val paymentViewModel : PaymentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Checkout.preload(applicationContext)

        enableEdgeToEdge()




        val mapsKey = getString(com.example.gocab.R.string.maps_api_key)
        if (!Places.isInitialized() && mapsKey.isNotBlank()) {
            Places.initialize(applicationContext, mapsKey)
        }

        setContent {
            GoCabTheme {

                AuthNavGraph()
            }
        }
    }

    override fun onPaymentSuccess(paymentId: String?) {
        paymentViewModel.onPaymentSuccess(paymentId)
    }

    override fun onPaymentError(code: Int, message: String?) {
        paymentViewModel.onPaymentError(message)
    }
}
