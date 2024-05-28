package com.example.foodapp.model.product.topsale

data class ProductSaleItem(
    val average_rating: Float,
    val calo: Int,
    val category: List<Category>,
    val created_time: String,
    val description: String,
    val discount: Int,
    val id: Int,
    val images: List<Image>,
    val ingredient: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val total_reviews: Int,
    val total_sold: Int,
    val updated_time: String
)