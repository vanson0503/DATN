package com.example.foodapp.utils

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.text.NumberFormat
import java.util.Locale

//val BASE_API_URL = "https://vanson.ddns.us/food-api/public/api/"
//val BASE_API_URL = "https://foodapipro.000webhostapp.com/food-api/public/api/"
val BASE_API_URL = "https://vanson.io.vn/food-api/public/api/"
//val BASE_API_URL = "https://57ee-171-228-155-107.ngrok-free.app/food-api/public/api/"
val BASE_IMAGE_PRODUCT_URL = "https://vanson.io.vn/food-api/storage/app/public/product_images/"
//val BASE_IMAGE_PRODUCT_URL =  "https://57ee-171-228-155-107.ngrok-free.app/food-api/storage/app/public/product_images/"
val BASE_IMAGE_CATEGORY_URL = "https://vanson.io.vn/food-api/storage/app/public/category_images/"
//val BASE_IMAGE_CATEGORY_URL = "https://57ee-171-228-155-107.ngrok-free.app/food-api/storage/app/public/category_images/"
val BASE_IMAGE_AVATAR_URL = "https://vanson.io.vn/food-api/storage/app/public/avatars/"
//val BASE_IMAGE_AVATAR_URL = "https://57ee-171-228-155-107.ngrok-free.app/food-api/storage/app/public/avatars/"
val BASE_IMAGE_BANNER_URL = "https://vanson.io.vn/food-api/storage/app/public/banner_images/"



fun isValidPhoneNumber(input: String): Boolean {
    val phoneRegex = Regex("^(\\+|0)[0-9]{9,11}\$")
    return phoneRegex.matches(input)
}

fun isValidEmail(input: String): Boolean {
    val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,})+\$")

    return emailRegex.matches(input)
}

fun formatVND(value: Int): String {
    val vietnam = Locale("vi", "VN")
    val format: NumberFormat = NumberFormat.getCurrencyInstance(vietnam)
    return format.format(value)
}

fun formatTotalSold(totalSold: Int): String {
    return if (totalSold >= 1000) {
        String.format("%.1fk", totalSold / 1000.0)
    } else {
        totalSold.toString()
    }
}

fun formatAvgRating(avgRating: Float): String {
    return String.format("%.1f/5", avgRating)
}


// Data classes for Provinces, Districts, and Wards (không thay đổi)
data class Ward(
    val ward_id: String,
    val ward_name: String,
    val ward_type: String,
    val ward_slug: String
)

data class District(
    val district_id: String,
    val district_name: String,
    val district_type: String,
    val district_slug: String,
    val wards: List<Ward>
)

data class Province(
    val province_id: String,
    val province_name: String,
    val province_type: String,
    val province_slug: String,
    val districts: List<District>
)

fun loadProvinces(context: Context): List<Province> {
    val gson = Gson()
    val jsonString = try {
        context.assets.open("sorted.json").bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return emptyList()
    }

    val provinces = mutableListOf<Province>()

    try {
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val provinceArray = jsonArray.getJSONArray(i)
            val province = parseProvince(provinceArray, gson)
            provinces.add(province)
        }
    } catch (jsonException: JSONException) {
        jsonException.printStackTrace()
    }

    return provinces
}

private fun parseProvince(provinceArray: JSONArray, gson: Gson): Province {
    val provinceId = provinceArray.getString(0)
    val provinceName = provinceArray.getString(1)
    val provinceType = provinceArray.getString(2)
    val provinceSlug = provinceArray.getString(3)

    val districts = mutableListOf<District>()

    val districtsArray = provinceArray.getJSONArray(4)
    for (j in 0 until districtsArray.length()) {
        val districtArray = districtsArray.getJSONArray(j)
        val district = parseDistrict(districtArray, gson)
        districts.add(district)
    }

    return Province(provinceId, provinceName, provinceType, provinceSlug, districts)
}

private fun parseDistrict(districtArray: JSONArray, gson: Gson): District {
    val districtId = districtArray.getString(0)
    val districtName = districtArray.getString(1)
    val districtType = districtArray.getString(2)
    val districtSlug = districtArray.getString(3)

    val wards = mutableListOf<Ward>()

    val wardsArray = districtArray.getJSONArray(4)
    for (k in 0 until wardsArray.length()) {
        val wardArray = wardsArray.getJSONArray(k)
        val ward = parseWard(wardArray)
        wards.add(ward)
    }

    return District(districtId, districtName, districtType, districtSlug, wards)
}

private fun parseWard(wardArray: JSONArray): Ward {
    val wardId = wardArray.getString(0)
    val wardName = wardArray.getString(1)
    val wardType = wardArray.getString(2)
    val wardSlug = wardArray.getString(3)

    return Ward(wardId, wardName, wardType, wardSlug)
}