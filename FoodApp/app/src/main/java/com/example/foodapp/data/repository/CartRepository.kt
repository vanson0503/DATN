package com.example.foodapp.data.repository

import com.example.foodapp.data.api.CartApiService
import com.example.foodapp.model.ResponseMessage
import com.example.foodapp.model.cart.CartItem

class CartRepository(private val cartApiService: CartApiService) {
    suspend fun getCartByCustomerId(customerId:Int):List<CartItem>{
        return try {
            cartApiService.getCartByCustomerId(customerId)
        }
        catch(e:Exception){
            throw e
        }
    }

    suspend fun addToCart(productId: Int, customerId: Int, quantity: Int,add: Boolean=true): ResponseMessage {
        val response = cartApiService.addToCart(productId, customerId, quantity, add.toString())
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    suspend fun removeFromCart(productId: Int, customerId: Int): ResponseMessage {
        val response = cartApiService.removeFromCart(productId, customerId)
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }
}