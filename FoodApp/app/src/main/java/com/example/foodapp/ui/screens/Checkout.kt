package com.example.foodapp.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.R
import com.example.foodapp.data.api.RetrofitClient
import com.example.foodapp.data.repository.CartRepository
import com.example.foodapp.data.repository.LocationRepository
import com.example.foodapp.data.repository.OrderRepository
import com.example.foodapp.ui.components.CustomToast
import com.example.foodapp.utils.formatVND
import com.example.foodapp.viewmodel.CartViewModel
import com.example.foodapp.viewmodel.LocationViewModel
import com.example.foodapp.viewmodel.OrderViewModel
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.json.responseJson
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import kotlinx.coroutines.delay
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CheckoutFormCartScreen(
    onBackClicked: ()->Unit,
    onClickProduct: (Int) -> Unit,
    onCheckoutClick:() ->Unit,
    onAddLocationClick:()->Unit
){
    val context = LocalContext.current
    val cartRepository: CartRepository = remember { CartRepository(RetrofitClient.cartApiService) }
    val cartViewModel: CartViewModel = remember { CartViewModel(cartRepository) }
    val locationRepository = remember { LocationRepository(RetrofitClient.locationApiService) }
    val locationViewModel: LocationViewModel = remember { LocationViewModel(locationRepository) }
    val orderRepository = remember { OrderRepository(RetrofitClient.orderApiService) }
    val orderViewModel: OrderViewModel = remember { OrderViewModel(orderRepository) }
    val sharedPreferences = context.getSharedPreferences("customer_data", Context.MODE_PRIVATE)
    val customerId = sharedPreferences.getInt("customer_id",-1)
    val orderNote = remember { mutableStateOf("") }
    val selectedPaymentMethod = remember { mutableStateOf("Tiền mặt") }
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val showToast = remember { mutableStateOf(false) }
    var orderSuccess by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(orderSuccess) {
        if(orderSuccess){
            delay(1000)
            onCheckoutClick()
        }
    }


    var customerConfig by remember { mutableStateOf<PaymentSheet.CustomerConfiguration?>(null) }
    var paymentIntentClientSecret by remember { mutableStateOf<String?>(null) }
    fun fetchPaymentInfo(amount:Int) {
        val jsonBody = JSONObject()
        jsonBody.put("amount", amount)
//        Toast.makeText(context, "Request sent", Toast.LENGTH_SHORT).show()
        try {
            "https://stripe-payment-ji83.onrender.com/payment-sheet"
                .httpPost()
                .header("Content-Type" to "application/json")
                .body(jsonBody.toString())
                .responseJson { _, _, result ->
                    result.fold(
                        success = { json ->
                            val responseJson = json.obj()
                            paymentIntentClientSecret = responseJson.getString("paymentIntent")
                            customerConfig = PaymentSheet.CustomerConfiguration(
                                responseJson.getString("customer"),
                                responseJson.getString("ephemeralKey")
                            )
                            val publishableKey = responseJson.getString("publishableKey")
                            PaymentConfiguration.init(context, publishableKey)
                        },
                        failure = { error ->
                            Log.e("Payment Error", "Error fetching payment info: ${error.message}")
                        }
                    )
                }
        } catch (e: Exception) {
            Log.e("JSON Error", "Error parsing JSON", e)
        }
    }





    LaunchedEffect(customerId) {
        locationViewModel.getLocationsByCustomerId(customerId)
        cartViewModel.getCartByCustomerId(customerId)
    }

    val locations by locationViewModel.getLocationsByCustomerId.observeAsState()
    val carts by cartViewModel.getCartByCustomerId.observeAsState()

    if (locations==null||carts == null) {
//        Toast.makeText(context, "locations ${locations.toString()}", Toast.LENGTH_SHORT).show()
//        Toast.makeText(context, "carts ${carts.toString()}", Toast.LENGTH_SHORT).show()
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color.Black)
            Text("Đang tải dữ liệu...")
        }
        return
    }

    if (locations.isNullOrEmpty() || carts.isNullOrEmpty()) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Thanh toán") },
                        navigationIcon = {
                            IconButton(onClick = {
                                onBackClicked()
                            }) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                            }
                        },
                        actions = {

                        },
                    )
                },
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(it),
                    Arrangement.Center
                ){
                    if (locations.isNullOrEmpty()) {
                        Text("Chưa có địa chỉ. Vui lòng thêm địa chỉ để đặt hàng",
                                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        Button(onClick = {
                            onAddLocationClick()
                        },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)) {
                            Text("Thêm địa chỉ",)
                        }
                    } else if (carts.isNullOrEmpty()) {
                        Text(
                            "Giỏ hàng của bạn đang trống.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

        }
        return
    }

    val locationId = remember { mutableIntStateOf(locations!![0].id ) }
    val paymentSheet = rememberPaymentSheet { result ->
        onPaymentSheetResult(result)
        if (result is PaymentSheetResult.Completed) {
            orderViewModel.addToOderFromCart(
                locationId = locationId.intValue,
                customerId = customerId,
                payment = if(selectedPaymentMethod.value=="Tiền mặt") "cash" else "online",
                paymentStatus = "completed",
                onResult = {result,message->
                    if(result){
                        showToast.value = true
                        orderSuccess = true
                    }else{
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                })

        }
    }



    val totalAmount = carts?.sumOf { cartItem ->
        cartItem.price * cartItem.cart_quantity * (1 - cartItem.discount / 100.0)
    } ?: 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thanh toán") },
                navigationIcon = {
                    IconButton(onClick = {
                        onBackClicked()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                    }
                },
                actions = {

                },
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(50.dp)
            ) {
                Button(
                    onClick = {

                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RectangleShape
                ) {
                    Icon(
                        painterResource(id = R.drawable.attach_money),
                        contentDescription = "",
                        tint = Color.White
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(formatVND(totalAmount.toInt()), color = Color.White, fontSize = 14.sp)
                }

                // Buy Now Button
                Button(
                    onClick = {
//                        for()

                        if(selectedPaymentMethod.value=="Tiền mặt"){
                            orderViewModel.addToOderFromCart(
                                locationId = locationId.intValue,
                                customerId = customerId,
                                payment = if(selectedPaymentMethod.value=="Tiền mặt") "cash" else "online",
                                onResult = {result,message->
                                    if(result){
                                        showToast.value = true
                                        orderSuccess = true
                                    }else{
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                })
                        }
                        else{
                            fetchPaymentInfo(totalAmount.toInt())
                            val currentConfig = customerConfig
                            val currentClientSecret = paymentIntentClientSecret

                            if (currentConfig != null && currentClientSecret != null) {
                                presentPaymentSheet(paymentSheet, currentConfig, currentClientSecret)
                            } else {
                                Toast.makeText(context, "Đang khởi tạo dịch vụ!", Toast.LENGTH_SHORT).show()
                                print("Config or Client Secret is null")
                            }
                        }

                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red) ,
                    shape = RectangleShape
                ) {
                    Text("Thanh toán", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    ) {
        Column(
            Modifier.padding(it)
        ){
            LazyColumn(
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                item {
                    Text(text = "Địa chỉ")
                    LocationDropdown(
                        locations = locations!!,
                        onLocationChange = {id->
                            locationId.intValue = id
                        },
                        onAddLocation = {

                        }
                    )
                }
                item{
                    repeat(carts!!.size){index->
                        CartItemContentCheckout(
                            carts!![index],
                            onClickProduct = {id->
                                onClickProduct(id)
                            }
                        )
                        Spacer(modifier = Modifier.padding(vertical = 10.dp))
                    }
                }
                item {
                    Text(text = "Ghi chú đơn hàng", style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(
                        value = orderNote.value,
                        onValueChange = { orderNote.value = it },
                        label = { Text("Thêm ghi chú") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (!focusState.isFocused) {
                                    focusManager.clearFocus()
                                }
                            },
                        maxLines = 3,
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                }
                item {
                    Text(text = "Phương thức thanh toán", style = MaterialTheme.typography.bodySmall)
                    PaymentMethodSelector(
                        selectedPaymentMethod = selectedPaymentMethod,
                        onPaymentMethodChange = { method->
                            selectedPaymentMethod.value = method
                        }
                    )
                }
            }
        }
    }
    if(showToast.value){
        CustomToast(
            message = "Đặt hàng thành công",
            icon = Icons.Filled.Check,
            onDismiss = {

            })
    }

}

@Composable
fun PaymentMethodSelector(
    selectedPaymentMethod: MutableState<String>,
    onPaymentMethodChange: (String) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = {
                    onPaymentMethodChange("Tiền mặt")
                    selectedPaymentMethod.value = "Tiền mặt"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedPaymentMethod.value == "Tiền mặt") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                )
            ) {
                Text("Tiền mặt", color = if (selectedPaymentMethod.value == "Tiền mặt") Color.White else Color.Black)
            }
            Button(
                onClick = {
                    onPaymentMethodChange("Banking")
                    selectedPaymentMethod.value = "Banking"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedPaymentMethod.value == "Banking") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                )
            ) {
                Text("Chuyển khoản", color = if (selectedPaymentMethod.value == "Banking") Color.White else Color.Black)
            }
        }
    }
}

private fun presentPaymentSheet(
    paymentSheet: PaymentSheet,
    customerConfig: PaymentSheet.CustomerConfiguration,
    paymentIntentClientSecret: String
) {
    paymentSheet.presentWithPaymentIntent(
        paymentIntentClientSecret,
        PaymentSheet.Configuration(
            merchantDisplayName = "My merchant name",
            customer = customerConfig,
            // Set `allowsDelayedPaymentMethods` to true if your business handles
            // delayed notification payment methods like US bank accounts.
            allowsDelayedPaymentMethods = true
        )
    )
}

private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
    when(paymentSheetResult) {
        is PaymentSheetResult.Canceled -> {
            Log.e("TAG", "PaymentSheetResult: Canceled")
        }
        is PaymentSheetResult.Failed -> {
            Log.e("TAG", "PaymentSheetResult: Error ${paymentSheetResult.error}")
        }
        is PaymentSheetResult.Completed -> {
            Log.e("TAG", "PaymentSheetResult: Completed")
        }
    }
}

