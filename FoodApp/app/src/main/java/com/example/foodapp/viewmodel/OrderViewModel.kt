package com.example.foodapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.repository.OrderRepository
import com.example.foodapp.model.order.OrderItem
import com.example.foodapp.model.product.ProductItem
import kotlinx.coroutines.launch

class OrderViewModel(private val orderRepository: OrderRepository):ViewModel() {

    private val _getOrderDetailsByCustomerId = MutableLiveData<List<OrderItem>>()
    val getOrderDetailsByCustomerId: LiveData<List<OrderItem>>
        get() = _getOrderDetailsByCustomerId

    private val _getOrderDetailById = MutableLiveData<OrderItem>()
    val getOrderDetailById: LiveData<OrderItem>
        get() = _getOrderDetailById

    fun addToOderFromCart(locationId:Int,customerId:Int,payment:String,note:String="",paymentStatus:String="",onResult:(Boolean,String)->Unit){
        viewModelScope.launch {
            try {
                val response = orderRepository.addToOrderFromCart(locationId,customerId,payment,note,paymentStatus)
                onResult(true,response.message)
            } catch (e: Exception) {
                e.printStackTrace()
                e.message?.let { onResult(false, it) }
            }
        }
    }

    fun updateOrderStatus(id: Int,status:String,onResult:(Boolean,String)->Unit){
        viewModelScope.launch {
            try {
                val response = orderRepository.updateOrderStatus(id,status)
                onResult(true,response.message)
            } catch (e: Exception) {
                e.printStackTrace()
                e.message?.let { onResult(false, it) }
            }
        }
    }

    fun getOrderDetailsByCustomerId(customerId: Int){
        viewModelScope.launch {
            try {
                _getOrderDetailsByCustomerId.value = orderRepository.getOrderDetailsByCustomerId(customerId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun  getOrderDetailById(id:Int){
        viewModelScope.launch {
            try {
                _getOrderDetailById.value = orderRepository.getOrderDetailById(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}