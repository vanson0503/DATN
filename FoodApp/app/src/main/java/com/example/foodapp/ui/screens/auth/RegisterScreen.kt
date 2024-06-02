package com.example.foodapp.ui.screens.auth

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.foodapp.R
import com.example.foodapp.data.api.RetrofitClient.authApiService
import com.example.foodapp.data.repository.AuthRepository
import com.example.foodapp.ui.components.FullNameTextField
import com.example.foodapp.ui.components.GoogleLoginButton
import com.example.foodapp.ui.components.PasswordTextField
import com.example.foodapp.ui.components.EmailOrPhoneTextField
import com.example.foodapp.ui.components.TwitterLoginButton
import com.example.foodapp.ui.theme.SoftCoral
import com.example.foodapp.utils.isValidEmail
import com.example.foodapp.utils.isValidPhoneNumber
import com.example.foodapp.viewmodel.AuthViewModel
import com.example.foodapp.viewmodel.GoogleAuthUiClient
import com.example.foodapp.viewmodel.SignInViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun RegisterScreen(
    lifecycleScope : LifecycleCoroutineScope,
    googleAuthUiClient: GoogleAuthUiClient,
    forgotPassword : () -> Unit,
    login : () -> Unit,
    onRegisterSuccess : (String) -> Unit,
    registerGoogleSuccess:()->Unit
) {
    val context = LocalContext.current
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repassword by remember { mutableStateOf("") }
    val authRepository = AuthRepository(authApiService)
    var isLoading by remember { mutableStateOf(false) }
    val viewModel = viewModel<SignInViewModel>()
    val state by viewModel.state.collectAsState()
    val sharedPreferences = context.getSharedPreferences("customer_data", Context.MODE_PRIVATE)

    val authViewModel: AuthViewModel = remember {
        AuthViewModel(authRepository = authRepository)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == ComponentActivity.RESULT_OK) {
                lifecycleScope.launch {
                    val signInResult = googleAuthUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    viewModel.onSignInResult(signInResult)
                }
            }
        }
    )
    LaunchedEffect(key1 = state.isSignInSuccessful) {
        if (state.isSignInSuccessful) {
            Toast.makeText(
                context,
                "Sign in successful ${googleAuthUiClient.getSignedInUser()}",
                Toast.LENGTH_LONG
            ).show()
            Log.e("TAG", "LoginScreen: ${googleAuthUiClient.getSignedInUser()}", )
            val data = googleAuthUiClient.getSignedInUser()
            if (data != null) {
                authViewModel.loginGoogle(data.username!!,data.email!!,data.profilePictureUrl!!){result,message,customer->
                    if(result){
                        if (customer != null) {
                            sharedPreferences.edit().putInt("customer_id", customer.id).apply()
                        }
                        registerGoogleSuccess()
                    }
                    else{
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            viewModel.resetState()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)


    ) {

        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Đăng ký",
                style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            FullNameTextField(fullName = fullName) {
                fullName = it
            }
            Spacer(modifier = Modifier.height(16.dp))
            EmailOrPhoneTextField("Nhập email",emailOrPhone = email) {
                email = it
            }
            Spacer(modifier = Modifier.height(16.dp))
            EmailOrPhoneTextField("Nhập số điện thoại",emailOrPhone = phoneNumber) {
                phoneNumber = it
            }
            Spacer(modifier = Modifier.height(16.dp))
            PasswordTextField(label = null, password = password,{
                password = it
            },{})
            Spacer(modifier = Modifier.height(16.dp))
            PasswordTextField(label = R.string.repassword_hint, password = repassword,{
                repassword = it
            },{})
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Quên mật khẩu?",
                    style = TextStyle(color = Color.Blue),
                    modifier = Modifier.clickable(onClick = { forgotPassword() })
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if(fullName.isEmpty()||email.isEmpty()||phoneNumber.isEmpty()||password.isEmpty()){
                        Toast.makeText(context, "Dữ liệu không được để trống!", Toast.LENGTH_SHORT).show()
                    }
                    else if (isValidEmail(email) && isValidPhoneNumber(phoneNumber)){
                        if(password==repassword){
                            isLoading = true
                            authViewModel.register(fullName,email,phoneNumber, password){success,message->
                                isLoading = false
                                if(success){
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    onRegisterSuccess(email)
                                }
                                else{
                                    Log.e("ERROR", "RegisterScreen: $message", )
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        else{
                            Toast.makeText(context, "Mật khẩu nhập lại không khớp!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(context, "Vui lòng nhập email hoặc số điện thoại hợp lệ", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .height(62.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SoftCoral,
                    disabledContainerColor = Color.Gray,
                    contentColor = Color.White,
                    disabledContentColor = Color.Black
                )
            ) {

                Text(
                    text = "Đăng ký",
                    fontSize = 24.sp
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(color = MaterialTheme.colorScheme.primary)
                )

                Text(
                    text = "Hoặc đăng ký bằng",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(color = MaterialTheme.colorScheme.primary)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GoogleLoginButton {
                    lifecycleScope.launch {
                        val signInIntentSender = googleAuthUiClient.signIn()
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                signInIntentSender ?: return@launch
                            ).build()
                        )

                    }
                }
                TwitterLoginButton {

                }
            }
        }
        Row(
            Modifier
                .padding(vertical = 30.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Bạn đã có tài khoản?")
            Text(
                text = "Đăng nhập",
                fontWeight = FontWeight(700),
                modifier = Modifier.clickable(enabled = true, onClick = {
                    login()
                })
            )
        }

    }
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .pointerInput(Unit) { },
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
//            val painter = rememberImagePainter(
//                data = R.drawable.loading,
//                imageLoader = imageLoader
//            )
//            Image(
//                painter = painter,
//                contentDescription = null,
//                modifier = Modifier.size(200.dp)
//            )
        }
    }
}
