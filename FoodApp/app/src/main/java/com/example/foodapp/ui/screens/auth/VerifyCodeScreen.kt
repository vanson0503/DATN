package com.example.foodapp.ui.screens.auth

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.data.api.RetrofitClient.authApiService
import com.example.foodapp.data.repository.AuthRepository
import com.example.foodapp.ui.theme.CalmTeal
import com.example.foodapp.ui.theme.SoftCoral
import com.example.foodapp.utils.isValidEmail
import com.example.foodapp.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyCodeScreen(
    email : String,
    onVerifySuccess : () -> Unit,
    onBackClicked:()->Unit
) {
    val authRepository = AuthRepository(authApiService)
    val authViewModel: AuthViewModel = remember {
        AuthViewModel(authRepository = authRepository)
    }
    val context = LocalContext.current
    var verityCode by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("Xác minh tài khoản") },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
                    }
                },
            )
        }
    ) {paddingValue->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Mã xác nhận đã gửi về email thành công!",
            )
            val coroutineScope = rememberCoroutineScope()
            var isResendEnabled by remember { mutableStateOf(true) }
            var remainingTime by remember { mutableIntStateOf(0) }
            AsyncImage(
                model = "https://cdn-icons-png.flaticon.com/256/3630/3630926.png",
                contentDescription = null,
                modifier = Modifier.width(200.dp)
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            Text(
                text = "Nhập mã xác nhận",
                fontSize = 24.sp,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            BasicTextField(
                value = verityCode,
                onValueChange = {
                    if (it.length <= 6) {
                        verityCode = it
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                ) {
                    repeat(6) { index ->
                        val number = when {
                            index >= verityCode.length -> ""
                            else -> verityCode[index].toString()
                        }
                        val isFilled = index < verityCode.length
                        Box(
                            modifier = Modifier
                                .width(45.dp)
                                .height(60.dp)
                                .background(
                                    if (isFilled) SoftCoral else Color.White,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .border(2.dp, SoftCoral, shape = RoundedCornerShape(6.dp))
                                .clip(RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = number,
                                style = TextStyle(
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }


            Button(
                onClick = {
                    authViewModel.confirmVerifyCode(email,verityCode){result,message->
                        if(result){
                            Toast.makeText(context, "Nhập mã thành công!", Toast.LENGTH_SHORT).show()
                            onVerifySuccess()
                        }
                        else{
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = verityCode.length == 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
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
                    "Xác nhận",
                    color = Color.White,
                    modifier = Modifier.background(Color.Transparent),
                    fontSize = 20.sp
                    )
            }
            if (isResendEnabled) {
                Text(
                    text = "Gửi lại mã",
                    modifier = Modifier
                        .clickable {
                            authViewModel.resendCode(email) { result, message ->
                                if (result) {
                                    Toast
                                        .makeText(
                                            context,
                                            "Gửi lại mã thành công!",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                    coroutineScope.launch {
                                        isResendEnabled = false
                                        remainingTime = 60
                                        for (i in 60 downTo 1) {
                                            remainingTime = i
                                            delay(1000)
                                        }
                                        isResendEnabled = true
                                    }
                                } else {
                                    Toast
                                        .makeText(
                                            context,
                                            "Gửi lại mã thất bại!",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                            }
                        }
                        .padding(vertical = 8.dp),
                    style = TextStyle(
                        color = CalmTeal,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(resId = R.font.poppinsregular)),
                        textDecoration = TextDecoration.Underline
                    )
                )
            } else {
                Text(
                    text = "Gửi lại mã sau $remainingTime giây",
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(resId = R.font.poppinsregular))
                    )
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onLoginClick: () -> Unit,
    onSendEmailSuccess: (String) -> Unit,
    onBackClicked: () -> Unit
) {
    val context = LocalContext.current
    val authRepository = AuthRepository(authApiService)
    val authViewModel: AuthViewModel = remember {
        AuthViewModel(authRepository = authRepository)
    }
    var email by remember { mutableStateOf("") }
    val isEmailValid by derivedStateOf { isValidEmail(email) }
    val coroutineScope = rememberCoroutineScope()
    var isResendEnabled by remember { mutableStateOf(true) }
    var remainingTime by remember { mutableIntStateOf(0) }
    var loading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
                    }
                },
            )
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = paddingValue.calculateTopPadding(), horizontal = 20.dp),
        ) {
            Text(
                text = "Quên mật khẩu",
                fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = FontFamily(Font(resId = R.font.poppinsbold)),
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            Text(
                text = "Vui lòng nhập địa chỉ email để lấy lại mật khẩu",
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = FontFamily(Font(resId = R.font.poppinsregular)),
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            Text(
                text = "Email",
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = FontFamily(Font(resId = R.font.poppinsbold)),
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text(text = "Nhập email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        BorderStroke(1.dp, Color.Gray),
                        shape = RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    errorBorderColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.padding(vertical = 20.dp))
            Button(
                onClick = {
                    if (isEmailValid) {
                        loading = true
                        authViewModel.sendResetEmail(email) { result, message ->
                            loading = false
                            if (result) {
                                Toast
                                    .makeText(
                                        context,
                                        "Gửi mã xác nhận thành công!",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                                onSendEmailSuccess(email)
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        "$message",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        }
                    }
                },
                enabled = isEmailValid && !loading,
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
                if (loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        "Lấy lại mật khẩu",
                        color = Color.White,
                        modifier = Modifier.background(Color.Transparent),
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyCodeForgotPasswordScreen(
    email:String,
    onVerifySuccess: (String) -> Unit,
    onBackClicked: () -> Unit
) {
    val context = LocalContext.current
    val authRepository = AuthRepository(authApiService)
    val authViewModel: AuthViewModel = remember {
        AuthViewModel(authRepository = authRepository)
    }
    val coroutineScope = rememberCoroutineScope()
    var isResendEnabled by remember { mutableStateOf(true) }
    var remainingTime by remember { mutableIntStateOf(0) }
    var loading by remember { mutableStateOf(false) }
    var verityCode by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
                    }
                },
            )
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = paddingValue.calculateTopPadding(), horizontal = 20.dp),
        ) {
            Text(
                text = "Kiểm tra email của bạn",
                fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = FontFamily(Font(resId = R.font.poppinsbold)),
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            Text(
                text = "Nhập mã xác nhận",
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = FontFamily(Font(resId = R.font.poppinsregular)),
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            BasicTextField(
                value = verityCode,
                onValueChange = {
                    if (it.length <= 6) {
                        verityCode = it
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    repeat(6) { index ->
                        val number = when {
                            index >= verityCode.length -> ""
                            else -> verityCode[index].toString()
                        }
                        val isFilled = index < verityCode.length
                        Box(
                            modifier = Modifier
                                .width(45.dp)
                                .height(60.dp)
                                .background(
                                    if (isFilled) SoftCoral else Color.White,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .border(2.dp, SoftCoral, shape = RoundedCornerShape(6.dp))
                                .clip(RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = number,
                                style = TextStyle(
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.padding(vertical = 20.dp))

            Button(
                onClick = {
                    authViewModel.confirmVerifyCodeResetPass(email,verityCode){result,message->
                        if(result){
                            Toast.makeText(context, "Nhập mã thành công!", Toast.LENGTH_SHORT).show()
                            onVerifySuccess(email)
                        }
                        else{
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = verityCode.length == 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
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
                    "Xác nhận",
                    color = Color.White,
                    modifier = Modifier.background(Color.Transparent),
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            if (isResendEnabled) {
                Text(
                    text = "Gửi lại mã",
                    modifier = Modifier
                        .clickable {
                            authViewModel.resendCode(email) { result, message ->
                                if (result) {
                                    Toast
                                        .makeText(
                                            context,
                                            "Gửi lại mã thành công!",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                    coroutineScope.launch {
                                        isResendEnabled = false
                                        remainingTime = 60
                                        for (i in 60 downTo 1) {
                                            remainingTime = i
                                            delay(1000)
                                        }
                                        isResendEnabled = true
                                    }
                                } else {
                                    Toast
                                        .makeText(
                                            context,
                                            "Gửi lại mã thất bại!",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                            }
                        }
                        .padding(vertical = 8.dp),
                    style = TextStyle(
                        color = CalmTeal,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(resId = R.font.poppinsregular)),
                        textDecoration = TextDecoration.Underline
                    )
                )
            } else {
                Text(
                    text = "Gửi lại mã sau $remainingTime giây",
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(resId = R.font.poppinsregular))
                    )
                )
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterResetPassword(
    email: String,
    onBackClicked: () -> Unit,
    onResetSuccess:()->Unit
) {
    val context = LocalContext.current
    val authRepository = AuthRepository(authApiService)
    val authViewModel: AuthViewModel = remember {
        AuthViewModel(authRepository = authRepository)
    }
    var password by remember {
        mutableStateOf("")
    }
    var repassword by remember {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
                    }
                },
            )
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = paddingValue.calculateTopPadding(), horizontal = 20.dp),
        ) {
            Text(
                text = "Cài đặt mật khẩu mới",
                fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = FontFamily(Font(resId = R.font.poppinsbold)),
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            Text(
                text = "Mật khẩu mới",
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = FontFamily(Font(resId = R.font.poppinsregular)),
            )
            PasswordTextField(
                password,
                onPasswordChange = {password=it}
                )
            Text(
                text = "Nhập lại mật khẩu",
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = FontFamily(Font(resId = R.font.poppinsregular)),
            )
            PasswordTextField(
                repassword,
                onPasswordChange = {repassword=it}
            )
            Spacer(modifier = Modifier.padding(vertical = 20.dp))
            Button(
                onClick = {
                    authViewModel.resetPassword(email,password){result,message->
                        if(result){
                            Toast.makeText(context, "Cập nhật mật khẩu thành công!", Toast.LENGTH_SHORT).show()
                            onResetSuccess()
                        }
                        else{
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = password==repassword&&password.length>=6,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
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
                    "Cập nhật",
                    color = Color.White,
                    modifier = Modifier.background(Color.Transparent),
                    fontSize = 20.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
){
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = {
            onPasswordChange(it)
        },
        placeholder = { Text(text = "Nhập mật khẩu") },
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, Color.Gray),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            errorBorderColor = Color.Transparent
        ),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible) {
                painterResource(R.drawable.visibility_off)
            } else {
                painterResource(R.drawable.visibility)
            }

            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = {
                passwordVisible = !passwordVisible
            }) {
                Icon(image, contentDescription = description)
            }
        }
    )
}