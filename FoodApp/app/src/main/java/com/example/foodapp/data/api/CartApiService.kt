package com.example.foodapp.data.api

import com.example.foodapp.model.ResponseMessage
import com.example.foodapp.model.cart.CartItem
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CartApiService {

    @GET("cart")
    suspend fun getCartByCustomerId(@Query("customer_id") customerId: Int):List<CartItem>


    @FormUrlEncoded
    @POST("cart")
    suspend fun addToCart(
        @Field("product_id") productId: Int,
        @Field("customer_id") customerId: Int,
        @Field("quantity") quantity: Int,
        @Field("add") add: String = "true",
    ): Response<ResponseMessage>

    @DELETE("cart")
    suspend fun removeFromCart(
        @Query("product_id") productId: Int,
        @Query("customer_id") customerId: Int
    ): Response<ResponseMessage>
}