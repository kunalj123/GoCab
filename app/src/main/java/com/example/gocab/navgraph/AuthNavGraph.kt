package com.example.gocab.navgraph

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gocab.screens.GoCabMap
import com.example.gocab.screens.LoginScreen
import com.example.gocab.screens.PaymentScreen
import com.example.gocab.screens.RideSimulationScreen
import com.example.gocab.screens.SignupScreen
import com.example.gocab.viewmodel.MapViewModel
import com.example.gocab.viewmodel.PaymentViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AuthNavGraph() {

    val auth = FirebaseAuth.getInstance()
    val navController = rememberNavController()

    val activity = LocalContext.current as ComponentActivity
    val mapViewModel: MapViewModel = hiltViewModel(activity)
    val paymentViewModel: PaymentViewModel = hiltViewModel(activity)


    NavHost(
        navController = navController,
        startDestination =
            if (auth.currentUser != null)
                "home"
            else
                "login"

    ) {

        composable("login") {

            LoginScreen(
                onNavigateToSignUp = {

                    navController.navigate("signup")

                },
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )


        }


        composable("signup") {

            SignupScreen(
                onSignupSuccess = {
                    navController.navigate("home")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )

        }

        composable("home") {
            GoCabMap(
                navController,
                viewModel = mapViewModel
            )
        }

        composable(
            route = "ride/{amount}",
            arguments = listOf(
                navArgument("amount") { type = NavType.FloatType }
            )
        ) { backStackEntry ->

            val amount = backStackEntry.arguments
                ?.getFloat("amount")
                ?.toDouble() ?: 0.0

            RideSimulationScreen(
                viewModel = mapViewModel,
                rideAmount = amount,
                onRideCompleted = {
                    navController.navigate("payment/${amount.toFloat()}") {
                        popUpTo("ride/{amount}") { inclusive = true }
                    }
                }
            )
        }


        composable(
            route = "payment/{amount}",
            arguments = listOf(
                navArgument("amount") { type = NavType.FloatType }
            )
        ) { backStackEntry ->

            val amount = backStackEntry.arguments?.getFloat("amount")?.toDouble()

            PaymentScreen(
                amount = amount,
                onPaymentDone = {
                    mapViewModel.clearRoute()
                    mapViewModel.resetRide()

                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                paymentViewModel = paymentViewModel
            )
        }


    }
}

