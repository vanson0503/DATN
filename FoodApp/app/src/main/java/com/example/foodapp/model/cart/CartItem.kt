package com.example.foodapp.model.cart

data class CartItem(
    val average_rating: Float,
    val calo: Int,
    val cart_quantity: Int,
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
    val total_reviews: Int,
    val updated_time: String
)