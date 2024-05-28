package com.example.foodapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.foodapp.ui.screens.AddLocationScreen
import com.example.foodapp.ui.screens.TestShowProducts
import com.example.foodapp.ui.screens.CartScreen
import com.example.foodapp.ui.screens.ChangePasswordScreen
import com.example.foodapp.ui.screens.ChatScreen
import com.example.foodapp.ui.screens.CheckoutFormCartScreen
import com.example.foodapp.ui.screens.EditLocationScreen
import com.example.foodapp.ui.screens.HomeScreenMain
import com.example.foodapp.ui.screens.LocationListScreen
import com.example.foodapp.ui.screens.OrderDetailScreen
import com.example.foodapp.ui.screens.ProductDetailScreen
import com.example.foodapp.ui.screens.ProfileDetailScreen
import com.example.foodapp.ui.screens.ProfileScreen
import com.example.foodapp.ui.screens.SearchScreen
import com.example.foodapp.ui.screens.UpdateProfileScreen
import com.example.foodapp.ui.screens.ViewAllReviewByProductIdScreen
import com.example.foodapp.ui.screens.auth.EnterResetPassword
import com.example.foodapp.ui.screens.auth.ForgotPasswordScreen
import com.example.foodapp.ui.screens.auth.LoginScreen
import com.example.foodapp.ui.screens.auth.RegisterScreen
import com.example.foodapp.ui.screens.auth.VerifyCodeForgotPasswordScreen
import com.example.foodapp.ui.screens.auth.VerifyCodeScreen
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.viewmodel.ChatModelFactory
import com.example.foodapp.viewmodel.ChatViewModel
import com.example.foodapp.viewmodel.GoogleAuthUiClient
import com.example.foodapp.viewmodel.SignInState
import com.example.foodapp.viewmodel.SignInViewModel
import com.example.foodapp.viewmodel.UserData
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp(
                        lifecycleScope = lifecycleScope,
                        googleAuthUiClient = googleAuthUiClient,
                    )
                }
            }
        }
    }
}




@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainApp(
    lifecycleScope : LifecycleCoroutineScope,
    googleAuthUiClient: GoogleAuthUiClient,
) {
    val context  = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("customer_data", Context.MODE_PRIVATE)
    var customerIdInfo by remember {
        mutableIntStateOf(sharedPreferences.getInt("customer_id", -1))
    }
    val preferenceChangeListener = remember {
        SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == "customer_id") {
                customerIdInfo = prefs.getInt(key, -1)
            }
        }
    }
    DisposableEffect(sharedPreferences) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
        onDispose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        }
    }
    val factory = remember(customerIdInfo) {
        ChatModelFactory(customerIdInfo)
    }
    val chatViewModel: ChatViewModel = viewModel(factory = factory)

    LaunchedEffect(customerIdInfo) {
        chatViewModel.reset(customerIdInfo)
        Log.d("YourComposable", "Customer ID changed to: $customerIdInfo")
    }
    var defaultRouter = "loginScreen"
    if(customerIdInfo!=-1){
        defaultRouter = "homeScreen"
    }
    Scaffold {
        val navController = rememberNavController()
        FoodAppTheme {
            NavHost(navController = navController, startDestination = defaultRouter){
                composable("registerScreen"){
                    RegisterScreen (
                        lifecycleScope = lifecycleScope,
                        googleAuthUiClient = googleAuthUiClient,
                        forgotPassword = {
                            navController.navigate("ForgotPasswordScreen")
                        },
                        login = {
                            navController.popBackStack()
                            navController.navigate("loginScreen")
                        },
                        onRegisterSuccess = { email->
                            navController.navigate("verify/$email")
                        },
                        registerGoogleSuccess = {
                            navController.navigate("homeScreen")
                        }
                    )
                }
                composable("loginScreen"){
                    LoginScreen(
                        lifecycleScope = lifecycleScope,
                        googleAuthUiClient = googleAuthUiClient,
                        forgotPassword = {
                            navController.navigate("ForgotPasswordScreen")
                        },
                        register = {
                            navController.navigate("registerScreen")
                        },
                        loginSuccess = {
                            navController.popBackStack()
                            navController.navigate("homeScreen")
                        })
                }
                composable(
                    "verify/{email}",
                    arguments = listOf(
                        navArgument("email"){
                            type = NavType.StringType
                        }
                    )
                ){
                    val email = it.arguments!!.getString("email")
                    VerifyCodeScreen(
                        email = email!!,
                        onVerifySuccess = {
                            navController.popBackStack()
                            navController.navigate("loginScreen")
                        },
                        onBackClicked = {
                            navController.popBackStack()
                        }
                    )

                }
                composable("ForgotPasswordScreen"){
                    ForgotPasswordScreen(
                        onBackClicked = {navController.popBackStack()},
                        onLoginClick = {},
                        onSendEmailSuccess = {email->
                            navController.navigate("VerifyCodeForgotPasswordScreen/${email}")
                        }
                    )
                }

                composable(
                    "VerifyCodeForgotPasswordScreen/{email}",
                    arguments = listOf(
                        navArgument("email"){
                            type = NavType.StringType
                        }
                    ),
                ){
                    val email = it.arguments!!.getString("email")
                    VerifyCodeForgotPasswordScreen(
                        email = email!!,
                        onBackClicked = {navController.popBackStack()},
                        onVerifySuccess = {email->
                            navController.popBackStack()
                            navController.navigate("EnterResetPassword/${email}")
                        }
                    )
                }
                composable(
                    "EnterResetPassword/{email}",
                    arguments = listOf(
                        navArgument("email"){
                            type = NavType.StringType
                        }
                    ),
                ){
                    val email = it.arguments!!.getString("email")
                    EnterResetPassword(
                        email = email!!,
                        onBackClicked = {
                            navController.popBackStack()
                        },
                        onResetSuccess = {
                            navController.navigate("loginScreen")
                        }
                    )
                }
                composable("homeScreen"){

                    HomeScreenMain(
                        onClickSearch = {
                            navController.navigate("searchScreen")
                        },
                        onClickProduct = {
                            navController.navigate("productDetail/$it")
                        },
                        onItemSelected = {

                        },
                        onCartClicked = {
                            navController.navigate("cart")
                        },
                        onClickOrder = {id->
                            navController.navigate("orderdetailscreen/$id")
                        },
                        onLogout = {
                            sharedPreferences.edit().putInt("customer_id", -1).apply()
                            navController.navigate("loginScreen"){
                                popUpTo("homeScreen") { inclusive = true }
                            }
                        },
                        onLocationClicked = {
                            navController.navigate("locationlistscreen")
                        },
                        onSupport = {
                            navController.navigate("chatscreen")
                        },
                        onCustomerInfo = {
                            navController.navigate("profileDetailScreen")
                        },

                    )
                }
                composable("searchScreen"){
                    SearchScreen(
                        onBackClicked = {
                            navController.popBackStack()
                        },
                        onProductClick = {
                            navController.navigate("productDetail/$it")
                        }
                    )
                }

                composable("chatscreen"){
                    ChatScreen(
                        viewModel = chatViewModel,
                        onBackClicked = {
                            navController.popBackStack()
                        }
                    )
                }


                composable(
                    "productDetail/{id}",
                    arguments = listOf(
                        navArgument("id"){
                            type = NavType.IntType
                        }
                    ),

                    ){
                    val id = it.arguments!!.getInt("id")
                    ProductDetailScreen(
                        id = id,
                        onBackClicked = {
                            navController.popBackStack()
                        },
                        onCartClicked = {
                            navController.navigate("cart")
                        },
                        viewAllReviewByProductId = {productId->
                            navController.navigate("reviewsByProduct/$productId")
                        }
                    )

                }

                composable("profileDetailScreen"){
                    ProfileDetailScreen(
                        onBackClicked = {navController.popBackStack()},
                        onEditClick = {navController.navigate("updateProfileScreen")},
                        onUpdatePassword = {
                            navController.navigate("ChangePasswordScreen")
                        }
                    )
                }

                composable("ChangePasswordScreen"){
                    ChangePasswordScreen(
                        onBackClicked = {navController.popBackStack()},
                        onUpdatePasswordSuccess = {
                            navController.popBackStack()
                        }
                    )
                }

                composable("updateProfileScreen"){
                    UpdateProfileScreen(
                        onBackClicked = { navController.popBackStack()},
                        onSave = {}
                    )
                }

                composable(
                    route = "reviewsByProduct/{id}",
                    arguments = listOf(
                        navArgument("id"){
                            type = NavType.IntType
                        }
                    ),
                ){
                    val id = it.arguments!!.getInt("id")
                    ViewAllReviewByProductIdScreen(
                        id= id,
                        onBackClicked = {
                            navController.popBackStack()
                        },
                        onCartClicked = {
                            navController.navigate("cart")
                        }
                    )
                }

                composable("cart"){
                    CartScreen(
                        onClickProduct = {id->
                            navController.navigate("productDetail/$id")
                        },
                        onBackClicked = {
                            navController.popBackStack()
                        },
                        onClickCheckOut = {
                            navController.navigate("checkoutfromcart")
                        }
                    )
                }
                composable("checkoutfromcart"){
                    CheckoutFormCartScreen(
                        onBackClicked = {
                            navController.popBackStack()
                        },
                        onClickProduct = {id->
                            navController.navigate("productDetail/$id")
                        },
                        onCheckoutClick = {
                            navController.navigate("homeScreen")
                        },
                        onAddLocationClick ={
                            navController.navigate("addlocationscreen")
                        }
                    )
                }

                composable("addlocationscreen"){
                    AddLocationScreen(
                        onBackClicked = {
                            navController.popBackStack()
                        },
                        onAddSuccess = {
                            navController.popBackStack()
                        }
                    )
                }
                composable(
                    "editlocationscreen/{id}",
                    arguments = listOf(
                        navArgument("id"){
                            type = NavType.IntType
                        }
                    ),
                ){
                    val id = it.arguments!!.getInt("id")
                    EditLocationScreen(
                        id = id,
                        onBackClicked = {
                            navController.popBackStack()
                        },
                        onEditSuccess = {
                            navController.popBackStack()
                        },
                        onDeleteSuccess = {
                            navController.popBackStack()
                        }
                    )
                }
                composable("locationlistscreen"){
                    LocationListScreen(
                        onBackClicked = {
                            navController.popBackStack()
                        },
                        onAddLocation = {
                            navController.navigate("addlocationscreen")
                        },
                        onEditLocation = {id->
                            navController.navigate("editlocationscreen/$id")
                        }
                    )
                }

                composable(
                    "orderdetailscreen/{id}",
                    arguments = listOf(
                        navArgument("id"){
                            type = NavType.IntType
                        }
                    ),
                ){
                    val id = it.arguments!!.getInt("id")
                    OrderDetailScreen(
                        id = id,
                        onBackClicked = {
                            navController.popBackStack()
                        },
                        onCartClicked = {
                            navController.navigate("cart")
                        }
                    )
                }
            }
        }
    }

}


