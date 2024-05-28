package com.example.foodapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.repository.ReviewRepository
import com.example.foodapp.model.product.ProductItem
import com.example.foodapp.model.review.ReviewItem
import kotlinx.coroutines.launch

class ReviewViewModel(private val reviewRepository: ReviewRepository):ViewModel() {
    private val _getReviewsByProductId = MutableLiveData<List<ReviewItem>>()
    val getReviewsByProductId: LiveData<List<ReviewItem>>
        get() = _getReviewsByProductId

    fun getReviewByProductId(id:Int){
        viewModelScope.launch {
            try {
                _getReviewsByProductId.value = reviewRepository.getReviewsByProductId(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun checkReview(ordersId:Int,customerId:Int,productId:Int,onResult: (Boolean,ReviewItem?) -> Unit){
        viewModelScope.launch {
            try {
                val data = reviewRepository.checkReview(ordersId, customerId, productId)
                onResult(true,data)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false,null)
            }
        }
    }

    fun addReview(ordersId:Int,customerId:Int,productId:Int,rate:Int,content:String?,onResult: (Boolean,String) -> Unit){
        viewModelScope.launch {
            try {
                val data = reviewRepository.addReview(ordersId, customerId, productId,rate,content)
                onResult(true,data.message)
            } catch (e: Exception) {
                e.printStackTrace()
                e.message?.let { onResult(false, it) }
            }
        }
    }

}