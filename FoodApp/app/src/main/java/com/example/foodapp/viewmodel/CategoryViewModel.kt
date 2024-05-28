package com.example.foodapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.repository.CategoryRepository
import com.example.foodapp.model.category.CategoryItem
import com.example.foodapp.model.customer.Customer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryViewModel(private val categoryRepository: CategoryRepository):ViewModel() {

    private val _getAllCategory = MutableLiveData<List<CategoryItem>>()
    val getAllCategory: LiveData<List<CategoryItem>>
        get() = _getAllCategory

    fun getAllCategory(onResult: (Boolean, List<CategoryItem>?) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = categoryRepository.getAllCategory()
                response.enqueue(object : Callback<List<CategoryItem>> {
                    override fun onResponse(
                        call: Call<List<CategoryItem>>,
                        response: Response<List<CategoryItem>>
                    ) {
                        if (response.isSuccessful) {
                            onResult(true, response.body())
                        } else {
                            onResult(false, null)
                        }
                    }

                    override fun onFailure(call: Call<List<CategoryItem>>, t: Throwable) {
                        onResult(false, null)
                    }

                })
            } catch (e: Exception) {
                onResult(false, null)
            }
        }
    }


    fun getAllCategory2(){
        viewModelScope.launch {
            try {
                _getAllCategory.value = categoryRepository.getAllCategory2()

            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}