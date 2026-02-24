package com.example.gocab.navgraph

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable

import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gocab.screens.GoCabMap
import com.example.gocab.screens.LoginScreen
import com.example.gocab.screens.OnboardingScreen

import com.example.gocab.screens.PaymentScreen
import com.example.gocab.screens.RideSimulationScreen
import com.example.gocab.screens.SignupScreen
import com.example.gocab.screens.SplashScreen
import com.example.gocab.viewmodel.MapViewModel
import com.example.gocab.viewmodel.PaymentViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AuthNavGraph() {
    val auth = FirebaseAuth.getInstance()
    val navController = rememberNavController()

    val activity = LocalContext.current as ComponentActivity
    val appContext = LocalContext.current
    val mapViewModel: MapViewModel = hiltViewModel(activity)
    val paymentViewModel: PaymentViewModel = hiltViewModel(activity)

    val prefs = appContext.getSharedPreferences("gocab_prefs", Context.MODE_PRIVATE)

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onFinished = {
                    val isFirstLaunch = prefs.getBoolean("is_first_launch", true)
                    when {
                        isFirstLaunch -> navController.navigate("onboarding") {
                            popUpTo("splash") { inclusive = true }
                        }

                        auth.currentUser != null -> navController.navigate("home") {
                            popUpTo("splash") { inclusive = true }
                        }

                        else -> navController.navigate("login") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("onboarding") {
            OnboardingScreen(
                onContinue = {
                    prefs.edit().putBoolean("is_first_launch", false).apply()
                    if (auth.currentUser != null) {
                        navController.navigate("home") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    } else {
                        navController.navigate("login") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("login") {

            LoginScreen(
                onNavigateToSignUp = { navController.navigate("signup") },
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
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )

        }

        composable("ride") {

            RideSimulationScreen(
                viewModel = mapViewModel,

                onRideCompleted = {
                    navController.navigate("payment") {
                        popUpTo("ride") { inclusive = true }
                    }
                }
            )
        }


        composable("payment") {

            PaymentScreen(
                amount = mapViewModel.price.value ?: 0.0,
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

