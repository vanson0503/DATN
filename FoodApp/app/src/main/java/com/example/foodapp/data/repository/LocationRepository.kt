package com.example.foodapp.data.repository

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.foodapp.data.api.LocationApiService
import com.example.foodapp.model.ResponseMessage
import com.example.foodapp.model.location.LocationItem

class LocationRepository(private val locationApiService: LocationApiService) {
    suspend fun getLocationsByCustomerId(customerId:Int):List<LocationItem>{
        return try {
            locationApiService.getLocationsByCustomerId(customerId)
        }
        catch(e:Exception){
            throw e
        }
    }

    suspend fun getLocationById(id:Int):LocationItem{
        return try {
            locationApiService.getLocationById(id)
        }
        catch(e:Exception){
            throw e
        }
    }

    suspend fun addLocation(customerId: Int,name:String,phoneNumber:String,address:String, isDefault:Boolean = false):ResponseMessage{
        val response = locationApiService.addLocation(customerId, name, phoneNumber, address, isDefault)
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            Log.e("TAG", "addLocation: ${customerId} ${name} ${phoneNumber} ${address}", )
            Log.e("TAG", "addLocation failed: HTTP status code: ${response.code()}")
            Log.e("TAG", "addLocation failed: Response message: ${response.message()}")

            response.errorBody()?.string()?.let {
                Log.e("TAG", "addLocation failed: Error body: $it")
            }

            throw Exception("Failed to add location: ${response.message()}")
        }

    }

    suspend fun editLocation(id: Int,customerId: Int,name:String,phoneNumber:String,address:String, isDefault:Boolean = false):ResponseMessage{
        val response = locationApiService.editLocation(id,customerId, name, phoneNumber, address, isDefault)
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            Log.e("TAG", "editLocation:${id} ${customerId} ${name} ${phoneNumber} ${address}", )
            Log.e("TAG", "editLocation failed: HTTP status code: ${response.code()}")
            Log.e("TAG", "editLocation failed: Response message: ${response.message()}")

            response.errorBody()?.string()?.let {
                Log.e("TAG", "editLocation failed: Error body: $it")
            }

            throw Exception("Failed to add location: ${response.message()}")
        }

    }

    suspend fun deleteLocation(id:Int):ResponseMessage{
        val response = locationApiService.deleteLocation(id)
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            Log.e("TAG", "deleteLocation:${id} ", )
            Log.e("TAG", "deleteLocation failed: HTTP status code: ${response.code()}")
            Log.e("TAG", "deleteLocation failed: Response message: ${response.message()}")

            response.errorBody()?.string()?.let {
                Log.e("TAG", "deleteLocation failed: Error body: $it")
            }

            throw Exception("Failed to del location: ${response.message()}")
        }
    }
}