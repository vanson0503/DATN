package com.example.foodapp.ui.screens

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.data.api.RetrofitClient
import com.example.foodapp.data.repository.AuthRepository
import com.example.foodapp.data.repository.CustomerRepository
import com.example.foodapp.ui.screens.auth.PasswordTextField
import com.example.foodapp.ui.theme.SoftCoral
import com.example.foodapp.utils.BASE_IMAGE_AVATAR_URL
import com.example.foodapp.viewmodel.AuthViewModel
import com.example.foodapp.viewmodel.CustomerViewModel
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    onLogout:()->Unit,
    onLocationClicked:()->Unit,
    onSupport:()->Unit,
    onCustomerInfo:()->Unit
){
    val context = LocalContext.current
    val customerRepository = remember { CustomerRepository(RetrofitClient.customerApiService) }
    val customerViewModel: CustomerViewModel = remember { CustomerViewModel(customerRepository) }

    val sharedPreferences = context.getSharedPreferences("customer_data", Context.MODE_PRIVATE)
    val customerId = sharedPreferences.getInt("customer_id",-1)
    LaunchedEffect(customerId) {
        customerViewModel.getCustomerById(customerId)
    }
    val customer by customerViewModel.getCustomerById.observeAsState()

    if (customer == null) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color.Black)
            androidx.compose.material3.Text("Loading customer info...")
        }
        return
    }

    var avatar by remember {
        mutableStateOf(customer?.image_url ?: "")
    }
    if(!avatar.startsWith("https")){
        avatar = BASE_IMAGE_AVATAR_URL + avatar
    }
    Scaffold(
        Modifier.background(Color.Gray),
    ) {

        Column(Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = avatar,
                    contentDescription = "User Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                // User full name
                Text(
                    text = customer!!.full_name,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

            }

            Column(
                Modifier

                    .padding(horizontal = 20.dp)
            ) {

                Column(
                    Modifier
                        .padding(vertical = 20.dp)
                        .background(
                            color = Color(0xFFF6F8FA),
                            shape = RoundedCornerShape(size = 16.dp)
                        )                ) {
                    SettingRow("Thông tin cá nhân", R.drawable.person, onClick = {onCustomerInfo()})
                    SettingRow("Địa chỉ", R.drawable.address, onClick = {
                        onLocationClicked()
                    })
                }
                Column(
                    Modifier
                        .padding(vertical = 20.dp)
                        .background(
                            color = Color(0xFFF6F8FA),
                            shape = RoundedCornerShape(size = 16.dp)
                        )
                ) {
                    SettingRow("Chat với admin", R.drawable.support, onClick = {
                        onSupport()
                    })
                    SettingRow("FAQs", R.drawable.faqs, onClick = {})
                    SettingRow("Cài đặt", R.drawable.setting, onClick = {})
                }
                Column(
                    Modifier
                        .padding(vertical = 20.dp)
                        .background(
                            color = Color(0xFFF6F8FA),
                            shape = RoundedCornerShape(size = 16.dp)
                        )
                ) {
                    SettingRow("Đăng xuất", R.drawable.logout, onClick = {
                        onLogout()
                    })
                }

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProfileDetailScreen(
    onBackClicked:()->Unit,
    onEditClick:()->Unit,
    onUpdatePassword:()->Unit
){
    val context = LocalContext.current
    val customerRepository = remember { CustomerRepository(RetrofitClient.customerApiService) }
    val customerViewModel: CustomerViewModel = remember { CustomerViewModel(customerRepository) }

    val sharedPreferences = context.getSharedPreferences("customer_data", Context.MODE_PRIVATE)
    val customerId = sharedPreferences.getInt("customer_id",-1)
    LaunchedEffect(customerId) {
        customerViewModel.getCustomerById(customerId)
    }
    val customer by customerViewModel.getCustomerById.observeAsState()


    if (customer == null) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color.Black)
            androidx.compose.material3.Text("Loading customer info...")
        }
        return
    }
    var avatar by remember {
        mutableStateOf(customer?.image_url ?: "")
    }
    if(!avatar.startsWith("https")){
        avatar = BASE_IMAGE_AVATAR_URL + avatar
    }
    val email = customer?.email ?: "Chưa có email"
    val phoneNumber = customer?.phone_number ?: "Chưa có số điện thoại"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Thông tin cá nhân",
                        style = TextStyle(
                            fontSize = 20.sp, // Cài đặt kích thước văn bản theo mong muốn
                            color = Color.Black, // Cài đặt màu chữ thành màu đen
                            fontFamily = FontFamily(Font(R.font.poppinsregular)),
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                    }
                },
                actions = {
                    Text(
                        text = "EDIT",
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 24.sp,
                            fontFamily = FontFamily(Font(R.font.poppinsregular)),
                            fontWeight = FontWeight.Bold,
                            color =Color(0xFFFF7622),
                            textAlign = TextAlign.Right,
                            textDecoration = TextDecoration.Underline,
                        ),
                        modifier = Modifier
                            .clickable {
                                onEditClick()
                            }
                            .padding(end = 16.dp)
                    )
                }
            )
        }

    ) {

        Column(Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                AsyncImage(
                    model = avatar,
                    contentDescription = "User Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                // User full name
                Text(
                    text = customer!!.full_name,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

            }

            Column(
                Modifier

                    .padding(horizontal = 20.dp)
            ) {

                Column(
                    Modifier
                        .padding(vertical = 20.dp)
                        .background(
                            color = Color(0xFFF6F8FA),
                            shape = RoundedCornerShape(size = 16.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 8.dp
                                )
                                .size(40.dp),
                            painter =  painterResource(id = R.drawable.person),
                            contentDescription = "image description",
                            contentScale = ContentScale.None
                        )
                        Text(
                            text = customer!!.full_name,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.poppinsregular)),
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF32343E),
                            ),
                            modifier = Modifier.weight(1f) // Đảm bảo Text chiếm hết phần còn lại của hàng
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 8.dp
                                )
                                .size(40.dp),
                            painter =  painterResource(id = R.drawable.email),
                            contentDescription = "image description",
                            contentScale = ContentScale.None
                        )
                        Text(
                            text = email,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.poppinsregular)),
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF32343E),
                            ),
                            modifier = Modifier.weight(1f) // Đảm bảo Text chiếm hết phần còn lại của hàng
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 8.dp
                                )
                                .size(40.dp),
                            painter =  painterResource(id = R.drawable.phone),
                            contentDescription = "image description",
                            contentScale = ContentScale.None
                        )
                        Text(
                            text = phoneNumber,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.poppinsregular)),
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF32343E),
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Column(
                    Modifier
                        .padding(vertical = 20.dp)
                        .background(
                            color = Color(0xFFF6F8FA),
                            shape = RoundedCornerShape(size = 16.dp)
                        )
                ) {
                    SettingRow("Cập nhật mật khẩu", R.drawable.lock, onClick = {
                        onUpdatePassword()
                    })
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "Recycle")
@Composable
fun UpdateProfileScreen(
    onBackClicked:()->Unit,
    onSave:()->Unit
){
    val context = LocalContext.current
    val customerRepository = remember { CustomerRepository(RetrofitClient.customerApiService) }
    val customerViewModel: CustomerViewModel = remember { CustomerViewModel(customerRepository) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val sharedPreferences = context.getSharedPreferences("customer_data", Context.MODE_PRIVATE)
    val customerId = sharedPreferences.getInt("customer_id",-1)
    val focusManager = LocalFocusManager.current
    val loading = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(customerId,selectedImageUri) {
        customerViewModel.getCustomerById(customerId)
    }
    val customer by customerViewModel.getCustomerById.observeAsState()


    if (customer == null) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color.Black)
            androidx.compose.material3.Text("Loading customer info...")
        }
        return
    }
    var fullName by remember {
        mutableStateOf(customer!!.full_name)
    }
    var email by remember {
        mutableStateOf(customer?.email ?: "")
    }
    var phoneNumber by remember {
        mutableStateOf(customer?.phone_number ?: "")
    }
    var avatar by remember {
        mutableStateOf(customer?.image_url ?: "")
    }
    if(!avatar.startsWith("https")){
        avatar = BASE_IMAGE_AVATAR_URL + avatar
    }
    val getContent = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if(uri != null){
                selectedImageUri = uri
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(selectedImageUri!!)
                val file = File(context.cacheDir, "image.jpg")
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                customerViewModel.updateCustomerAvatar(
                    id = customerId,
                    image = file
                )
            }
        }
    )

    LaunchedEffect(selectedImageUri) {
        if (selectedImageUri != null) {
            delay(1000)
            customerViewModel.getCustomerById(customerId)
            Toast.makeText(context, "Cập nhật ảnh đại diện thành công!", Toast.LENGTH_SHORT).show()
        }
    }





    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cập nhật thông tin",
                        style = TextStyle(
                            fontSize = 20.sp, // Cài đặt kích thước văn bản theo mong muốn
                            color = Color.Black, // Cài đặt màu chữ thành màu đen
                            fontFamily = FontFamily(Font(R.font.poppinsregular)),
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                    }
                },
            )
        }

    ) {

        Column(Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.padding(8.dp),
                ) {
                    AsyncImage(
                        model = if(customer!!.image_url.startsWith("https")) customer!!.image_url else BASE_IMAGE_AVATAR_URL+customer!!.image_url ,
                        contentDescription = "User Profile Picture",
                        modifier = Modifier
                            .clip(CircleShape)
                            .shadow(
                                elevation = 30.dp,
                                spotColor = Color(0x26F88222),
                                ambientColor = Color(0x26F88222)
                            )
                            .padding(1.dp)
                            .width(130.dp)
                            .height(130.dp)
                            .background(color = Color(0xFFFFBF6D)),
                        contentScale = ContentScale.Crop
                    )

                    // Hiển thị icon upload ảnh ở dưới phải
                    Column(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        IconButton(
                            onClick = {
                                getContent.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        )
                        {
                            Icon(
                                painterResource(id = R.drawable.camera),
                                contentDescription = "Upload Image"
                            )
                        }
                    }
                }



                Spacer(modifier = Modifier.height(16.dp))
            }

            Column(
                Modifier
                    .padding(horizontal = 20.dp)
            ) {

                Column(
                    Modifier.weight(1f)
                ){
                    Text(
                        text = "Full Name",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.poppinsregular)),
                            fontWeight = FontWeight(400),
                            color = Color(0xFF32343E),
                        )
                    )
                    TextField(
                        value = fullName,
                        onValueChange = { name -> fullName = name },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(
                                color = Color(0xFFF0F5FA),
                                shape = RoundedCornerShape(size = 10.dp) // Đảm bảo giá trị của 'size' là một nửa chiều cao của TextField
                            ),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent, // Đặt màu nền của TextField thành trong suốt
                            cursorColor = Color.Black, // Đặt màu con trỏ thành màu đen
                            focusedIndicatorColor = Color.Transparent, // Loại bỏ hiệu ứng khi TextField được focus
                            unfocusedIndicatorColor = Color.Transparent, // Loại bỏ hiệu ứng khi TextField không được focus
                            disabledIndicatorColor = Color.Transparent // Loại bỏ hiệu ứng khi TextField bị vô hiệu hóa
                        ),
                        shape = RoundedCornerShape(size = 10.dp) // Đảm bảo giá trị của 'size' là một nửa chiều cao của TextField
                    )

                    Text(
                        text = "Email",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.poppinsregular)),
                            fontWeight = FontWeight(400),
                            color = Color(0xFF32343E),
                        )
                    )
                    TextField(
                        value = email,
                        onValueChange = { name -> email = name },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(
                                color = Color(0xFFF0F5FA),
                                shape = RoundedCornerShape(size = 10.dp) // Đảm bảo giá trị của 'size' là một nửa chiều cao của TextField
                            ),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent, // Đặt màu nền của TextField thành trong suốt
                            cursorColor = Color.Black, // Đặt màu con trỏ thành màu đen
                            focusedIndicatorColor = Color.Transparent, // Loại bỏ hiệu ứng khi TextField được focus
                            unfocusedIndicatorColor = Color.Transparent, // Loại bỏ hiệu ứng khi TextField không được focus
                            disabledIndicatorColor = Color.Transparent // Loại bỏ hiệu ứng khi TextField bị vô hiệu hóa
                        ),
                        shape = RoundedCornerShape(size = 10.dp) // Đảm bảo giá trị của 'size' là một nửa chiều cao của TextField
                    )
                    Text(
                        text = "Phone number",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.poppinsregular)),
                            fontWeight = FontWeight(400),
                            color = Color(0xFF32343E),
                        )
                    )
                    TextField(
                        value = phoneNumber,
                        onValueChange = { name -> phoneNumber = name },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(
                                color = Color(0xFFF0F5FA),
                                shape = RoundedCornerShape(size = 10.dp) // Đảm bảo giá trị của 'size' là một nửa chiều cao của TextField
                            ),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent, // Đặt màu nền của TextField thành trong suốt
                            cursorColor = Color.Black, // Đặt màu con trỏ thành màu đen
                            focusedIndicatorColor = Color.Transparent, // Loại bỏ hiệu ứng khi TextField được focus
                            unfocusedIndicatorColor = Color.Transparent, // Loại bỏ hiệu ứng khi TextField không được focus
                            disabledIndicatorColor = Color.Transparent // Loại bỏ hiệu ứng khi TextField bị vô hiệu hóa
                        ),
                        shape = RoundedCornerShape(size = 10.dp) // Đảm bảo giá trị của 'size' là một nửa chiều cao của TextField
                    )
                }
                Button(
                    onClick = {
                              customerViewModel.updateCustomer(
                                  id = customerId,
                                  fullName = fullName,
                                  email = email,
                                  phoneNumber = phoneNumber,
                                  onResult = {result->
                                      loading.value = true
                                      if(result){
                                          Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                                          loading.value = false
                                      }
                                      else{
                                          Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                                          loading.value = false
                                      }
                                  }
                              )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                        .clip(shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SoftCoral
                    )
                ) {
                    if (loading.value) {
                        androidx.compose.material.CircularProgressIndicator(
                            modifier = Modifier.size(
                                18.dp
                            )
                        )
                    } else {
                        Text(
                            text = "Lưu",
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center ,
                            color = Color.White
                        )
                    }

                }


            }
        }
    }
}




@Composable
fun  SettingRow(
    label:String,
    icon: Int,
    onClick: () -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp) // Khoảng cách dọc giữa hàng

            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ) // Khoảng cách ngang và dọc giữa biểu tượng và viền
                .size(40.dp), // Kích thước của biểu tượng
            painter =  painterResource(id = icon),
            contentDescription = "image description",
            contentScale = ContentScale.None
        )
        Text(
            text = label,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.poppinsregular)),
                fontWeight = FontWeight.Normal,
                color = Color(0xFF32343E),
            ),
            modifier = Modifier.weight(1f) // Đảm bảo Text chiếm hết phần còn lại của hàng
        )
        Image(
            modifier = Modifier
                .padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ) // Khoảng cách giữa biểu tượng và viền bên phải và theo chiều dọc
                .size(24.dp), // Kích thước của biểu tượng
            painter = painterResource(id = R.drawable.chevron_right),
            contentDescription = "image description",
            contentScale = ContentScale.None,
            alignment = Alignment.CenterEnd
        )
    }
}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBackClicked: () -> Unit,
    onUpdatePasswordSuccess:()->Unit
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("customer_data", Context.MODE_PRIVATE)
    val customerId  = sharedPreferences.getInt("customer_id",-1)
    val authRepository = AuthRepository(RetrofitClient.authApiService)
    val authViewModel: AuthViewModel = remember {
        AuthViewModel(authRepository = authRepository)
    }
    var oldPassword by remember {
        mutableStateOf("")
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
            androidx.compose.material3.Text(
                text = "Cài đặt mật khẩu mới",
                fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = FontFamily(Font(resId = R.font.poppinsbold)),
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            androidx.compose.material3.Text(
                text = "Mật khẩu cũ",
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = FontFamily(Font(resId = R.font.poppinsregular)),
            )
            PasswordTextField(
                oldPassword,
                onPasswordChange = {oldPassword=it}
            )
            androidx.compose.material3.Text(
                text = "Mật khẩu mới",
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = FontFamily(Font(resId = R.font.poppinsregular)),
            )
            PasswordTextField(
                password,
                onPasswordChange = {password=it}
            )
            androidx.compose.material3.Text(
                text = "Nhập lại mật khẩu mới",
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
                    authViewModel.updatePassword(customerId,oldPassword,password){result,message->
                        if(result){
                            Toast.makeText(context, "Cập nhật mật khẩu thành công!", Toast.LENGTH_SHORT).show()
                            onUpdatePasswordSuccess()
                        }
                        else{
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = password==repassword&&password.length>=6&&oldPassword.length>=6,
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
                androidx.compose.material3.Text(
                    "Cập nhật",
                    color = Color.White,
                    modifier = Modifier.background(Color.Transparent),
                    fontSize = 20.sp
                )
            }
        }
    }
}