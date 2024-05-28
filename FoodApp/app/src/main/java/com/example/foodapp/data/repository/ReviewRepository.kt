package com.example.foodapp.data.repository

import com.example.foodapp.data.api.ReviewApiService
import com.example.foodapp.model.ResponseMessage
import com.example.foodapp.model.review.ReviewItem

class ReviewRepository(private val reviewApiService: ReviewApiService) {
    suspend fun getReviewsByProductId(id:Int):List<ReviewItem>{
        return reviewApiService.getReviewsByProductId(id)
    }

    suspend fun checkReview(ordersId:Int,customerId:Int,productId:Int):ReviewItem{
        val response = reviewApiService.checkReview(ordersId,customerId,productId)
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    suspend fun addReview(ordersId:Int,customerId:Int,productId:Int,rate:Int,content:String?):ResponseMessage
    {
        val response = reviewApiService.addReview(ordersId,customerId,productId,rate,content)
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }
}