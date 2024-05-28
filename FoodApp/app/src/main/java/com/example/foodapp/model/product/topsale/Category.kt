package com.example.foodapp.model.product.topsale

data class Category(
    val created_time: String,
    val id: Int,
    val image_url: String,
    val name: String,
    val pivot: Pivot,
    val updated_time: String
)