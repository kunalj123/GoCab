package com.example.gocab.screens


import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gocab.module.PaymentState
import com.example.gocab.viewmodel.PaymentViewModel
import kotlinx.coroutines.delay
@Composable
fun PaymentScreen(
    amount: Double,
    onPaymentDone: () -> Unit,
    paymentViewModel: PaymentViewModel
) {

    val context = LocalContext.current
    val paymentState by paymentViewModel.paymentState.collectAsState()
    val baseFare = amount * 0.8
    val taxes = amount * 0.2

    LaunchedEffect(paymentState) {
        if (paymentState is PaymentState.Success) {
            delay(1000)
            onPaymentDone()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = "Payment", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Fare Breakdown", style = MaterialTheme.typography.titleMedium)
                    Text("Base Fare: ₹${baseFare.toInt()}")
                    Text("Taxes & Fees: ₹${taxes.toInt()}")
                    Text("Total: ₹${amount.toInt()}", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                if (amount > 0) paymentViewModel.startPayment(context as Activity, amount)
            }) {
                Text("Pay ₹${amount.toInt()}")
            }

            Spacer(Modifier.height(10.dp))

            when (val state = paymentState) {
                is PaymentState.Success -> Text("Payment Successful ${state.paymentId}")
                is PaymentState.Error -> Text("Payment Failed ${state.message}")
                else -> Unit
            }
        }
    }

}