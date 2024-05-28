package com.example.foodapp.data.api

import com.example.foodapp.model.ResponseMessage
import com.example.foodapp.model.order.OrderItem
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderApiService {

    @FormUrlEncoded
    @POST("order")
    suspend fun addToOderFromCart(
        @Field("location_id") locationId: Int,
        @Field("customer_id") customerId: Int,
        @Field("payment") payment: String,
        @Field("note") note: String="",
        @Field("payment_status") paymentStatus: String="",
    ): Response<ResponseMessage>

    @GET("orderdetails/{customerId}")
    suspend fun getOrderDetailsByCustomerId(@Path("customerId") customerId: Int):Response< List<OrderItem>>

    @GET("orderdetail/{id}")
    suspend fun getOrderDetailById(@Path("id") id: Int):Response< OrderItem>

    @FormUrlEncoded
    @POST("updateorderstatus")
    suspend fun updateOrderStatus(
        @Field("id") id: Int,
        @Field("status") status:String,
    ):Response< ResponseMessage>
}