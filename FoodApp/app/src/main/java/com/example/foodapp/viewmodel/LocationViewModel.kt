package com.example.foodapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.repository.LocationRepository
import com.example.foodapp.model.cart.CartItem
import com.example.foodapp.model.location.LocationItem
import com.example.foodapp.model.product.ProductItem
import kotlinx.coroutines.launch

class LocationViewModel(private val locationRepository: LocationRepository):ViewModel() {
    private val _getLocationsByCustomerId = MutableLiveData<List<LocationItem>>()
    val getLocationsByCustomerId: LiveData<List<LocationItem>>
        get() = _getLocationsByCustomerId

    private val _getLocationById = MutableLiveData<LocationItem>()
    val getLocationById: LiveData<LocationItem>
        get() = _getLocationById


    fun getLocationsByCustomerId(customerId:Int){
        viewModelScope.launch {
            try {
                _getLocationsByCustomerId.value = locationRepository.getLocationsByCustomerId(customerId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getLocationById(id:Int){
        viewModelScope.launch {
            try {
                _getLocationById.value = locationRepository.getLocationById(id)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun addLocation(customerId: Int,name:String,phoneNumber:String,address:String, isDefault:Boolean = false,onResult: (Boolean) -> Unit){
        viewModelScope.launch {
            try {
                val response = locationRepository.addLocation(
                    customerId,
                    name,
                    phoneNumber,
                    address,
                    isDefault
                )
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun editLocation(id:Int,customerId: Int,name:String,phoneNumber:String,address:String, isDefault:Boolean = false,onResult: (Boolean) -> Unit){
        viewModelScope.launch {
            try {
                val response = locationRepository.editLocation(
                    id,
                    customerId,
                    name,
                    phoneNumber,
                    address,
                    isDefault
                )
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun deleteLocation(id:Int,onResult: (Boolean) -> Unit){
        viewModelScope.launch {
            try {
                val response = locationRepository.deleteLocation(id)
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }
}