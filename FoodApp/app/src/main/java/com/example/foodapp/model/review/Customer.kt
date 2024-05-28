package com.example.foodapp.model.review

data class Customer(
    val created_time: String,
    val email: String,
    val full_name: String,
    val id: Int,
    val image_url: String,
    val password: String,
    val phone_number: String,
    val social_name: Any,
    val status: String,
    val updated_time: String,
    val verify_code: Any
)