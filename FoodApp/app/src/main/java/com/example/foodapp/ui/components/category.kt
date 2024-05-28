package com.example.foodapp.ui.components

import android.graphics.drawable.shapes.Shape
import android.icu.text.CaseMap.Title
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foodapp.model.category.CategoryItem
import com.example.foodapp.ui.theme.CalmTeal
import com.example.foodapp.utils.BASE_IMAGE_CATEGORY_URL

@Composable
fun CategoryItemScreen(
    id:Int,
    imageUrl:String,
    title: String,
    onClickCategory:(Int)->Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {
            onClickCategory(id)
        }
    ) {
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(10.dp),
                    spotColor = Color(0xFF2C3128),
                    ambientColor = Color(0xADD8DAE0),

                )

                .padding(10.dp)
                .size(122.dp)
                .background(color = Color.White, shape = RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = BASE_IMAGE_CATEGORY_URL + imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(96.dp, 81.dp)
                    .background(color = Color.Transparent)
            )
        }


        Text(
            text = title,
            modifier = Modifier
                .height(40.dp)
                .padding(vertical = 8.dp)
                .wrapContentHeight(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
            color = Color.Black
        )
    }
}

@Composable
fun CategoryListScreen(
    categories: List<CategoryItem>,
    onClickCategory: (Int) -> Unit
) {
    LazyRow(contentPadding = PaddingValues(start = 2.dp, end = 2.dp)) {
        items(categories.size) { i ->
            Column(
                modifier = Modifier.padding(horizontal = 4.dp)

            ) {
                CategoryItemScreen(
                    id = categories[i].id,
                    imageUrl = categories[i].image_url,
                    title = categories[i].name,
                    onClickCategory = onClickCategory
                )
            }
        }
    }
}

