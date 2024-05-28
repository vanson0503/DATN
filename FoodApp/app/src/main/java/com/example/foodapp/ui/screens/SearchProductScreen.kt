package com.example.foodapp.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.RangeSlider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.data.api.RetrofitClient
import com.example.foodapp.data.repository.CategoryRepository
import com.example.foodapp.data.repository.ProductRepository
import com.example.foodapp.model.category.CategoryItem
import com.example.foodapp.ui.theme.CalmTeal
import com.example.foodapp.ui.theme.GraySearchBackground
import com.example.foodapp.ui.theme.NeonYellow
import com.example.foodapp.ui.theme.SoftCoral
import com.example.foodapp.ui.theme.VibrantRed
import com.example.foodapp.utils.BASE_IMAGE_CATEGORY_URL
import com.example.foodapp.utils.formatVND
import com.example.foodapp.viewmodel.CategoryViewModel
import com.example.foodapp.viewmodel.ProductViewModel
import kotlin.io.path.Path


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClicked : ()->Unit,
    onProductClick : (Int) -> Unit
){
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("customer_data", Context.MODE_PRIVATE)
    val customerId = sharedPreferences.getInt("customer_id",-1)
    val productRepository = remember { ProductRepository(RetrofitClient.productApiService) }
    val productViewModel: ProductViewModel = remember { ProductViewModel(productRepository) }
    val categoryRepository = remember { CategoryRepository(RetrofitClient.categoryApiService) }
    val categoryViewModel: CategoryViewModel = remember { CategoryViewModel(categoryRepository) }
    val scrollState = rememberLazyListState()
    val searchText = remember {
        mutableStateOf("")
    }
    var showDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(customerId) {
        categoryViewModel.getAllCategory2()
    }
    val searchProducts by productViewModel.searchProducts.observeAsState()
    val allCategory by categoryViewModel.getAllCategory.observeAsState()

    if (allCategory == null) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color.Black)
            Text("Loading data...")
        }
        return
    }

    Scaffold(
        Modifier.background(Color.Gray),
        topBar = {
            TopAppBar(
                title = {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White) // Set background to white
                                    .border(2.dp, SoftCoral, RoundedCornerShape(8.dp))
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
                                    TextField(
                                        value = searchText.value,
                                        onValueChange = {value->
                                            searchText.value = value
                                        },
                                        placeholder = {
                                            Text(text = "Tìm kiếm...", color = Color.Gray)
                                        },
                                        textStyle = TextStyle(color = SoftCoral, fontSize = 16.sp),
                                        colors = TextFieldDefaults.textFieldColors(
                                            containerColor = Color.Transparent,
                                            cursorColor = Color.Black,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            disabledIndicatorColor = Color.Transparent
                                        ),

                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            imeAction = ImeAction.Search
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onSearch = {
                                                productViewModel.getProductSearch(
                                                    categoryIds = null,
                                                    keyword = searchText.value,
                                                    minPrice = null,
                                                    maxPrice = null,
                                                    minRating = null,
                                                    sortBy = "newest"
                                                )
                                                focusManager.clearFocus()
                                            }
                                        ),
                                        modifier = Modifier

                                            .fillMaxSize()

                                    )
                                }
                            }

                            IconButton(onClick = {
                                showDialog = true
                            }) {
                                Icon(painterResource(
                                    id = R.drawable.filter),
                                    contentDescription = null,
                                    tint = SoftCoral
                                )
                            }
                        }
                },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                    }
                }
            )
        }
    ) {paddingValue->
        if (searchProducts==null){
            Text(text = "avbc")
        }
        else{
            Column(
                Modifier.padding(paddingValue)
            ) {
                LazyColumn(

                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ){
                    item {
                        VerticalListProduct("Tìm thấy ${searchProducts!!.size} sản phẩm", searchProducts!!){
                            onProductClick(it)
                        }
                    }
                }


            }
        }


        FilterModal(
            categoryList = allCategory!!,
            showDialog = showDialog,
            onDismiss = {showDialog = false},
            onNegativeClick = {showDialog = false},
            onPositiveClick = {selectedCategory,minPrice,maxPrice,selectedRating->
                  productViewModel.getProductSearch(
                      categoryIds = selectedCategory,
                      keyword = searchText.value,
                      minPrice = minPrice,
                      maxPrice = maxPrice,
                      minRating = selectedRating,
                      sortBy = "newest"
                  )
                showDialog = false
            },
        )
    }

}




@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilterModal(
    categoryList:List<CategoryItem>,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onNegativeClick:()->Unit,
    onPositiveClick:(List<Int>,Int,Int,Int)->Unit
) {
    var selectedRating by remember { mutableStateOf<Int?>(null) }
    var selectedCategory by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var range by remember { mutableStateOf(0f..1000000f) }
    val ratings = listOf(5, 4, 3, 2, 1)
    if (showDialog) {
        Dialog(
            onDismissRequest = { onDismiss() },
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Column(
                    Modifier
                        .background(Color.White)

                ) {
                    Text(
                        text = "Bộ lọc tìm kiếm",
                        fontSize = 18.sp,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SoftCoral)
                            .padding(10.dp),
                    )
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()

                    ) {

                        Text(
                            text = "Theo Danh Mục",
                            fontFamily = FontFamily(Font(R.font.poppinsregular)),
                            fontSize = 14.sp
                        )
                        LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 100.dp)){
                            items(categoryList.size){index->
                                ChipCategoryItem(categoryList[index]){id,bool->
                                    selectedCategory = if (bool) {
                                        selectedCategory + id
                                    } else {
                                        selectedCategory - id
                                    }

                                }
                            }
                        }
                        Column(
                            modifier = Modifier.height(60.dp),

                        ) {
                            Text(
                                text = "Khoảng giá (đ)",
                                fontFamily = FontFamily(Font(R.font.poppinsregular)),
                                fontSize = 14.sp
                            )
                            
                            RangeSlider(
                                value = range,
                                onValueChange = { range = it },
                                valueRange = 0f..1000000f,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .weight(1f),
                                steps = 4,
                                colors = SliderDefaults.colors(
                                    thumbColor = SoftCoral,
                                    activeTickColor = NeonYellow,
                                    activeTrackColor = SoftCoral
                                )
                            )
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "${formatVND(range.start.toInt())} - ${formatVND(range.endInclusive.toInt())}",
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }

                        }

                        Text(
                            text = "Đánh giá",
                            fontFamily = FontFamily(Font(R.font.poppinsregular)),
                            fontSize = 14.sp
                        )
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 100.dp),
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            items(ratings) { rating ->
                                val selected = selectedRating == rating
                                Box(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .background(
                                            color = if (!selected) GraySearchBackground else SoftCoral,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .clickable {
                                            selectedRating =
                                                if (selectedRating == rating) null else rating
                                        }
                                        .border(
                                            width = 1.dp,
                                            color = if (selected) VibrantRed else Color.Transparent,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                ) {
                                    Canvas(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .align(Alignment.TopStart)
                                            .clip(RoundedCornerShape(4.dp))
                                    ) {
                                        val path = androidx.compose.ui.graphics.Path().apply {
                                            moveTo(0f, 0f)
                                            lineTo(size.width, 0f)
                                            lineTo(0f, size.height)
                                            close()
                                        }
                                        drawPath(
                                            path = path,
                                            color = if (selected) NeonYellow else Color.Transparent
                                        )
                                    }

                                    Text(
                                        modifier = Modifier
                                            .align(Alignment.Center),
                                        text = "$rating sao",
                                        fontFamily = FontFamily(Font(R.font.poppinsregular)),
                                        textAlign = TextAlign.Center,
                                        fontSize = 12.sp,
                                        color = if (selected) Color.White else Color.Black
                                    )
                                }
                            }
                        }

                            Row(

                        ) {
                            TextButton(
                                onClick = onNegativeClick,

                                ) {
                                Text(
                                    text = "Cancel",
                                    fontFamily = FontFamily(Font(R.font.poppinsregular))
                                )
                            }
                            TextButton(
                                onClick = {
                                    onPositiveClick(selectedCategory.toList(),range.start.toInt(),range.endInclusive.toInt(),
                                        if(selectedRating!=null) selectedRating!! else 1
                                    )
                                }
                            ) {
                                Text(
                                    text = "Filter",
                                    fontFamily = FontFamily(Font(R.font.poppinsregular))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChipCategoryItem(
    category: CategoryItem,
    onClick: (Int, Boolean) -> Unit
) {
    var selected by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(40.dp)
            .background(
                color = if (!selected) GraySearchBackground else SoftCoral,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable {
                selected = !selected
                onClick(category.id, selected)
            }
            .border(
                width = 1.dp,
                color = if (selected) VibrantRed else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        Canvas(
            modifier = Modifier
                .size(16.dp)
                .align(Alignment.TopStart)
                .clip(RoundedCornerShape(4.dp))
        ) {
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(0f, size.height)
                close()
            }
            drawPath(
                path = path,
                color = if (selected) NeonYellow else Color.Transparent
            )
        }

        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = category.name,
            fontFamily = FontFamily(Font(R.font.poppinsregular)),
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            color = if (selected) Color.White else Color.Black
        )
    }
}






@Composable
fun SearchBar(
    text: String,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = text,
        onValueChange = { onTextChanged(it) },
        modifier = modifier,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium,
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.fillMaxWidth()
            ) {
                innerTextField()
            }
        }
    )
}

