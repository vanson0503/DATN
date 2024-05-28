package com.example.foodapp.data.api

import com.example.foodapp.model.ResponseMessage
import com.example.foodapp.model.auth.CustomerResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApiService {
    @FormUrlEncoded
    @POST("customer/login")
    fun login(
        @Field("email_or_phone") emailOrPhone: String,
        @Field("password") password: String
    ): Call<CustomerResponse>

    @FormUrlEncoded
    @POST("customer/logingoogle")
    suspend fun loginGoogle(
        @Field("full_name") fullName: String,
        @Field("email") email: String,
        @Field("image_url") imageUrl: String
    ): Response<CustomerResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("full_name") fullName:String,
        @Field("email") email: String,
        @Field("phone_number") phoneNumber: String,
        @Field("password") password: String
    ): Call<ResponseMessage>

    @FormUrlEncoded
    @POST("confirmverifycode")
    fun confirmVerifyCode(
        @Field("email_or_phone") emailOrPhone: String,
        @Field("verify_code") verifyCode: String
    ): Call<ResponseMessage>

    @FormUrlEncoded
    @POST("resendcode")
    fun resendCode(
        @Field("email") email: String,
    ): Call<ResponseMessage>

    @FormUrlEncoded
    @POST("send-reset-password-request")
    fun sendResetEmail(
        @Field("email") email: String,
    ): Call<ResponseMessage>

    @FormUrlEncoded
    @POST("resend-reset-password-code")
    fun resendCodeResetPass(
        @Field("email") email: String,
    ): Call<ResponseMessage>

    @FormUrlEncoded
    @POST("confirm-reset-password-code")
    fun confirmVerifyCodeResetPass(
        @Field("email") emailOrPhone: String,
        @Field("verify_code") verifyCode: String
    ): Call<ResponseMessage>

    @FormUrlEncoded
    @POST("reset-password")
    fun resetPassword(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<ResponseMessage>

    @FormUrlEncoded
    @POST("update-password/{id}")
    fun updatePassword(
        @Path("id") id: Int,
        @Field("old_password") oldPassword: String,
        @Field("new_password") newPassword: String,
    ): Call<ResponseMessage>
}