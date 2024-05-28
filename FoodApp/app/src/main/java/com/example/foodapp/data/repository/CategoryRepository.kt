package com.example.foodapp.data.repository

import com.example.foodapp.data.api.CategoryApiService
import com.example.foodapp.model.category.CategoryItem
import retrofit2.Call

class CategoryRepository(private val categoryApiService: CategoryApiService) {
    suspend fun getAllCategory():Call<List<CategoryItem>>{
        return categoryApiService.getAllCategory()
    }
    suspend fun getAllCategory2():List<CategoryItem>{
        return categoryApiService.getAllCategory2()
    }

}