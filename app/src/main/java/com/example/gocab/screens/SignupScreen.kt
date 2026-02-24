package com.example.gocab.screens


import android.accounts.Account
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gocab.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SignupScreen(
    onSignupSuccess: () -> Unit,
    onNavigateBack: () -> Unit
){

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

//    data class User(
//        val userId : String = "",
//        val name : String = "",
//        val email : String = "",
//        val phoneNumber : String = "",
//        val createdAt : Long = System.currentTimeMillis()
//    )


    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf("false") }
    val context = LocalContext.current







    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Image(
            painter = painterResource(R.drawable.gocab_logo),
            contentDescription = "GoCab Logo",
            modifier = Modifier.padding(bottom = 24.dp)
                .size(
                    140.dp)
                .clip(RoundedCornerShape(50.dp))
                ,
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it},
            label = { Text("Full Name") },
            leadingIcon = {
                Icon(Icons.Default.Person,null)
            }

        )

        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = email,
            onValueChange = { email = it},
            label =  { Text("Email")},
            leadingIcon = {
                Icon(Icons.Default.Email,null)
            }

        )

        Spacer(modifier = Modifier.height(16.dp))



        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            label = { Text("Password")},
            leadingIcon = {
                Icon(Icons.Default.Lock,null)
            },
            trailingIcon = {

                val icon  = if(passwordVisible)
                    Icons.Default.Visibility
                else
                    Icons.Default.VisibilityOff


                IconButton(onClick = {
                    passwordVisible = !passwordVisible
                }) {
                    Icon(
                        icon,
                        "TogglePassword"
                    )
                }

            },
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation()
        )




        Button(modifier = Modifier.padding(20.dp),
            onClick =  {

                if (name.isBlank() || email.isBlank() || password.isBlank()){
                    Toast.makeText(
                        context,
                        "Please enter all fields",
                        Toast.LENGTH_LONG
                    ).show()

                    return@Button
                }

                auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener {
                        task ->

                        if(task.isSuccessful){
                            Toast.makeText(
                                context,
                                "Account Created Successfully ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }else{
                            Toast.makeText(
                                context,
                                task.exception?.message ?: "Signup Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        ) {
            Text("SignUp")
        }



    }


}


//@Preview(
//    showBackground = true,
//    showSystemUi = true
//)
//@Composable
//fun SignupScreenPreview(){
//    SignupScreen()
//}
