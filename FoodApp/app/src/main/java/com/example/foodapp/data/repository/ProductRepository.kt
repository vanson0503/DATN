package com.example.foodapp.data.repository

import android.util.Log
import com.example.foodapp.data.api.ProductApiService
import com.example.foodapp.model.ResponseMessage
import com.example.foodapp.model.banner.BannerItem
import com.example.foodapp.model.product.ProductItem
import com.example.foodapp.model.product.search.SearchProductItem
import com.example.foodapp.model.product.topsale.ProductSaleItem
import retrofit2.Call

class ProductRepository(private val productApiService: ProductApiService) {





    suspend fun getAllProduct():List<ProductItem>{
        return try {
            productApiService.getAllProducts()
        }
        catch(e:Exception){
            throw e
        }
    }

    suspend fun getProductById(id:Int):ProductItem{
        return try {
            productApiService.getProductById(id)
        }
        catch(e:Exception){
            throw e
        }
    }

    suspend fun getRecommendProductByCustomerId(id:Int):List<ProductItem>{
        return try {
            productApiService.getRecommendProductByCustomerId(id)
        }
        catch(e:Exception){
            throw e
        }
    }

    suspend fun getProductSearch(
        categoryIds: List<Int>? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        keyword: String? = null,
        minRating: Int? = null,
        sortBy: String? = null
    ): List<ProductItem> {
        val response = productApiService.getProductSearch(categoryIds, minPrice, maxPrice, keyword, minRating, sortBy)
        Log.e("TAG", "getProductSearch: ${response.raw()} ${categoryIds.toString()}", )
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    suspend fun getRelatedProducts(id:Int):List<ProductItem>{
        val response = productApiService.getRelatedProducts(id)
        if(response.isSuccessful&&response.body()!=null){
            return response.body()!!
        }
        else{
            throw Exception(response.message())
        }
    }

    suspend fun getProductsByCategory(id:Int):List<ProductItem>{
        val response = productApiService.getProductsByCategory(id)
        if(response.isSuccessful&&response.body()!=null){
            return response.body()!!
        }
        else{
            throw Exception(response.message())
        }
    }

    suspend fun getTopSaleProduct():List<ProductItem>{
        return try {
            productApiService.getTopSaleProduct()
        }
        catch(e:Exception){
            throw e
        }
    }

    suspend fun getTopRateProduct():List<ProductItem>{
        return try {
            productApiService.getTopRateProduct()
        }
        catch(e:Exception){
            throw e
        }
    }

    suspend fun getFavoriteProductByCustomerId(customerId:Int):List<ProductItem>{
        return try {
            productApiService.getFavoriteProductByCustomerId(customerId)
        }
        catch(e:Exception){
            throw e
        }
    }

    suspend fun addToFavorite(productId:Int,customerId: Int):ResponseMessage{
        val response = productApiService.addToFavorite(productId,customerId)
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    suspend fun deleteFavorite(productId:Int,customerId: Int):ResponseMessage {
        val response = productApiService.deleteFavorite(productId,customerId)
        if (response.isSuccessful || response.body() != null) {
            return response.body()!!
        } else {
            Log.e("TAG", "deleteFavorite: ${response.code().toString()} $productId $customerId", )
            throw Exception(response.message())
        }
    }


    suspend fun getBanner():List<BannerItem>{
        val response = productApiService.getBanner()
        if(response.isSuccessful||response.body()!=null){
            return response.body()!!
        }
        else{
            throw Exception(response.message())
        }
    }
}