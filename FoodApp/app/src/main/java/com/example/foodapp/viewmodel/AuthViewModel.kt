package com.example.foodapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.repository.AuthRepository
import com.example.foodapp.model.ResponseMessage
import com.example.foodapp.model.auth.CustomerItem
import com.example.foodapp.model.auth.CustomerResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    fun login(emailOrPhone: String, password: String, onResult: (Boolean, String?,CustomerItem?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = authRepository.login(emailOrPhone, password)
                response.enqueue(object : Callback<CustomerResponse> {
                    override fun onResponse(call: Call<CustomerResponse>, response: Response<CustomerResponse>) {
                        if (response.isSuccessful) {
                            val customerResponse = response.body()
                            if (customerResponse != null) {
                                onResult(true, null,customerResponse.customer)
                            } else {
                                onResult(false, "Invalid response body",null)
                            }
                        } else {
                            val errorMessage = "Error code: ${response.code()}"
                            onResult(false, errorMessage,null)
                        }
                    }
                    override fun onFailure(call: Call<CustomerResponse>, t: Throwable) {
                        onResult(false, t.message,null)
                    }
                })
            } catch (e: Exception) {
                onResult(false, e.message,null)
            }
        }
    }

    fun register(fullName:String,email: String,phoneNumber:String, password: String, onResult: (Boolean,message:String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = authRepository.register(fullName,email,phoneNumber,password)
                response.enqueue(object : Callback<ResponseMessage>{
                    override fun onResponse(
                        call: Call<ResponseMessage>,
                        response: Response<ResponseMessage>
                    ) {
                        if (!response.isSuccessful) {
                            val errorMessage = response.errorBody()?.string()
                            val jsonObject = errorMessage?.let { JSONObject(it) }
                            val message = jsonObject?.getString("message")
                            if (message != null) {
                                onResult(false, message)
                            }else{
                                onResult(false, errorMessage ?: "Unknown error")
                            }

                        } else {
                            val res = response.body()
                            if (res != null) {
                                onResult(true, res.message)
                            }
                        }
                    }
                    override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                        t.message?.let { onResult(false, it) }
                    }

                })
            }
            catch (e:Exception){
                onResult(false,e.message!!)
            }
        }
    }

    fun confirmVerifyCode(emailOrPhone: String, verityCode: String, onResult: (Boolean,message:String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = authRepository.confirmVerifyCode(emailOrPhone,verityCode)
                response.enqueue(object : Callback<ResponseMessage>{
                    override fun onResponse(
                        call: Call<ResponseMessage>,
                        response: Response<ResponseMessage>
                    ) {
                        if (!response.isSuccessful) {
                            val errorMessage = response.errorBody()?.string()
                            val jsonObject = errorMessage?.let { JSONObject(it) }
                            val message = jsonObject?.getString("message")
                            if (message != null) {
                                onResult(false, message)
                            }else{
                                onResult(false, errorMessage ?: "Unknown error")
                            }

                        } else {
                            val res = response.body()
                            if (res != null) {
                                onResult(true, res.message)
                            }
                        }
                    }
                    override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                        t.message?.let { onResult(false, it) }
                    }

                })
            }
            catch (e:Exception){
                onResult(false,e.message!!)
            }
        }
    }
    fun resendCode(email:String,onResult: (Boolean,message:String) -> Unit){
        viewModelScope.launch {
            try {
                val response = authRepository.resendCode(email)
                response.enqueue(object : Callback<ResponseMessage>{
                    override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                        if (!response.isSuccessful) {
                            val errorMessage = response.errorBody()?.string()
                            val jsonObject = errorMessage?.let { JSONObject(it) }
                            val message = jsonObject?.getString("message")
                            if (message != null) {
                                onResult(false, message)
                            }else{
                                onResult(false, errorMessage ?: "Unknown error")
                            }

                        } else {
                            val res = response.body()
                            if (res != null) {
                                onResult(true, res.message)
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                        t.message?.let { onResult(false, it) }
                    }

                })
            }
            catch (e:Exception){
                onResult(false,e.message!!)
            }
        }
    }

    fun sendResetEmail(email:String,onResult: (Boolean,message:String) -> Unit){
        viewModelScope.launch {
            try {
                val response = authRepository.sendResetEmail(email)
                response.enqueue(object : Callback<ResponseMessage>{
                    override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                        if (!response.isSuccessful) {
                            val errorMessage = response.errorBody()?.string()
                            val jsonObject = errorMessage?.let { JSONObject(it) }
                            val message = jsonObject?.getString("message")
                            if (message != null) {
                                onResult(false, message)
                            }else{
                                onResult(false, errorMessage ?: "Unknown error")
                            }

                        } else {
                            val res = response.body()
                            if (res != null) {
                                onResult(true, res.message)
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                        t.message?.let { onResult(false, it) }
                    }

                })
            }
            catch (e:Exception){
                onResult(false,e.message!!)
            }
        }
    }

    fun resetPassword(email:String,password: String,onResult: (Boolean,message:String) -> Unit){
        viewModelScope.launch {
            try {
                val response = authRepository.resetPassword(email,password)
                response.enqueue(object : Callback<ResponseMessage>{
                    override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                        if (!response.isSuccessful) {
                            val errorMessage = response.errorBody()?.string()
                            val jsonObject = errorMessage?.let { JSONObject(it) }
                            val message = jsonObject?.getString("message")
                            if (message != null) {
                                onResult(false, message)
                            }else{
                                onResult(false, errorMessage ?: "Unknown error")
                            }

                        } else {
                            val res = response.body()
                            if (res != null) {
                                onResult(true, res.message)
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                        t.message?.let { onResult(false, it) }
                    }

                })
            }
            catch (e:Exception){
                onResult(false,e.message!!)
            }
        }
    }

    fun updatePassword(id:Int,oldPassword:String,newPassword: String,onResult: (Boolean,message:String) -> Unit){
        viewModelScope.launch {
            try {
                val response = authRepository.updatePassword(id,oldPassword,newPassword)
                response.enqueue(object : Callback<ResponseMessage>{
                    override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                        if (!response.isSuccessful) {
                            val errorMessage = response.errorBody()?.string()
                            val jsonObject = errorMessage?.let { JSONObject(it) }
                            val message = jsonObject?.getString("message")
                            if (message != null) {
                                onResult(false, message)
                            }else{
                                onResult(false, errorMessage ?: "Unknown error")
                            }

                        } else {
                            val res = response.body()
                            if (res != null) {
                                onResult(true, res.message)
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                        t.message?.let { onResult(false, it) }
                    }

                })
            }
            catch (e:Exception){
                onResult(false,e.message!!)
            }
        }
    }

    fun confirmVerifyCodeResetPass(emailOrPhone: String, verityCode: String, onResult: (Boolean,message:String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = authRepository.confirmVerifyCodeResetPass(emailOrPhone,verityCode)
                response.enqueue(object : Callback<ResponseMessage>{
                    override fun onResponse(
                        call: Call<ResponseMessage>,
                        response: Response<ResponseMessage>
                    ) {
                        if (!response.isSuccessful) {
                            val errorMessage = response.errorBody()?.string()
                            val jsonObject = errorMessage?.let { JSONObject(it) }
                            val message = jsonObject?.getString("message")
                            if (message != null) {
                                onResult(false, message)
                            }else{
                                onResult(false, errorMessage ?: "Unknown error")
                            }

                        } else {
                            val res = response.body()
                            if (res != null) {
                                onResult(true, res.message)
                            }
                        }
                    }
                    override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                        t.message?.let { onResult(false, it) }
                    }

                })
            }
            catch (e:Exception){
                onResult(false,e.message!!)
            }
        }
    }
    fun resendCodeResetPass(email:String,onResult: (Boolean,message:String) -> Unit){
        viewModelScope.launch {
            try {
                val response = authRepository.resendCodeResetPass(email)
                response.enqueue(object : Callback<ResponseMessage>{
                    override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                        if (!response.isSuccessful) {
                            val errorMessage = response.errorBody()?.string()
                            val jsonObject = errorMessage?.let { JSONObject(it) }
                            val message = jsonObject?.getString("message")
                            if (message != null) {
                                onResult(false, message)
                            }else{
                                onResult(false, errorMessage ?: "Unknown error")
                            }

                        } else {
                            val res = response.body()
                            if (res != null) {
                                onResult(true, res.message)
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                        t.message?.let { onResult(false, it) }
                    }

                })
            }
            catch (e:Exception){
                onResult(false,e.message!!)
            }
        }
    }

    fun loginGoogle(fullName:String,email:String,image_url:String,onResult:(Boolean,String,CustomerItem?)->Unit){
        viewModelScope.launch {
            try {
                val data = authRepository.loginGoogle(fullName,email,image_url)
                data.customer?.let { onResult(true,data.message, it) }
            }catch (e: Exception) {
                val data = authRepository.loginGoogle(fullName,email,image_url)
                onResult(false,data.message,null)
                e.printStackTrace()
            }
        }
    }
}