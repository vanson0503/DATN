package com.example.foodapp.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.foodapp.data.api.RetrofitClient
import com.example.foodapp.data.repository.ReviewRepository
import com.example.foodapp.model.review.ReviewItem
import com.example.foodapp.utils.BASE_IMAGE_AVATAR_URL
import com.example.foodapp.utils.BASE_IMAGE_PRODUCT_URL
import com.example.foodapp.utils.formatAvgRating
import com.example.foodapp.utils.formatVND
import com.example.foodapp.viewmodel.ReviewViewModel
import com.google.accompanist.pager.HorizontalPager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAllReviewByProductIdScreen(
    id:Int,
    onBackClicked: () -> Unit,
    onCartClicked: () -> Unit,
){
    val reviewRepository = remember { ReviewRepository(RetrofitClient.reviewApiService) }
    val reviewViewModel: ReviewViewModel = remember { ReviewViewModel(reviewRepository) }

    LaunchedEffect(id) {
        reviewViewModel.getReviewByProductId(id)
    }

    val reviews by reviewViewModel.getReviewsByProductId.observeAsState()

    if (reviews==null) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color.Black)
            Text("Loading reviews...")
        }
        return
    }

    Scaffold(
        Modifier.background(Color.Gray),
        topBar = {
            TopAppBar(
                title = { Text("Product Reviews") },
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
        },
    ) {

        Column(Modifier.padding(it)) {
            LazyColumn() {
                item {
                    repeat(reviews!!.size){ index->
                        ReviewItem(reviews!![index] )
                    }
                }
            }


        }

    }
}

@Composable
fun ReviewItem(review: ReviewItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),

        ) {
        Log.e("TAG", "ReviewItem: ${review.toString()}", )
        AsyncImage(
            model  = if(review.customer.image_url.startsWith("https")) review.customer.image_url else BASE_IMAGE_AVATAR_URL+review.customer.image_url,
            contentDescription = "Customer Image",
            modifier = Modifier
                .size(48.dp)
                .padding(4.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = review.customer.full_name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Rating: ${"★".repeat(review.rate)}${"☆".repeat(5 - review.rate)}",
                color = Color.Yellow,
                fontSize = 14.sp
            )
            Text(
                text = if(review.content==null) "None" else review.content,
                fontSize = 14.sp
            )
        }
    }
}

