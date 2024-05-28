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
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.foodapp.R
import com.example.foodapp.data.api.RetrofitClient.authApiService
import com.example.foodapp.data.repository.AuthRepository
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
fun LoginScreen(
    lifecycleScope : LifecycleCoroutineScope,
    googleAuthUiClient: GoogleAuthUiClient,
    forgotPassword : () -> Unit,
    register : () -> Unit,
    loginSuccess : () -> Unit,
) {
    val context = LocalContext.current
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authRepository = AuthRepository(authApiService)
    var isLoading by remember { mutableStateOf(false) }
    var isLoginSuccess by remember { mutableStateOf(false) }
    var customerId by remember { mutableIntStateOf(0) }
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
                        loginSuccess()
                    }
                    else{
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            viewModel.resetState()
        }
    }


    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)


    ) {

        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,

        ) {
            Text(
                text = "Đăng nhập",
                style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            EmailOrPhoneTextField("Nhập email hoặc số điên thoại",emailOrPhone = emailOrPhone) {
                emailOrPhone = it
            }
            Spacer(modifier = Modifier.height(16.dp))
            PasswordTextField(label = null, password = password,{
                password = it
            },{

            })
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

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    if (isValidEmail(emailOrPhone) || isValidPhoneNumber(emailOrPhone)){
                        isLoading = true
                        authViewModel.login(emailOrPhone, password){success,result,customer->
                            isLoading = false
                            if(success){
                                isLoginSuccess = true
                                customerId = customer!!.id;
                                sharedPreferences.edit().putInt("customer_id", customerId).apply()
                                loginSuccess()
                            }
                            else{
                                Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                            }
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
                    text = "Login",
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
                        .background(color = colorScheme.primary)
                )

                Text(
                    text = "Hoặc đăng nhập bằng",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(color = colorScheme.primary)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
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
                    lifecycleScope.launch {
                        googleAuthUiClient.signOut()
                    }
                }
            }

        }
        Row(
            Modifier
                .padding(vertical = 30.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Bạn chưa có tài khoản?")
            Text(
                text = "Đăng ký",
                fontWeight = FontWeight(700),
                modifier = Modifier.clickable(enabled = true, onClick = {
                    register()
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

    if (isLoginSuccess) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .pointerInput(Unit) { },
            contentAlignment = Alignment.Center
        ) {
            val painter = rememberImagePainter(
                data = R.drawable.success,
                imageLoader = imageLoader
            )
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            LaunchedEffect(Unit) {
                delay(1000)
                isLoginSuccess = false
            }

        }

    }

}
