package com.example.foodapp.data.api

import com.example.foodapp.model.ResponseMessage
import com.example.foodapp.model.review.ReviewItem
import com.example.foodapp.viewmodel.MessageItem
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewApiService {
    @GET("reviews/{id}")
    suspend fun getReviewsByProductId(@Path("id") id: Int):List<ReviewItem>

    @GET("reviews")
    suspend fun checkReview(
        @Query("orders_id") orderId:Int,
        @Query("customer_id") customerId:Int,
        @Query("product_id") productId:Int,
    ):Response<ReviewItem>


    @FormUrlEncoded
    @POST("reviews")
    suspend fun addReview(
        @Field("orders_id") ordersId: Int,
        @Field("customer_id") customerId: Int,
        @Field("product_id") productId: Int,
        @Field("rate") rate: Int,
        @Field("content") content: String?=null,
    ):Response<ResponseMessage>

}