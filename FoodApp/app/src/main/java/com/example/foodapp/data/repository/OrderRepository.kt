package com.example.foodapp.data.repository

import android.util.Log
import com.example.foodapp.data.api.OrderApiService
import com.example.foodapp.model.ResponseMessage
import com.example.foodapp.model.order.OrderItem
import org.json.JSONObject
import kotlin.math.log

class OrderRepository(private val orderApiService: OrderApiService) {
    suspend fun addToOrderFromCart(
        locationId: Int,
        customerId: Int,
        payment: String,
        note: String = "",
        paymentStatus: String = ""
    ): ResponseMessage {
        val response = orderApiService.addToOderFromCart(locationId, customerId, payment, note, paymentStatus)
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception(parseErrorMessage(errorBody, response.message()))
        }
    }

    suspend fun updateOrderStatus(id:Int,status:String):ResponseMessage{
        val response = orderApiService.updateOrderStatus(id,status)
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception(parseErrorMessage(errorBody, response.message()))
        }
    }

    private fun parseErrorMessage(errorBody: String?, defaultMessage: String): String {
        return if (errorBody != null) {
            try {
                val jsonObject = JSONObject(errorBody)
                jsonObject.optString("message", defaultMessage)
            } catch (e: Exception) {
                defaultMessage
            }
        } else {
            defaultMessage
        }
    }
    suspend fun getOrderDetailsByCustomerId(customerId: Int):List<OrderItem>{
        val response = orderApiService.getOrderDetailsByCustomerId(customerId)
        if (response.isSuccessful && response.body() != null) {
            return if(response.body()!=null)
                response.body()!!
            else emptyList<OrderItem>()
        } else {
            throw Exception(response.message())
        }
    }

    suspend fun getOrderDetailById(customerId: Int):OrderItem{
        val response = orderApiService.getOrderDetailById(customerId)
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }
}