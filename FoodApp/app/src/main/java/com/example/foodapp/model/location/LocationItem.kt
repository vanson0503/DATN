package com.example.foodapp.model.location

data class LocationItem(
    val address: String,
    val created_time: String,
    val customer_id: Int,
    val id: Int,
    val is_default: Int,
    val name: String,
    val phone_number: String,
    val updated_time: String
)