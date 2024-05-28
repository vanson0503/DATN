package com.example.foodapp.model.review

data class ReviewItem(
    val content: String,
    val created_time: String,
    val customer: Customer,
    val customer_id: Int,
    val orders_id: Int,
    val id: Int,
    val product_id: Int,
    val rate: Int,
    val updated_time: String
)