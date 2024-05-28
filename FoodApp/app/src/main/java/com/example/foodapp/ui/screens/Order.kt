package com.example.foodapp.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.RatingBar
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.data.api.RetrofitClient
import com.example.foodapp.data.repository.OrderRepository
import com.example.foodapp.data.repository.ProductRepository
import com.example.foodapp.data.repository.ReviewRepository
import com.example.foodapp.model.order.Detail
import com.example.foodapp.model.order.OrderItem
import com.example.foodapp.model.review.ReviewItem
import com.example.foodapp.utils.BASE_IMAGE_PRODUCT_URL
import com.example.foodapp.utils.formatVND
import com.example.foodapp.viewmodel.OrderViewModel
import com.example.foodapp.viewmodel.ProductViewModel
import com.example.foodapp.viewmodel.ReviewViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OrderMainScreen(
    onClickOrder:(Int)->Unit
){
    val context = LocalContext.current
    val orderRepository = remember { OrderRepository(RetrofitClient.orderApiService) }
    val orderViewModel: OrderViewModel = remember { OrderViewModel(orderRepository) }

    val sharedPreferences = context.getSharedPreferences("customer_data", Context.MODE_PRIVATE)
    val customerId = sharedPreferences.getInt("customer_id",-1)
    LaunchedEffect(customerId) {
        orderViewModel.getOrderDetailsByCustomerId(customerId)
    }
    val orderDetails by orderViewModel.getOrderDetailsByCustomerId.observeAsState()

    if (orderDetails == null) {
        // Hiển thị quá trình tải
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = Color.Black)
            Text("Đang tải thông tin đơn hàng...")
        }
    } else if (orderDetails!!.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Chưa có đơn hàng nào.")
        }
    } else {
        Scaffold(
            topBar = {TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Đơn mua",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                },

                )
            }
        ) {paddingValues->
            LazyColumn(
                modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
            ) {
                item {
                    Column(Modifier.padding(horizontal = 10.dp)) {
                        repeat(orderDetails!!.size){index->
                            OrderItem(
                                order = orderDetails!![index],
                                onClickOrder = {id->
                                    onClickOrder(id)
                                },
                                resetData = {
                                    orderViewModel.getOrderDetailsByCustomerId(customerId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OrderDetailScreen(
    id:Int,
    onBackClicked: () -> Unit,
    onCartClicked: () -> Unit,
){
    val context = LocalContext.current
    val orderRepository = remember { OrderRepository(RetrofitClient.orderApiService) }
    val orderViewModel: OrderViewModel = remember { OrderViewModel(orderRepository) }

    LaunchedEffect(id) {
        orderViewModel.getOrderDetailById(id)
    }
    val orderDetail by orderViewModel.getOrderDetailById.observeAsState()

    if (orderDetail == null) {
        // Hiển thị quá trình tải
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color.Black)
            Text("Đang tải thông tin đơn hàng...")
        }
    } else {
        Scaffold(
            topBar = {TopAppBar(
                title = { Text("Thông tin đơn hàng") },
                navigationIcon = {
                    IconButton(onClick = {onBackClicked()}) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                    }
                },
                actions = {
                    IconButton(onClick = {onCartClicked()}) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart")
                    }
                },
            )
            }
        ) {paddingValues->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val paymentMethodText = when (orderDetail!!.payment) {
                    "cash" -> "Tiền mặt"
                    "online" -> "Ngân hàng"
                    else -> "Không xác định"
                }

                val paymentStatusText = when (orderDetail!!.payment_status) {
                    "initialization" -> "Khởi tạo"
                    "completed" -> "Hoàn thành"
                    "failed" -> "Thất bại"
                    else -> "Không xác định"
                }
                val statusText = when (orderDetail!!.status){
                    "initialization" -> "Khởi tạo"
                    "confirm" -> "Đã xác nhận"
                    "delivering" -> "Đang giao hàng"
                    "completed" -> "Hoàn thành"
                    "cancelled" -> "Đã hủy"
                    "refund" -> "Hoàn hàng"
                    else -> "Không xác định"
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        // Địa chỉ nhận hàng
                        Text(
                            text = "Thông tin nhận hàng",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "${orderDetail!!.name}\n${orderDetail!!.phone_number}\n${orderDetail!!.address}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }

                    item {
                        // Danh sách sản phẩm
                        Text(
                            text = "Danh sách sản phẩm",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Column(

                        ) {
                            orderDetail!!.details.forEach { detail ->
                                ProductDetailItem(detail,orderDetail!!.id,orderDetail!!.status)
                                Divider(
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                    item{
                        val formattedDate1 = formatDateFromString(orderDetail!!.created_time, "yyyy-MM-dd HH:mm:ss", "dd/MM/yyyy")
                        Text(
                            text = "Ngày đặt hàng: $formattedDate1"
                        )
                        if(orderDetail!!.status=="completed"){
                            val formattedDate = formatDateFromString(orderDetail!!.updated_time, "yyyy-MM-dd HH:mm:ss", "dd/MM/yyyy")
                            Text(
                                text = "Ngày nhận hàng: $formattedDate"
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Phương thức thanh toán: $paymentMethodText",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Payment status
                        Text(
                            text = "Trạng thái thanh toán: $paymentStatusText",
                            style = MaterialTheme.typography.bodyMedium.copy(color = when (orderDetail!!.status) {
                                "initialization" -> Color.Blue
                                "completed" -> Color.Green
                                "failed" -> Color.Red
                                else -> Color.Gray
                            }),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Divider()

                        // Order status
                        Text(
                            text = "Tình trạng đơn hàng: $statusText",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                }
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateFromString(dateTimeString: String, inputFormat: String, outputFormat: String): String {
    val formatterInput = DateTimeFormatter.ofPattern(inputFormat)
    val formatterOutput = DateTimeFormatter.ofPattern(outputFormat)
    val dateTime = LocalDateTime.parse(dateTimeString, formatterInput)
    return dateTime.format(formatterOutput)
}

@Composable
fun ProductDetailItem(
    detail: Detail,
    ordersId: Int,
    status: String
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("customer_data", Context.MODE_PRIVATE)
    val customerId = sharedPreferences.getInt("customer_id", -1)
    val reviewRepository = remember { ReviewRepository(RetrofitClient.reviewApiService) }
    val reviewViewModel: ReviewViewModel = remember { ReviewViewModel(reviewRepository) }

    var review: ReviewItem? by remember { mutableStateOf(null) }
    val rating = remember { mutableIntStateOf(0) }
    val (content, setContent) = remember { mutableStateOf("") }

    val openDialog = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = null) {
        reviewViewModel.checkReview(ordersId, customerId, detail.product_id) { result, data ->
            Log.e("TAG", "ProductDetailItem: $data", )
            review = if (result) {
                data
            } else {
                null
            }
        }
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(4.dp))
                .clickable { },
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = if (detail.product.images.isNotEmpty() && detail.product.images[0].imgurl.startsWith("https")) detail.product.images[0].imgurl else BASE_IMAGE_PRODUCT_URL + detail.product.images[0].imgurl,
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
                    text = detail.product.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatVND((detail.price ).toInt()),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Số lượng: ${detail.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

        }
        if (status == "completed") {
            Button(
                onClick = { openDialog.value = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = review == null
            ) {
                Text("Đánh giá")
            }
        }

        // Hiện AlertDialog khi openDialog == true
        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = { openDialog.value = false },
                title = { Text("Đánh giá") },
                text = {
                    Column {
                        CustomRatingBar{rate->
                            rating.value = rate
                        }
                        // Nhập nội dung đánh giá
                        OutlinedTextField(
                            value = content,
                            onValueChange = { setContent(it) },
                            label = { Text("Nội dung") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if(rating.value==0){
                                Toast.makeText(context, "Vui lòng chọn số sao!", Toast.LENGTH_SHORT).show()
                            }
                            else{
                                reviewViewModel.addReview(ordersId,customerId,detail.product_id,rating.value,content){result,message->
                                    if(result){
                                        Toast.makeText(context, "Đánh giá thành công", Toast.LENGTH_SHORT).show()
                                        openDialog.value = false
                                        reviewViewModel.checkReview(ordersId, customerId, detail.product_id) { result, data ->
                                            Log.e("TAG", "ProductDetailItem: $data", )
                                            review = if (result) {
                                                data
                                            } else {
                                                null
                                            }
                                        }
                                    }
                                    else{
                                        Log.e("TAG", "ProductDetailItem: $message", )
                                        Toast.makeText(context, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Gửi")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { openDialog.value = false }
                    ) {
                        Text("Hủy")
                    }
                }
            )
        }
    }
}

@Composable
fun CustomRatingBar(
    maxRating: Int = 5,
    size: Dp = 32.dp,
    onRatingChanged: (Int) -> Unit
) {
    var rating by remember {
        mutableStateOf(0)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(maxRating) { index ->
            val star = if (index < rating) {
                R.drawable.star_solid
            } else {
                R.drawable.ic_star
            }
            Icon(
                painterResource(id = star) ,
                contentDescription = null,
                tint = Color.Yellow,
                modifier = Modifier
                    .size(size)
                    .clickable {
                        rating = index + 1
                        onRatingChanged(index + 1)
                    }
            )
        }
    }
}




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderItem(
    order: OrderItem,
    onClickOrder: (Int) -> Unit,
    resetData:()->Unit
) {
    val context = LocalContext.current
    val firstProduct = order.details.firstOrNull()?.product
    val totalPrice = remember {
        order.details.sumByDouble { it.quantity * it.price.toDouble() }
    }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var dialogAction: (() -> Unit)? by remember { mutableStateOf(null) }

    val orderRepository = remember { OrderRepository(RetrofitClient.orderApiService) }
    val orderViewModel: OrderViewModel = remember { OrderViewModel(orderRepository) }
    val sharedPreferences = context.getSharedPreferences("customer_data", Context.MODE_PRIVATE)
    val customerId = sharedPreferences.getInt("customer_id",-1)

    val currentDate = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val completedDate = order.updated_time.let { LocalDateTime.parse(it, dateFormatter).toLocalDate() }
    val daysSinceCompleted = completedDate?.let { ChronoUnit.DAYS.between(it, currentDate) } ?: Long.MAX_VALUE


    if (firstProduct != null) {
        Row(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .background(Color.White)
                .fillMaxWidth()
                .clickable {
                    onClickOrder(order.id)
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    AsyncImage(
                        model = if (firstProduct.images.isNotEmpty() && firstProduct.images[0].imgurl.startsWith("https")) firstProduct.images[0].imgurl else BASE_IMAGE_PRODUCT_URL + firstProduct.images[0].imgurl,
                        contentDescription = "Product Image",
                        modifier = Modifier.size(100.dp)
                    )
                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f)
                    ) {
                        Text(
                            text = firstProduct.name,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "x ${order.details.firstOrNull()?.quantity ?: 0}",
                            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Giá: ${formatVND(order.details[0].price.toInt())}",
                            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Divider(
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${order.details.size} sản phẩm",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    val text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.Black, fontSize = 14.sp)) {
                            append("Thành tiền: ")
                        }
                        withStyle(style = SpanStyle(color = Color.Red, fontSize = 14.sp)) {
                            append(formatVND(totalPrice.toInt()))
                        }
                    }

                    Text(text = text)
                }
                Divider(
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                val statusText = when (order.status) {
                    "initialization" -> "Khởi tạo"
                    "confirm" -> "Đã xác nhận"
                    "delivering" -> "Đang giao hàng"
                    "completed" -> "Hoàn thành"
                    "cancelled" -> "Đã hủy"
                    "refund" -> "Hoàn hàng"
                    else -> "Không xác định"
                }

                val statusColor = when (order.status) {
                    "confirm" -> Color.Yellow
                    "completed" -> Color.Green
                    "failed", "cancelled", "refund" -> Color.Red
                    "initialization", "delivering" -> Color.Blue
                    else -> Color.Gray
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Status Icon",
                        tint = statusColor
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            dialogMessage = "Bạn có chắc chắn đã nhận hàng?"
                            dialogAction = {
                                orderViewModel.updateOrderStatus(order.id,"completed"){result,message->
                                    if(result){
                                        Toast.makeText(context, "Nhận hàng thành công", Toast.LENGTH_SHORT).show()
                                        resetData()
                                    }
                                    else{
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                                Log.e("TAG", "OrderItem: Nhan hang", )
                            }
                            showConfirmDialog = true
                        },
                        enabled = order.status == "delivering"
                    ) {
                        Text("Đã nhận hàng")
                    }

                    Button(
                        onClick = {
                            dialogMessage = when (order.status) {
                                "confirm", "initialization" -> "Bạn có chắc chắn muốn hủy đơn hàng?"
                                "completed" -> "Bạn có chắc chắn muốn trả hàng?"
                                else -> ""
                            }
                            dialogAction = {
                                when (order.status) {
                                    "confirm", "initialization" -> {
                                        Log.e("TAG", "OrderItem: Huy", )
                                        orderViewModel.updateOrderStatus(order.id,"cancelled"){result,message->
                                            if(result){
                                                Toast.makeText(context, "Hủy hàng thành công", Toast.LENGTH_SHORT).show()
                                                resetData()
                                            }
                                            else{
                                                Log.e("TAG", "OrderItem: $message", )
                                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                    }
                                    "completed" -> {
                                        orderViewModel.updateOrderStatus(order.id,"refund"){result,message->
                                            if(result){
                                                Toast.makeText(context, "Trả hàng thành công", Toast.LENGTH_SHORT).show()
                                                resetData()
                                            }
                                            else{
                                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        Log.e("TAG", "OrderItem: Tran hang", )
                                    }
                                }
                            }
                            showConfirmDialog = true
                        },
                        enabled = order.status == "confirm" || order.status == "initialization" || (order.status == "completed" && daysSinceCompleted <= 7)
                    ) {
                        Text(
                            text = when (order.status) {
                                "confirm", "initialization" -> "Hủy"
                                "completed" -> "Trả hàng"
                                else -> "Hủy"
                            }
                        )
                    }
                }
                if (showConfirmDialog) {
                    AlertDialog(
                        onDismissRequest = { showConfirmDialog = false },
                        title = { Text(text = "Xác nhận") },
                        text = { Text(text = dialogMessage) },
                        confirmButton = {
                            Button(
                                onClick = {
                                    dialogAction?.invoke()
                                    showConfirmDialog = false
                                }
                            ) {
                                Text("Xác nhận")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showConfirmDialog = false }
                            ) {
                                Text("Hủy")
                            }
                        }
                    )
                }
            }
        }
    }
}