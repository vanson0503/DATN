package com.example.foodapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.repository.CartRepository
import com.example.foodapp.model.cart.CartItem
import kotlinx.coroutines.launch

class CartViewModel(private val cartRepository: CartRepository):ViewModel() {
    private val _getCartByCustomerId = MutableLiveData<List<CartItem>>()
    val getCartByCustomerId: LiveData<List<CartItem>>
        get() = _getCartByCustomerId



    fun getCartByCustomerId(customerId:Int){
        viewModelScope.launch {
            try {
                _getCartByCustomerId.value = cartRepository.getCartByCustomerId(customerId)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }



    fun removeItem(item: CartItem) {
        val currentItems = _getCartByCustomerId.value?.toMutableList() ?: return
        currentItems.remove(item)
        _getCartByCustomerId.value = currentItems
        Log.e("REM", "removeItem: ${_getCartByCustomerId.value.toString()}", )
    }

    fun updateCartItemQuantity(productId: Int, customerId: Int, quantity: Int, add:Boolean=true,onResult:(Boolean)->Unit) {
        viewModelScope.launch {
            try {
                val response = cartRepository.addToCart(productId, customerId, quantity,add)
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun removeFromCart(productId: Int, customerId: Int,onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = cartRepository.removeFromCart(productId, customerId)
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }



}