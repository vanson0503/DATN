package com.example.foodapp.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.data.api.RetrofitClient.categoryApiService
import com.example.foodapp.data.api.RetrofitClient.customerApiService
import com.example.foodapp.data.repository.CategoryRepository
import com.example.foodapp.data.repository.CustomerRepository
import com.example.foodapp.model.category.CategoryItem
import com.example.foodapp.model.customer.Customer
import com.example.foodapp.ui.components.CategoryListScreen
import com.example.foodapp.viewmodel.CategoryViewModel
import com.example.foodapp.viewmodel.CustomerViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.foodapp.data.api.RetrofitClient.productApiService
import com.example.foodapp.data.repository.ProductRepository
import com.example.foodapp.model.banner.BannerItem
import com.example.foodapp.ui.theme.CalmTeal
import com.example.foodapp.ui.theme.SoftCoral
import com.example.foodapp.utils.BASE_IMAGE_BANNER_URL
import com.example.foodapp.viewmodel.ProductViewModel
import kotlinx.coroutines.delay


@Composable
fun TopHomeScreen(
    imageUrl : String = ""
){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment =Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 10.dp),

        ) {
        val shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 12.dp)
        Image(
            painter = painterResource(id = R.drawable.menu_regular),
            modifier = Modifier.size(30.dp),
            contentDescription = null)
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(shape)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}






@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun HomeScreen(
    onClickSearch : ()->Unit,
    onClickProduct : (Int)->Unit,
    onCartClicked : () -> Unit,
    onClickCategory:(Int)->Unit
) {
    val customerRepository = remember { CustomerRepository(customerApiService)}
    val productRepository = remember { ProductRepository(productApiService)}
    val customerViewModel:CustomerViewModel = remember { CustomerViewModel(customerRepository) }
    val productViewModel: ProductViewModel = remember { ProductViewModel(productRepository) }
    val customerState = remember { mutableStateOf<Customer?>(null) }
    val categoryRepository = remember { CategoryRepository(categoryApiService) }
    val categoryViewModel: CategoryViewModel = remember { CategoryViewModel(categoryRepository) }
    val categoryListState = remember { mutableStateOf<List<CategoryItem>>(emptyList()) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("customer_data", Context.MODE_PRIVATE)
    val customerIdInfo  = sharedPreferences.getInt("customer_id1",-1)
    val allProductList by productViewModel.getAllProduct.observeAsState()
    val topSaleProductList by productViewModel.getTopSaleProduct.observeAsState()
    val topRateProductList by productViewModel.getTopRateProduct.observeAsState()
    val banner by productViewModel.getBanner.observeAsState()


    LaunchedEffect(categoryViewModel,customerIdInfo) {
        categoryViewModel.getAllCategory { result, categories ->
            if (result) {
                categoryListState.value = categories!!
            } else {
                Toast.makeText(context, "Failed to fetch categories", Toast.LENGTH_SHORT).show()
            }
        }
        productViewModel.getRecommendProductByCustomerId(customerIdInfo)
    }

    val recommendProducts by productViewModel.getRecommendProductByCustomerId.observeAsState()
    if ( recommendProducts == null ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = Color.Black)
            Text("Đang tải...")
        }
        return
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {innerPadding ->

        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            LazyColumn(

                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    MyBanner(
                        banner!!
                    )
                }
                item {
                    if(customerState.value!=null){
                        TopHomeScreen(imageUrl = customerState.value!!.image_url)
                    }
                    Text(
                        text = "Danh mục",
                        style = TextStyle(
                            fontSize = 20.sp,
                            lineHeight = 24.sp,
                            fontFamily = FontFamily(Font(R.font.poppinsregular)),
                            fontWeight = FontWeight.Bold,
                            color =CalmTeal,
                        ),
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp)
                    )
                    CategoryListScreen(categories = categoryListState.value) {id->
                        onClickCategory(id)
                    }
                }

                item {
                    if(topSaleProductList!=null){
                        HorizontalListProduct("Sản phẩm bán chạy",topSaleProductList!!){
                            onClickProduct(it)
                        }
                    }
                }

                item {
                    if(topRateProductList!=null){
                        HorizontalListProduct("Đánh giá cao nhất",topRateProductList!!){
                            onClickProduct(it)
                        }
                    }
                }

                item {
                    if(allProductList!=null){
                        VerticalListProduct("Gợi ý cho bạn", recommendProducts!!){
                            onClickProduct(it)
                        }
                    }
                }

                item {
                    if(allProductList!=null){
                        VerticalListProduct("Sản phẩm",allProductList!!){
                            onClickProduct(it)
                        }
                    }
                }

            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding() + 10.dp,
                    end = 20.dp,
                    start = 20.dp
                )
                .height(50.dp)

        ) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White) // Set background to white
                    .border(2.dp, SoftCoral, RoundedCornerShape(8.dp))
                    .clickable {
                        onClickSearch()
                    }
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(start = 10.dp)

                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = SoftCoral,
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Tìm kiếm...",
                        color = SoftCoral,
                        fontSize =16.sp,
                        modifier = Modifier
                            .padding(start = 10.dp)
                    )
                }
            }


            Image(
                painterResource(id = R.drawable.cart),
                contentDescription = null,
                modifier = Modifier
                    .width(50.dp)
                    .size(35.dp)
                    .align(Alignment.CenterVertically)
                    .clickable {
                        onCartClicked()
                    }
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyBanner(
    banners:List<BannerItem>
) {

    val pagerState = rememberPagerState(pageCount = { banners.size })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        while (true) {
            delay(3000) // Delay for 3 seconds on each page
            val nextPage = if (pagerState.currentPage == pagerState.pageCount - 1) 0 else pagerState.currentPage + 1
            coroutineScope.launch {
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) { page ->
        val url = if(banners[page].img_url.startsWith("https"))  banners[page].img_url else BASE_IMAGE_BANNER_URL+banners[page].img_url
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

