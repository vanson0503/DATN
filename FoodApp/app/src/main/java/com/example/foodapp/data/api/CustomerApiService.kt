package com.example.foodapp.data.api

import com.example.foodapp.model.ResponseMessage
import com.example.foodapp.model.customer.Customer
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface CustomerApiService {
    @GET("customer/{id}")
    suspend fun getCustomerById(@Path("id") id: Int): Response<Customer>

    @Multipart
    @POST("customer/updateavatar/{id}")
    suspend fun updateCustomerAvatar(
        @Part image: MultipartBody.Part? = null,
        @Path("id") id: Int
    ): Response<ResponseMessage>

    @FormUrlEncoded
    @POST("customer/update/{id}")
    suspend fun updateCustomer(
        @Path("id") id: Int,
        @Field("full_name") fullName:String,
        @Field("phone_number") phoneNumber:String,
        @Field("email") email:String,
    ): Response<ResponseMessage>
}