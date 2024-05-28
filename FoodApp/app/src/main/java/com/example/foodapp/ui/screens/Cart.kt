package com.example.foodapp.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.data.api.RetrofitClient
import com.example.foodapp.data.repository.CartRepository
import com.example.foodapp.model.cart.CartItem
import com.example.foodapp.utils.BASE_IMAGE_PRODUCT_URL
import com.example.foodapp.utils.formatVND
import com.example.foodapp.viewmodel.CartViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onClickProduct: (Int) -> Unit,
    onBackClicked: ()->Unit,
    onClickCheckOut: ()->Unit
){
    val cartRepository: CartRepository = remember { CartRepository(RetrofitClient.cartApiService) }
    val cartViewModel:CartViewModel = remember { CartViewModel(cartRepository) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("customer_data", Context.MODE_PRIVATE)
    val customerId = sharedPreferences.getInt("customer_id",-1)

    LaunchedEffect(customerId) {
        cartViewModel.getCartByCustomerId(customerId)
    }

    val carts by cartViewModel.getCartByCustomerId.observeAsState()

    if (carts == null) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color.Black)
            Text("Đang tải giỏ hàng...")
        }
        return
    }

    val totalAmount = carts?.sumOf { cartItem ->
        cartItem.price * cartItem.cart_quantity * (1 - cartItem.discount / 100.0)
    } ?: 0.0


    Scaffold(
        Modifier.background(Color.Gray),
        topBar = {
            TopAppBar(
                title = { Text("Cart") },
                navigationIcon = {
                    IconButton(onClick = {onBackClicked()}) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                    }
                }
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
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RectangleShape
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            "Tổng ${formatVND(totalAmount.toInt())}",
                            color = Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Buy Now Button
                Button(
                    onClick = {
                        onClickCheckOut()
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        disabledContentColor = Color.Black,
                        disabledContainerColor = Color.Gray,
                        contentColor = Color.White
                    ) ,
                    shape = RectangleShape,
                    enabled = totalAmount.toInt()!=0
                ) {
                    Text("Mua hàng (${carts!!.size})", fontSize = 18.sp)
                }
            }
        }
    ) {
        Column(Modifier.padding(it)) {
            LazyColumn(
                Modifier.padding(horizontal = 10.dp)
            ) {
                items(carts!!, key = { it.id }) { product ->
                    SwipeToDeleteContainer(
                        item = product,
                        onDelete = {
                            cartViewModel.removeFromCart(
                                product.id,
                                customerId,
                                onResult = { result ->
                                    if (result) {
                                        cartViewModel.getCartByCustomerId(customerId)
                                    } else {
                                        Toast.makeText(context, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    ) { item ->
                        CartItemContent(
                            item,
                            onQuantityChanged = { quantity ->
                                cartViewModel.updateCartItemQuantity(
                                    productId = item.id,
                                    customerId = customerId,
                                    quantity = quantity,
                                    add = false,
                                    onResult = { result ->
                                        if (result) {
                                            cartViewModel.getCartByCustomerId(customerId)
                                        }
                                    }
                                )
                            },
                            onClickProduct = { id ->
                                onClickProduct(id)
                            }
                        )
                    }
                }
            }



        }
    }
}




@Composable
fun CartItemContent(
    product: CartItem,
    onQuantityChanged: (Int) -> Unit,
    onClickProduct: (Int)->Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(4.dp))
            .clickable { onClickProduct(product.id) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = if (product.images.isNotEmpty() && product.images[0].imgurl.startsWith("https")) product.images[0].imgurl else BASE_IMAGE_PRODUCT_URL + product.images[0].imgurl,
            contentDescription = "Product Image",
            modifier = Modifier.size(90.dp),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = formatVND((product.price * (1 - product.discount / 100.0)).toInt()),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = formatVND(product.price),
                style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            QuantitySelector(
                initialQuantity = product.cart_quantity,
                onQuantityChanged = onQuantityChanged,
            )
        }
    }
}

@Composable
fun CartItemContentCheckout(
    product: CartItem,
    onClickProduct: (Int)->Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(4.dp))
            .clickable { onClickProduct(product.id) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = if (product.images.isNotEmpty() && product.images[0].imgurl.startsWith("https")) product.images[0].imgurl else BASE_IMAGE_PRODUCT_URL + product.images[0].imgurl,
            contentDescription = "Product Image",
            modifier = Modifier.size(90.dp),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = formatVND((product.price * (1 - product.discount / 100.0)).toInt()),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Số lượng: ${product.cart_quantity}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuantitySelector(
    initialQuantity: Int,
    onQuantityChanged: (Int) -> Unit,
) {
    var quantityState by remember { mutableStateOf(initialQuantity.toString()) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    Box(modifier = Modifier
        .fillMaxSize()
        .clickable { focusManager.clearFocus() }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = {
                    if (quantityState.toInt() > 1) {
                        quantityState = (quantityState.toInt() - 1).toString()
                        onQuantityChanged(quantityState.toInt())
                    }
                },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(painterResource(id = R.drawable.remove), contentDescription = "Decrease")
            }
            TextField(
                value = quantityState,
                onValueChange = {
                    if (it.all { char -> char.isDigit() } && it.isNotEmpty()) {
                        quantityState = it
                        onQuantityChanged(it.toInt())
                    } else if (it.isEmpty()) {
                        quantityState = "1"
                        onQuantityChanged(1)
                    }
                },
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier
                    .width(100.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            IconButton(
                onClick = {
                    quantityState = (quantityState.toInt() + 1).toString()
                    onQuantityChanged(quantityState.toInt())
                },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase")
            }
        }
    }
}