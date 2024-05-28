<?php

use App\Http\Controllers\ReviewController;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\CategoryController;
use App\Http\Controllers\ProductController;
use App\Http\Controllers\FavoriteController;
use App\Http\Controllers\CartController;
use App\Http\Controllers\LocationController;
use App\Http\Controllers\CustomerController;
use App\Http\Controllers\AdminController;
use App\Http\Controllers\OrderController;
use App\Http\Controllers\MessageController;
use App\Http\Controllers\StatsController;

//Category
Route::get('category/{id}', [CategoryController::class, 'index']);
Route::get('category', [CategoryController::class, 'index']);
Route::post('category', [CategoryController::class, 'create']);
Route::put('category/{id}', [CategoryController::class, 'update']);
Route::delete('category/{id}', [CategoryController::class, 'delete']);
Route::get('category/product/{productId}', [CategoryController::class, 'getCategoryByProductId']);


//Products
Route::get('products/price', [ProductController::class, 'searchByPriceRange']);
Route::get('products', [ProductController::class, 'index']);
Route::get('products/filter', [ProductController::class, 'search']);
Route::get('product/{id}', [ProductController::class, 'index'])->where('id', '[0-9]+');
Route::get('products/least/{limit}', [ProductController::class, 'least']);
Route::get('products/search', [ProductController::class, 'searchByName']);
Route::get('products/category/{id}', [ProductController::class, 'productsByCategoryId']);
Route::post('products', [ProductController::class, 'create']);
Route::put('products/{id}', [ProductController::class, 'update']);
Route::delete('products/{id}', [ProductController::class, 'delete']);
Route::get('products/toprate', [ProductController::class, 'topRate']);
Route::get('products/topsale', [ProductController::class, 'topSale']);


//Review
Route::get('reviews/{product_id}', [ReviewController::class, 'index']);
Route::post('reviews', [ReviewController::class, 'create']);


//Favorite
Route::get('favorite', [FavoriteController::class, 'index']);
Route::post('favorite', [FavoriteController::class, 'create']);
Route::post('favoritedel', [FavoriteController::class, 'deleteFavorite']);

//Cart
Route::get('cart', [CartController::class, 'index']);
Route::post('cart', [CartController::class, 'addToCart']);
Route::delete('cart', [CartController::class, 'removeFromCart']);


//Location
Route::get('locations', [LocationController::class, 'index']);
Route::post('addlocation', [LocationController::class, 'create']);
Route::post('editlocation/{id}', [LocationController::class, 'update']);
Route::delete('location/{id}', [LocationController::class, 'delete']);

//Customer
Route::get('customers', [CustomerController::class,'index']);
Route::post('customer/update/{id}', [CustomerController::class,'update']);
Route::post('customer/updateavatar/{id}', [CustomerController::class,'updateAvatar']);
Route::post('customer/updatestatus/{id}', [CustomerController::class,'updateStatus']);
Route::get('customer/{id}', [CustomerController::class,'getCustomerById']);
Route::post('customer/login', [CustomerController::class,'login']);
Route::post('customer/logingoogle', [CustomerController::class,'loginGoogle']);
Route::post('register', [CustomerController::class,'register']);
Route::post('resendcode', [CustomerController::class,'resendVerifyCode']);
Route::post('confirmverifycode', [CustomerController::class,'confirmVerifyCode']);

//Admin
Route::get('admins', [AdminController::class,'getAllAdmins']);
Route::post('admin/create', [AdminController::class, 'store']);
Route::post('admin/login', [AdminController::class, 'login']);
Route::post('admin/update/{id}', [AdminController::class, 'update']);
Route::post('admin/updatepassword/{id}', [AdminController::class, 'updatePassword']);
Route::post('admin/updaterole/{id}', [AdminController::class,'updateAdminRole']);
Route::post('admin/updatestatus/{id}', [AdminController::class,'updateAdminStatus']);
Route::get('admin/{id}', [AdminController::class,'getAdmin']);

//Order
Route::get('orders', [OrderController::class, 'index']);
Route::get('orderdetail/{id}', [OrderController::class, 'show']);
Route::get('orderdetails/{customerId}', [OrderController::class, 'getCustomerOrders']);
Route::post('order', [OrderController::class,'createOrder']);



Route::get('customer/{customerId}/messages', [MessageController::class, 'getCustomerMessages']);
Route::post('messages', [MessageController::class, 'storeMessage']);

Route::get('customers/last-message', [MessageController::class,'getCustomersWithLastMessage']);





Route::get('stats/customer-status', [StatsController::class, 'customerStatusStats']);
Route::get('stats/monthly-revenue', [StatsController::class, 'monthlyRevenue']);
Route::get('stats/category-sales', [StatsController::class, 'categorySales']);
Route::get('stats/customer-reviews', [StatsController::class, 'customerReviews']);
Route::get('stats/cart-quantities', [StatsController::class, 'cartQuantities']);
Route::get('stats/order-status', [StatsController::class, 'orderStatusStats']);
Route::get('stats/monthly-revenue-with-change', [StatsController::class, 'monthlyRevenueWithChange']);
Route::get('stats/monthly-customer-sign-ups-with-change', [StatsController::class, 'monthlyCustomerSignUpsWithChange']);
Route::get('stats/monthly-customer-stats', [StatsController::class, 'monthlyCustomerStats']);
Route::get('stats/monthly-sales', [StatsController::class, 'getMonthlySales']);
Route::get('stats/monthly-revenue-change', [StatsController::class, 'monthlyRevenueChange']);
Route::get('stats/monthly-profit-change', [StatsController::class, 'monthlyProfitChange']);

Route::get('stats/product-count-by-category', [StatsController::class, 'productCountByCategory']);
Route::get('stats/sold-product-count-by-category', [StatsController::class, 'soldProductCountByCategoryWithImage']);



