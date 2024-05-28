<?php

use App\Http\Controllers\ChatController;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\HomeController;
use App\Http\Controllers\CategoryController;
use App\Http\Controllers\AdminController;
use App\Http\Controllers\ProductController;
use App\Http\Controllers\CustomerController;
use App\Http\Controllers\OrderController;


Route::get("/",[HomeController::class,"index"])->name('index');
Route::get("banner",[HomeController::class,"banner"])->name('banner');
Route::get("login",[AdminController::class,"login"])->name('login');

Route::get("customer",[CustomerController::class,"index"])->name('customer');
Route::get("admin",[AdminController::class,"index"])->name('admin');
Route::get("order",[OrderController::class,"index"])->name('order');
Route::get("order",[OrderController::class,"index"])->name('order');

Route::get("chat",[ChatController::class,"index"])->name("chat");
Route::get("chatdetail/{id}",[ChatController::class,"chatdetail"])->name("chatdetail");

Route::resource('category', CategoryController::class);
Route::resource('product', ProductController::class);