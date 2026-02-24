package com.example.gocab.screens


import android.app.Activity
import android.content.Context
import android.widget.EditText
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gocab.module.PaymentState
import com.example.gocab.viewmodel.PaymentViewModel
import kotlinx.coroutines.delay
@Composable
fun PaymentScreen(
    amount: Double?,
    onPaymentDone: () -> Unit,
    paymentViewModel: PaymentViewModel
) {

    val context = LocalContext.current
    val paymentState by paymentViewModel.paymentState.collectAsState()

    LaunchedEffect(paymentState) {
        if (paymentState is PaymentState.Success) {
            delay(1000)
            onPaymentDone()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "Payment",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Total Amount")

            Text(
                text = "₹${amount?.toInt() ?: 0}",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val finalAmount = amount ?: 0.0

                    if (finalAmount > 0) {
                        paymentViewModel.startPayment(
                            context as Activity,
                            finalAmount
                        )
                    }
                }
            ) {
                Text("Pay the amount")
            }

            Spacer(Modifier.height(10.dp))

            when (val state = paymentState) {
                is PaymentState.Success -> {
                    Text("Payment Successful ${state.paymentId}")
                }
                is PaymentState.Error -> {
                    Text("Payment Failed ${state.message}")
                }
                else -> {}
            }
        }
    }
}