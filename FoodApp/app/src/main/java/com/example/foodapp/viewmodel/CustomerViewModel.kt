package com.example.foodapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.repository.CustomerRepository
import com.example.foodapp.model.customer.Customer
import com.example.foodapp.model.order.OrderItem
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CustomerViewModel(private val customerRepository: CustomerRepository):ViewModel() {
    private val _getCustomerById = MutableLiveData<Customer>()
    val getCustomerById: LiveData<Customer>
        get() = _getCustomerById

    fun getCustomerById(id:Int,){
        viewModelScope.launch {
            try {
                _getCustomerById.value = customerRepository.getCustomerById(id)

            }
            catch (e: Exception) {
                Log.e("DATA", "getOrderDetailsByCustomerId: ${_getCustomerById.value.toString()}", )
                e.printStackTrace()
            }
        }
    }

    fun updateCustomer(
        id: Int,
        fullName: String,
        phoneNumber: String,
        email: String,
        onResult:(Boolean)->Unit
    ) {
        viewModelScope.launch {
            try {
                customerRepository.updateCustomer(id, fullName, phoneNumber, email)
                onResult(true)
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Error updating customer", e)
                onResult(false)
            }
        }
    }

    fun updateCustomerAvatar(
        id: Int,
        image:File,
    ){
        viewModelScope.launch {
            try {
                val requestBody = image.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image",image.name,requestBody)
                customerRepository.updateCustomerAvatar(id, imagePart)

            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Error updating customer", e)
            }
        }
    }
}