package com.example.foodapp.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foodapp.R


sealed class Screen(
    val route:String,
    @StringRes val title:Int,
    val icon:ImageVector
){
    data object Home:Screen("home", R.string.home, Icons.Rounded.Home)
    data object Favorite:Screen("favorite",R.string.love,Icons.Rounded.Favorite)
    data object Order:Screen("order",R.string.order,Icons.Rounded.ShoppingCart)
    data object Profile:Screen("profile",R.string.profile,Icons.Rounded.Person)
}


@Composable
fun CustomBottomNavigation(
    selectedRoute:String,
    onItemSelected:(Screen)->Unit
){
    val items = listOf(
        Screen.Home,
        Screen.Favorite,
        Screen.Order,
        Screen.Profile
    )
    val navShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(navShape)
            .background(MaterialTheme.colorScheme.secondary)
            .padding(top = 14.dp, bottom = 34.dp),
    ) {
        items.forEach{
            val isSelected = it.route==selectedRoute

            val color = if(isSelected)
                Color.Cyan
            else
                Color.Gray

            IconButton(onClick = {
                if(isSelected) onItemSelected(it)
            }) {
                Icon(
                    imageVector = it.icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}






