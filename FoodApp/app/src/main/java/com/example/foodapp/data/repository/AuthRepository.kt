package com.example.foodapp.data.repository

import android.util.Log
import com.example.foodapp.data.api.AuthApiService
import com.example.foodapp.model.ResponseMessage
import com.example.foodapp.model.auth.CustomerResponse
import retrofit2.Call

class AuthRepository(private val authApiService: AuthApiService) {
    suspend fun login(emailOrPhone: String, password: String): Call<CustomerResponse> {
        return authApiService.login(emailOrPhone, password)
    }
    suspend fun register(fullName:String,email: String,phoneNumber:String, password: String):Call<ResponseMessage>{
        return authApiService.register(fullName,email,phoneNumber,password)
    }
    suspend fun confirmVerifyCode(emailOrPhone: String, verityCode: String):Call<ResponseMessage>{
        return authApiService.confirmVerifyCode(emailOrPhone, verityCode)
    }
    suspend fun resendCode(email: String):Call<ResponseMessage>{
        return authApiService.resendCode(email);
    }

    suspend fun sendResetEmail(email: String):Call<ResponseMessage>{
        return authApiService.sendResetEmail(email);
    }
    suspend fun resetPassword(email: String,password:String):Call<ResponseMessage>{
        return authApiService.resetPassword(email,password);
    }
    suspend fun updatePassword(id:Int,oldPassword: String,newPassword:String):Call<ResponseMessage>{
        return authApiService.updatePassword(id,oldPassword,newPassword);
    }



    suspend fun confirmVerifyCodeResetPass(emailOrPhone: String, verityCode: String):Call<ResponseMessage>{
        return authApiService.confirmVerifyCodeResetPass(emailOrPhone, verityCode)
    }
    suspend fun resendCodeResetPass(email: String):Call<ResponseMessage>{
        return authApiService.resendCodeResetPass(email);
    }

    suspend fun loginGoogle(fullName:String,email:String,image_url:String):CustomerResponse{
        val response = authApiService.loginGoogle(fullName,email,image_url)
        if (response.isSuccessful) {
            Log.e("TAG", "loginGoogle: "+response.body()!!, )
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }
}