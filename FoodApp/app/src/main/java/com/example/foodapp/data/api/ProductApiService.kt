package com.example.foodapp.data.api

import com.example.foodapp.model.ResponseMessage
import com.example.foodapp.model.banner.BannerItem
import com.example.foodapp.model.product.ProductItem
import com.example.foodapp.model.product.search.SearchProductItem
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {
    @GET("products")
    suspend fun getAllProducts():List<ProductItem>

    @GET("product/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductItem

    @GET("products/topsale")
    suspend fun getTopSaleProduct():List<ProductItem>

    @GET("products/filter")
    suspend fun getProductSearch(
        @Query("category_ids[]") categoryIds: List<Int>? = null,
        @Query("min_price") minPrice: Int? = null,
        @Query("max_price") maxPrice: Int? = null,
        @Query("keyword") keyword: String? = null,
        @Query("min_rating") minRating: Int? = null,
        @Query("sort_by") sortBy: String? = null
    ): Response<List<ProductItem>>

    @GET("products/related/{id}")
    suspend fun getRelatedProducts(@Path("id") id: Int):Response<List<ProductItem>>

    @GET("products/category/{id}")
    suspend fun getProductsByCategory(@Path("id") id: Int):Response<List<ProductItem>>

    @GET("products/toprate")
    suspend fun getTopRateProduct():List<ProductItem>

    @GET("favorite")
    suspend fun getFavoriteProductByCustomerId(@Query("customer_id") customerId: Int): List<ProductItem>

    @GET("recommendations")
    suspend fun getRecommendProductByCustomerId(@Query("id") id: Int): List<ProductItem>

    @FormUrlEncoded
    @POST("favorite")
    suspend fun addToFavorite(
        @Field("product_id") productId: Int,
        @Field("customer_id") customerId: Int,
    ): Response<ResponseMessage>

    @FormUrlEncoded
    @POST("favoritedel")
    suspend fun deleteFavorite(
        @Field("product_id") productId: Int,
        @Field("customer_id") customerId: Int,
    ): Response<ResponseMessage>

    @GET("banner")
    suspend fun getBanner():Response<List<BannerItem>>
}