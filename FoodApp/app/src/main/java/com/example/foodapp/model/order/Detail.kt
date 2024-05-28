package com.example.foodapp.model.order

data class Detail(
    val id: Int,
    val orders_id: Int,
    val price: Long,
    val product: Product,
    val product_id: Int,
    val quantity: Int
)