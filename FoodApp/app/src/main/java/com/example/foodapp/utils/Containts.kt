package com.example.foodapp.utils

import androidx.compose.ui.graphics.Color
import java.text.NumberFormat
import java.util.Locale

//val BASE_API_URL = "https://vanson.ddns.us/food-api/public/api/"
//val BASE_API_URL = "https://foodapipro.000webhostapp.com/food-api/public/api/"
val BASE_API_URL = "https://vanson.io.vn/food-api/public/api/"
//val BASE_API_URL = "https://57ee-171-228-155-107.ngrok-free.app/food-api/public/api/"
val BASE_IMAGE_PRODUCT_URL = "https://vanson.io.vn/food-api/storage/app/public/product_images/"
//val BASE_IMAGE_PRODUCT_URL =  "https://57ee-171-228-155-107.ngrok-free.app/food-api/storage/app/public/product_images/"
val BASE_IMAGE_CATEGORY_URL = "https://vanson.io.vn/food-api/storage/app/public/category_images/"
//val BASE_IMAGE_CATEGORY_URL = "https://57ee-171-228-155-107.ngrok-free.app/food-api/storage/app/public/category_images/"
val BASE_IMAGE_AVATAR_URL = "https://vanson.io.vn/food-api/storage/app/public/avatars/"
//val BASE_IMAGE_AVATAR_URL = "https://57ee-171-228-155-107.ngrok-free.app/food-api/storage/app/public/avatars/"
val BASE_IMAGE_BANNER_URL = "https://vanson.io.vn/food-api/storage/app/public/banner_images/"




val rainbowColors = listOf(
    Color.Red,
    Color(0xFFFF7F00),  // Orange
    Color.Yellow,
    Color.Green,
    Color.Blue,
    Color(0xFF4B0082),  // Indigo
    Color(0xFF8A2BE2)   // Violet
)


fun isValidPhoneNumber(input: String): Boolean {
    val phoneRegex = Regex("^\\+?[0-9]{10,12}\$")
    return phoneRegex.matches(input)
}

fun isValidEmail(input: String): Boolean {
    // Regex pattern for email
    val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,})+\$")

    return emailRegex.matches(input)
}

fun formatVND(value: Int): String {
    val vietnam = Locale("vi", "VN")
    val format: NumberFormat = NumberFormat.getCurrencyInstance(vietnam)
    return format.format(value)
}

fun formatTotalSold(totalSold: Int): String {
    return if (totalSold >= 1000) {
        String.format("%.1fk", totalSold / 1000.0)
    } else {
        totalSold.toString()
    }
}

fun formatAvgRating(avgRating: Float): String {
    return String.format("%.1f/5", avgRating)
}
