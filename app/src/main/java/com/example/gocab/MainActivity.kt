package com.example.gocab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.gocab.module.AppApiKey
import com.example.gocab.module.PaymentState
import com.example.gocab.navgraph.AuthNavGraph
import com.example.gocab.screens.GoCabMap
import com.example.gocab.screens.SignupScreen
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




        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, AppApiKey.KEY_ID)
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
