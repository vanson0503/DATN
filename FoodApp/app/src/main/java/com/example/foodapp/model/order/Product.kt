package com.example.foodapp.model.order

data class Product(
    val calo: Int,
    val category: List<Category>,
    val created_time: String,
    val description: String,
    val discount: Int,
    val id: Int,
    val images: List<Image>,
    val ingredient: String,
    val name: String,
    val price: Int,
    val quantity: Int,
    val updated_time: String
)