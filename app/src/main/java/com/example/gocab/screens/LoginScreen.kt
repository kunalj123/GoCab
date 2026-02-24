package com.example.gocab.screens



import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.gocab.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit
){
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
        ) {

        Image(
            painter = painterResource(R.drawable.gocab_logo),
            contentDescription = "GoCab Logo",
            modifier = Modifier.padding(bottom = 24.dp)
                .size(140.dp)
                .clip(RoundedCornerShape(50.dp))
        )

        Spacer(Modifier.height(24.dp))


        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            label = { Text("Email") },
            leadingIcon = {
                Icon(
                    Icons.Default.Email,
                    null)
            }

        )

        Spacer(Modifier.height(16.dp))


        OutlinedTextField(
            value =  password,
            onValueChange = {password = it},
            label = { Text("Password") },
            trailingIcon = {

                val icon = if(passwordVisible)
                    Icons.Default.Visibility
                else
                    Icons.Default.VisibilityOff

                IconButton(onClick =  {
                    passwordVisible = !passwordVisible
                }) {

                    Icon(
                        icon,
                        null
                    )
                }

            },
            visualTransformation =
                if(passwordVisible)
                    VisualTransformation.None
            else
                    PasswordVisualTransformation()

        )


        Button(onClick =  {

            if(email.isBlank() || password.isBlank()){

                Toast.makeText(
                    context,
                    "Please enter Email and Password",
                    Toast.LENGTH_LONG
                ).show()

                return@Button
            }

            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    task ->

                    if(task.isSuccessful){
                        onLoginSuccess()
                    }else{
                        Toast.makeText(
                            context,
                            task.exception?.message ?: "Login Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

        }){
            Text("Login")
        }


        TextButton(
            onClick = {
                onNavigateToSignUp()
            }
        ) {
            Text("Don't have account? Signup")
        }
    }
}