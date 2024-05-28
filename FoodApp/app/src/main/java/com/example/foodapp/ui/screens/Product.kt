package com.example.foodapp.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.data.api.RetrofitClient
import com.example.foodapp.data.repository.CartRepository
import com.example.foodapp.data.repository.ProductRepository
import com.example.foodapp.data.repository.ReviewRepository
import com.example.foodapp.model.product.ProductItem
import com.example.foodapp.model.review.ReviewItem
import com.example.foodapp.ui.components.CustomToast
import com.example.foodapp.ui.theme.CalmTeal
import com.example.foodapp.ui.theme.NeonYellow
import com.example.foodapp.ui.theme.SoftCoral
import com.example.foodapp.ui.theme.VibrantRed
import com.example.foodapp.utils.BASE_IMAGE_PRODUCT_URL
import com.example.foodapp.utils.formatAvgRating
import com.example.foodapp.utils.formatTotalSold
import com.example.foodapp.utils.formatVND
import com.example.foodapp.viewmodel.CartViewModel
import com.example.foodapp.viewmodel.ProductViewModel
import com.example.foodapp.viewmodel.ReviewViewModel
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch


@Composable
fun HorizontalListProduct(
    title: String,
    productList: List<ProductItem>,
    onProductClick : (Int)-> Unit
){
    Text(
        text = title,
        style = TextStyle(
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontFamily = FontFamily(Font(R.font.poppinsregular)),
            fontWeight = FontWeight.Bold,
            color =CalmTeal,
        ),
        modifier = Modifier
            .padding( horizontal = 30.dp)
    )
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            repeat(productList.size){index->
                ItemProduct(productList[index]){
                    onProductClick(it)
                }
            }
        }
    }

}



@Composable
fun VerticalListProduct(
    title: String,
    productList: List<ProductItem>,
    onProductClick: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontFamily = FontFamily(Font(R.font.poppinsregular)),
                fontWeight = FontWeight.Bold,
                color =CalmTeal,
            ),
            modifier = Modifier
                .padding( horizontal = 30.dp)
        )

        // Create rows of two columns
        val chunkedProducts = productList.chunked(2)
        chunkedProducts.forEach { rowProducts ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowProducts.forEach { product ->
                    ItemProduct2(modifier = Modifier.weight(0.5f), product = product) {
                        onProductClick(product.id)
                    }
                }

            }
        }
    }
}



@Composable
fun ItemProduct2(
    modifier: Modifier,
    product : ProductItem,
    onProductClick : (Int)->Unit
){
    Column(
        modifier = modifier
            .padding(10.dp)
            .clickable {
                onProductClick(product.id)
            }
            .background(Color.White)
    ) {
        Box {
            val imageUrl = if(product.images[0].imgurl.startsWith("https")) product.images[0].imgurl else BASE_IMAGE_PRODUCT_URL+product.images[0].imgurl
            AsyncImage(
                model = imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(150.dp)
            )
            if (product.discount > 0) {
                Text(
                    text = "-${product.discount}%",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(
                            color = SoftCoral,
                            shape = RoundedCornerShape(bottomStart = 4.dp) // Rounded corner only at bottom-left
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    maxLines = 1,
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.4f), // Semi-transparent background
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_star),
                    contentDescription = "Rating",
                    tint = Color.Yellow,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "${String.format("%.1f", product.average_rating)} (${product.total_reviews} reviews)",
                    color = Color.White,
                    fontSize = 10.sp
                )
            }

        }
        Text(
            text = product.name,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .height(50.dp)
                .padding(horizontal = 5.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 5.dp)
        ) {
            val price = product.price - product.price * (product.discount.toDouble() / 100)

            Text(
                text = formatVND(price.toInt()),
                Modifier.weight(1f),
                color = VibrantRed,
                fontSize = 16.sp,
            )
            Text(
                text = "Đã bán "+formatTotalSold(product.total_sold),
                color = Color.Black,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
fun ItemProduct(
    product : ProductItem,
    onProductClick : (Int)->Unit
){
    Column(
        modifier = Modifier
            .width(200.dp)
            .padding(10.dp)
            .clickable {
                onProductClick(product.id)
            }
            .background(Color.White)
    ) {
        Box {
            val imageUrl = if(product.images[0].imgurl.startsWith("https")) product.images[0].imgurl else BASE_IMAGE_PRODUCT_URL+product.images[0].imgurl
            AsyncImage(
                model = imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(150.dp)
            )
            if (product.discount > 0) {
                Text(
                    text = "-${product.discount}%",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(
                            color = SoftCoral,
                            shape = RoundedCornerShape(bottomStart = 4.dp) // Rounded corner only at bottom-left
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    maxLines = 1,
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.4f), // Semi-transparent background
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_star),
                    contentDescription = "Rating",
                    tint = Color.Yellow,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "${String.format("%.1f", product.average_rating)} (${product.total_reviews} reviews)",
                    color = Color.White,
                    fontSize = 10.sp
                )
            }

        }
        Text(
            text = product.name,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .height(50.dp)
                .padding(horizontal = 5.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 5.dp)
        ) {
            val price = product.price - product.price * (product.discount.toDouble() / 100)

            Text(
                text = formatVND(price.toInt()),
                Modifier.weight(1f),
                color = VibrantRed,
                fontSize = 16.sp,
            )
            Text(
                text = "Đã bán "+formatTotalSold(product.total_sold),
                color = Color.Black,
                fontSize = 12.sp,
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProductDetailScreen(
    id: Int,
    onBackClicked: () -> Unit,
    onCartClicked: () -> Unit,
    viewAllReviewByProductId: (Int) -> Unit,
) {
    val context = LocalContext.current
    val productRepository = remember { ProductRepository(RetrofitClient.productApiService) }
    val productViewModel: ProductViewModel = remember { ProductViewModel(productRepository) }
    val reviewRepository = remember { ReviewRepository(RetrofitClient.reviewApiService) }
    val reviewViewModel: ReviewViewModel = remember { ReviewViewModel(reviewRepository) }
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val cartRepository: CartRepository = remember { CartRepository(RetrofitClient.cartApiService) }
    val cartViewModel: CartViewModel = remember { CartViewModel(cartRepository) }
    val sharedPreferences = context.getSharedPreferences("customer_data", Context.MODE_PRIVATE)
    val customerId = sharedPreferences.getInt("customer_id", -1)
    var isFavorite by remember { mutableStateOf(false) }
    var showToast by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        productViewModel.getProductById(id)
        reviewViewModel.getReviewByProductId(id)
        productViewModel.getFavoriteProductByCustomerId(customerId)
    }
    val product by productViewModel.getProductById.observeAsState()
    val reviews by reviewViewModel.getReviewsByProductId.observeAsState()
    val productsFavorite by productViewModel.getFavoriteProductByCustomerId.observeAsState()

    LaunchedEffect(productsFavorite) {
        if (productsFavorite != null) {
            isFavorite = productsFavorite!!.any { it.id == id }
        }
    }

    if (product == null || reviews == null || productsFavorite == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = Color.Black)
            Text("Loading product details...")
        }
        return
    }

    val imageUrls = product!!.images.map { it.imgurl }
    var selectedImageUrl by remember { mutableStateOf(imageUrls.firstOrNull() ?: "") }
    val pagerState = rememberPagerState()
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage in imageUrls.indices) {
            selectedImageUrl = imageUrls[pagerState.currentPage]
        }
    }

    val dynamicBackgroundColor by remember {
        derivedStateOf {
            if (scrollState.firstVisibleItemIndex > 0 || scrollState.firstVisibleItemScrollOffset > 100) {
                Color.White
            } else {
                Color.Transparent
            }
        }
    }

    Scaffold(
        Modifier.background(Color.Gray),
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(50.dp)
            ) {
                Button(
                    onClick = {
                        cartViewModel.updateCartItemQuantity(
                            productId = id,
                            customerId = customerId,
                            quantity = 1,
                            add = true,
                            onResult = { result ->
                                if (result) {
                                    Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Thêm thất bại", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RectangleShape
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add to Cart",
                        tint = Color.White
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Thêm vào giỏ hàng", color = Color.White, fontSize = 14.sp)
                }

                // Buy Now Button
//                Button(
//                    onClick = { },
//                    modifier = Modifier
//                        .weight(1f)
//                        .fillMaxHeight(),
//                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
//                    shape = RectangleShape
//                ) {
//                    Text("Mua ngay", color = Color.White, fontSize = 18.sp)
//                }
            }
        }
    ) {

        Column(Modifier.padding(it)) {
            LazyColumn(state = scrollState) {
                item {
                    Box {
                        Column {
                            HorizontalPager(
                                count = imageUrls.size,
                                state = pagerState,
                                modifier = Modifier
                                    .height(300.dp)
                                    .fillMaxWidth()
                            ) { page ->
                                val imageUrl = imageUrls[page]
                                val fullImageUrl = if (imageUrl.startsWith("https")) imageUrl else BASE_IMAGE_PRODUCT_URL + imageUrl
                                AsyncImage(
                                    model = fullImageUrl,
                                    contentDescription = "Selected Product Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .horizontalScroll(rememberScrollState())
                            ) {
                                imageUrls.forEach { imageUrl ->
                                    val thumbnailUrl = if (imageUrl.startsWith("https")) imageUrl else BASE_IMAGE_PRODUCT_URL + imageUrl
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 8.dp, start = 8.dp)
                                            .size(100.dp)
                                            .border(
                                                width = if (selectedImageUrl == imageUrl) 2.dp else 0.dp,
                                                color = if (selectedImageUrl == imageUrl) Color.Red else Color.Transparent,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .clickable {
                                                coroutineScope.launch {
                                                    pagerState.scrollToPage(imageUrls.indexOf(imageUrl))
                                                }
                                            }
                                    ) {
                                        AsyncImage(
                                            model = thumbnailUrl,
                                            contentDescription = "Product Thumbnail",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(4.dp))
                                            ,
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.padding(8.dp),
                    ) {
                        if (product!!.discount != 0) {
                            val discountedPrice = product!!.price - (product!!.price * product!!.discount.toDouble() / 100)

                            // Display the discounted price
                            Text(
                                text = formatVND(discountedPrice.toInt()),
                                color = Color.Red,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Display the original price with strikethrough
                            Text(
                                text = formatVND(product!!.price),
                                color = Color.Gray,
                                fontSize = 16.sp,
                                textDecoration = TextDecoration.LineThrough
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Discount percentage tag
                            Box(
                                modifier = Modifier
                                    .background(Color.Yellow)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "-${product!!.discount}%",
                                    color = Color.Red,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            // Only show the original price if there is no discount
                            Text(
                                text = formatVND(product!!.price),
                                color = Color.Gray,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    // Product name
                    Text(
                        text = product!!.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                    )

                    // Rate
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween, // This arranges children to start and end of the container
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        // Start grouping
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Rating Star",
                                tint = if (product!!.average_rating > 0) Color.Yellow else Color.Gray,
                                modifier = Modifier
                                    .size(25.dp)
                                    .padding(end = 4.dp)
                            )
                            Text(
                                text = formatAvgRating(product!!.average_rating),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Spacer(
                                modifier = Modifier
                                    .width(10.dp)
                                    .background(Color.Black)
                            )
                            Text(
                                text = "Đã bán: ${product!!.total_sold}",
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        // Favorite icon on the right
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (isFavorite) "Unmark as favorite" else "Mark as favorite",
                            tint = if (isFavorite) Color.Red else Color.Gray,
                            modifier = Modifier
                                .clickable {

                                    if (!isFavorite) {
                                        productViewModel.addToFavorite(
                                            product!!.id,
                                            customerId,
                                            onResult = { result ->
                                                if (result) {
                                                    isFavorite = !isFavorite
                                                }
                                            })
                                    } else {
                                        productViewModel.deleteFavorite(
                                            product!!.id,
                                            customerId,
                                            onResult = { result ->
                                                Log.e("TAG", "ProductDetailScreen: $result")
                                                if (result) {
                                                    isFavorite = !isFavorite
                                                }
                                            })
                                    }
                                    showToast = true
                                }
                                .padding(start = 8.dp)
                        )
                    }
                }
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if(product!!.quantity>0){
                            Text(
                                text = "Số lượng: ${product!!.quantity}",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        else{
                            Text(
                                text = "Hết hàng",
                                fontSize = 18.sp,
                                color = Color.Red,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        Text(
                            text = "Mô tả: ${product!!.description}",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Nguyên liệu: ${product!!.ingredient}",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Calories: ${product!!.calo} kcal",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left side - Product rating and star icons
                        Column {
                            Text(
                                text = "Đánh giá sản phẩm",
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${"★".repeat((product!!.average_rating).toInt())}${"☆".repeat(5 - (product!!.average_rating).toInt())}",
                                    fontSize = 16.sp,
                                    color = Color.Yellow
                                )
                                Text(
                                    text = formatAvgRating(product!!.average_rating),
                                    fontSize = 14.sp,
                                    color = Color.Red
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(${product!!.total_reviews} đánh giá)",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        // Right side - See all with arrow icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewAllReviewByProductId(product!!.id) }
                        ) {
                            Text(
                                text = "Xem tất cả",
                            )
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowRight,
                                contentDescription = "See All Reviews",
                                tint = Color.Blue
                            )
                        }
                    }
                    repeat(3) { index ->
                        ReviewItem(reviews!![index])
                    }
                }
                item{
                    Text(
                        text = "Sản phẩm liên quan",
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            }
        }
        Row {
            HeaderRow(
                onBackClicked = { onBackClicked() },
                onCartClicked = { onCartClicked() },
                paddingValues = it,
                backgroundColor = dynamicBackgroundColor
            )
        }
    }

    if (isFavorite && showToast) {
        CustomToast(
            message = "Thêm thành công vào yêu thích!",
            onDismiss = {},
            icon = Icons.Filled.Favorite
        )
    }
    if (!isFavorite && showToast) {
        CustomToast(
            message = "Xóa thành công khỏi yêu thích!",
            onDismiss = { },
            icon = Icons.Filled.FavoriteBorder
        )
    }
}





@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoriteProductsScreen(
    onProductClick : (Int) -> Unit
){
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("customer_data", Context.MODE_PRIVATE)
    val customerId = sharedPreferences.getInt("customer_id",-1)
    val productRepository = remember { ProductRepository(RetrofitClient.productApiService) }
    val productViewModel: ProductViewModel = remember { ProductViewModel(productRepository) }
    val scrollState = rememberLazyListState()
    LaunchedEffect(customerId) {
        productViewModel.getFavoriteProductByCustomerId(customerId)
    }
    val favoriteProducts by productViewModel.getFavoriteProductByCustomerId.observeAsState()

    if (favoriteProducts == null) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color.Black)
            Text("Loading favorite product...")
        }
        return
    }

    if (favoriteProducts!!.isEmpty()) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

            Text("Chưa có sản phẩm yêu thích")
        }
        return
    }

    Scaffold(
        Modifier.background(Color.Gray),
    ) {

        Column(Modifier.padding(it)) {
            LazyColumn(state = scrollState) {
                item {
                    VerticalListProduct(
                        title = "Sản phẩm yêu thích",
                        productList = favoriteProducts!!,
                        onProductClick = onProductClick
                    )
                }

            }


        }
    }




}







@Composable
fun HeaderRow(
    onBackClicked: () -> Unit,
    onCartClicked: () -> Unit,
    paddingValues: PaddingValues,
    backgroundColor:Color
) {
    Row (
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth()
            .background(color = backgroundColor),

        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .background(color = CalmTeal, shape = CircleShape)
                    .clickable { onBackClicked() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .background(color = CalmTeal, shape = CircleShape)
                    .clickable { onCartClicked() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "Cart",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}