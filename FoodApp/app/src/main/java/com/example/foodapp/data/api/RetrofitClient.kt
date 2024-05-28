package com.example.foodapp.data.api

import com.example.foodapp.utils.BASE_API_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val productApiService:ProductApiService by lazy {
        retrofit.create(ProductApiService::class.java)
    }

    val categoryApiService:CategoryApiService by lazy {
        retrofit.create(CategoryApiService::class.java)
    }
    val customerApiService:CustomerApiService by lazy {
        retrofit.create(CustomerApiService::class.java)
    }
    val reviewApiService:ReviewApiService by lazy {
        retrofit.create(ReviewApiService::class.java)
    }
    val cartApiService:CartApiService by lazy{
        retrofit.create(CartApiService::class.java)
    }
    val locationApiService:LocationApiService by lazy {
        retrofit.create(LocationApiService::class.java)
    }
    val orderApiService:OrderApiService by lazy {
        retrofit.create(OrderApiService::class.java)
    }
}