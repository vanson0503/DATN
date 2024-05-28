package com.example.foodapp.data.api

import com.example.foodapp.model.category.CategoryItem
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface CategoryApiService {
    @GET("category")
    fun getAllCategory():Call<List<CategoryItem>>

    @GET("category")
    suspend fun getAllCategory2():List<CategoryItem>
}