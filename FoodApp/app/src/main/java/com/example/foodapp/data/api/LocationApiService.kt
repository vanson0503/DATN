package com.example.foodapp.data.api

import com.example.foodapp.model.ResponseMessage
import com.example.foodapp.model.location.LocationItem
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LocationApiService {
    @GET("locations")
    suspend fun getLocationsByCustomerId(
        @Query("customer_id") customerId: Int
    ):List<LocationItem>

    @GET("locations")
    suspend fun getLocationById(
        @Query("id") id: Int
    ):LocationItem

    @FormUrlEncoded
    @POST("addlocation")
    suspend fun addLocation(
        @Field("customer_id") customerId: Int,
        @Field("name") name: String,
        @Field("phone_number") phoneNumber: String,
        @Field("address") address: String,
        @Field("is_default") isDefault: Boolean = false,
    ): Response<ResponseMessage>

    @FormUrlEncoded
    @POST("editlocation/{id}")
    suspend fun editLocation(
        @Path("id") id: Int,
        @Field("customer_id") customerId: Int,
        @Field("name") name: String,
        @Field("phone_number") phoneNumber: String,
        @Field("address") address: String,
        @Field("is_default") isDefault: Boolean = false,
    ): Response<ResponseMessage>

    @DELETE("location/{id}")
    suspend fun deleteLocation( @Path("id") id: Int): Response<ResponseMessage>

}