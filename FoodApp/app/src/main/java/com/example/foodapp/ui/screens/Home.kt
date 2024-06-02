package com.example.foodapp.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.SystemBarStyle
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodapp.R
import com.example.foodapp.ui.theme.BrightYellow
import com.example.foodapp.ui.theme.CalmTeal
import com.example.foodapp.ui.theme.Green800
import com.example.foodapp.ui.theme.SoftCoral
import com.example.foodapp.viewmodel.MainViewModel


sealed class Screen(
    val route:String,
    @StringRes val title:Int,
){
    data object Home:Screen("home", R.string.home)
    data object Favorite:Screen("favorite",R.string.love)
    data object Order:Screen("order",R.string.order)
    data object Profile:Screen("profile",R.string.profile)
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreenMain(
    onItemSelected: () -> Unit,
    onClickSearch : ()->Unit,
    onClickProduct : (Int)->Unit,
    onCartClicked : ()->Unit,
    onClickOrder:(Int)->Unit,
    onLogout:()->Unit,
    onLocationClicked:()->Unit,
    onSupport:()->Unit,
    onCustomerInfo:()->Unit,
    onClickCategory:(Int)->Unit,
    mainViewModel: MainViewModel = viewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val currentScreen  = mainViewModel.currentScreen
    val navShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
    val iconSize = 35.dp
    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .clip(navShape)
                    .height(100.dp)
            ) {
                BottomNavigationItem(
                    selected = currentScreen.route == Screen.Home.route,
                    onClick = {
                        mainViewModel.navigateTo(Screen.Home)
                        onItemSelected()
                    },
                    icon = {
                        val color = if(currentScreen.route == Screen.Home.route)
                            SoftCoral
                        else
                            Color.Gray
                        Icon(
                            painterResource(id = R.drawable.home),
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(iconSize)
                        )
                           },
                )
                BottomNavigationItem(
                    selected = currentScreen.route == Screen.Favorite.route,
                    onClick = {
                        mainViewModel.navigateTo(Screen.Favorite)
                        onItemSelected()
                    },
                    icon = {
                        val color = if(currentScreen.route == Screen.Favorite.route)
                            Color.Red
                        else
                            Color.Gray
                        Icon(
                            painterResource(id = R.drawable.favorite),
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(iconSize)
                        )
                           },
                )
                BottomNavigationItem(
                    selected = currentScreen.route == Screen.Order.route,
                    onClick = {
                        mainViewModel.navigateTo(Screen.Order)
                        onItemSelected()
                    },
                    icon = {
                        val color = if(currentScreen.route == Screen.Order.route)
                            CalmTeal
                        else
                            Color.Gray
                        Icon(
                            painterResource(id = R.drawable.localmail),
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(iconSize)
                        )
                           },
                )
                BottomNavigationItem(
                    selected = currentScreen.route == Screen.Profile.route,
                    onClick = {
                        mainViewModel.navigateTo(Screen.Profile)
                        onItemSelected()
                    },
                    icon = {
                        val color = if(currentScreen.route == Screen.Profile.route)
                            Green800
                        else
                            Color.Gray
                        Icon(
                            painterResource(id = R.drawable.profile),
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(iconSize)
                        )
                           },
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (currentScreen) {
                is Screen.Home -> HomeContent(
                    onClickSearch=onClickSearch,
                    onClickProduct=onClickProduct,
                    onCartClicked = onCartClicked,
                    onClickCategory = onClickCategory
                )
                is Screen.Favorite -> FavoriteContent(onClickProduct = onClickProduct)
                is Screen.Order -> OrderContent(onClickOrder = onClickOrder)
                is Screen.Profile -> ProfileContent(
                    onLogout = onLogout,
                    onLocationClicked = onLocationClicked,
                    onSupport = onSupport,
                    onCustomerInfo = onCustomerInfo
                )

                Screen.Favorite -> TODO()
                Screen.Home -> TODO()
                Screen.Order -> TODO()
                Screen.Profile -> TODO()
            }
        }
    }
}



@Composable
fun HomeContent(
    onClickSearch : ()->Unit,
    onClickProduct : (Int)->Unit,
    onCartClicked : ()->Unit,
    onClickCategory:(Int)->Unit
) {
    HomeScreen(
        onClickSearch = onClickSearch,
        onClickProduct = onClickProduct,
        onCartClicked = onCartClicked,
        onClickCategory = onClickCategory
    )
}

@Composable
fun FavoriteContent(
    onClickProduct : (Int)->Unit
) {
    FavoriteProductsScreen(
        onClickProduct
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderContent(
    onClickOrder:(Int)->Unit
) {
    OrderMainScreen(
        onClickOrder = onClickOrder
    )
}

@Composable
fun ProfileContent(
    onLogout:()->Unit,
    onLocationClicked:()->Unit,
    onSupport:()->Unit,
    onCustomerInfo:()->Unit
) {
    ProfileScreen(
        onLogout = onLogout,
        onLocationClicked = onLocationClicked,
        onSupport = onSupport,
        onCustomerInfo = onCustomerInfo
    )
}
