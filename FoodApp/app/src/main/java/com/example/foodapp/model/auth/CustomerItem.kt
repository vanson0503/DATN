package com.example.foodapp.model.auth

data class CustomerItem(
    val created_time: String,
    val email: String,
    val full_name: String,
    val id: Int,
    val image_url: String,
    val password: String,
    val phone_number: String,
    val social_name: String,
    val status: String,
    val updated_time: String
)