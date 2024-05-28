package com.example.foodapp.data.repository

import android.util.Log
import com.example.foodapp.data.api.CustomerApiService
import com.example.foodapp.model.customer.Customer
import okhttp3.MultipartBody

class CustomerRepository(private val customerApiService: CustomerApiService) {
    suspend fun getCustomerById(id:Int): Customer {
        val response = customerApiService.getCustomerById(id)
        if (response.isSuccessful) {
            Log.e("TAG", "getCustomerById: "+response.body()!!, )
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    suspend fun updateCustomer(
        id: Int,
        fullName: String,
        phoneNumber: String,
        email: String
    ) {
        val response = customerApiService.updateCustomer( id, fullName, phoneNumber, email)

        if (!response.isSuccessful) {
            throw Exception(response.message())
        }
    }

    suspend fun updateCustomerAvatar(
        id: Int,
        image: MultipartBody.Part,
    ) {
        val response = customerApiService.updateCustomerAvatar( image,id)
        if (!response.isSuccessful) {
            throw Exception(response.message())
        }
    }
}