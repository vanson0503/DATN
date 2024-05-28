package com.example.foodapp.model.order

data class OrderItem(
    val address: String,
    val created_time: String,
    val customer_id: Int,
    val details: List<Detail>,
    val id: Int,
    val name: String,
    val note: String,
    val payment: String,
    val payment_status: String,
    val phone_number: String,
    val status: String,
    val updated_time: String
)