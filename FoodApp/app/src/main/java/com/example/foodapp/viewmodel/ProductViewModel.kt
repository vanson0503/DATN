package com.example.foodapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.repository.ProductRepository
import com.example.foodapp.model.banner.BannerItem
import com.example.foodapp.model.product.ProductItem
import com.example.foodapp.model.product.search.SearchProductItem
import com.example.foodapp.model.product.topsale.ProductSaleItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ProductViewModel(private val productRepository: ProductRepository):ViewModel() {
    private val _getAllProduct = MutableLiveData<List<ProductItem>>()
    val getAllProduct: LiveData<List<ProductItem>>
        get() = _getAllProduct

    private val _searchProducts = MutableLiveData<List<ProductItem>>()
    val searchProducts: LiveData<List<ProductItem>>
        get() = _searchProducts

    private val _getRelatedProducts = MutableLiveData<List<ProductItem>>()
    val getRelatedProducts: LiveData<List<ProductItem>>
        get() = _getRelatedProducts

    private val _getProductsByCategory = MutableLiveData<List<ProductItem>>()
    val getProductsByCategory: LiveData<List<ProductItem>>
        get() = _getProductsByCategory

    private val _getProductById = MutableLiveData<ProductItem>()
    val getProductById: LiveData<ProductItem>
        get() = _getProductById

    private val _getTopSaleProduct = MutableLiveData<List<ProductItem>>()
    val getTopSaleProduct: LiveData<List<ProductItem>>
        get() = _getTopSaleProduct

    private val _getTopRateProduct = MutableLiveData<List<ProductItem>>()
    val getTopRateProduct: LiveData<List<ProductItem>>
        get() = _getTopRateProduct

    private val _getFavoriteProductByCustomerId = MutableLiveData<List<ProductItem>>()
    val getFavoriteProductByCustomerId: LiveData<List<ProductItem>>
        get() = _getFavoriteProductByCustomerId

    private val _getRecommendProductByCustomerId = MutableLiveData<List<ProductItem>>()
    val getRecommendProductByCustomerId: LiveData<List<ProductItem>>
        get() = _getRecommendProductByCustomerId

    private val _getBanner = MutableLiveData<List<BannerItem>>()
    val getBanner: LiveData<List<BannerItem>>
        get() = _getBanner

    private fun getBanner(){
        viewModelScope.launch {
            try {
                _getBanner.value = productRepository.getBanner()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun getAllProducts(){
        viewModelScope.launch {
            try {
                _getAllProduct.value = productRepository.getAllProduct()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getRelatedProducts(id:Int){
        viewModelScope.launch {
            try {
                _getRelatedProducts.value = productRepository.getRelatedProducts(id)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun getProductsByCategory(id:Int){
        viewModelScope.launch {
            try {
                _getProductsByCategory.value = productRepository.getProductsByCategory(id)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun getProductById(id:Int){
        viewModelScope.launch {
            try {
                _getProductById.value = productRepository.getProductById(id)
//                Log.e("CCCC", "getProductById: $id  ${_getProductById.value.toString()}", )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getRecommendProductByCustomerId(id:Int){
        viewModelScope.launch {
            try {
                _getRecommendProductByCustomerId.value = productRepository.getRecommendProductByCustomerId(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun getTopSaleProduct(){
        viewModelScope.launch {
            try {
                _getTopSaleProduct.value = productRepository.getTopSaleProduct()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getTopRateProduct(){
        viewModelScope.launch {
            try {
                _getTopRateProduct.value = productRepository.getTopRateProduct()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun getFavoriteProductByCustomerId(customerId:Int){
        viewModelScope.launch {
            try {
                _getFavoriteProductByCustomerId.value = productRepository.getFavoriteProductByCustomerId(customerId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addToFavorite(productId: Int, customerId: Int,onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = productRepository.addToFavorite(productId, customerId)
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun deleteFavorite(productId: Int, customerId: Int,onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = productRepository.deleteFavorite(productId, customerId)
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun getProductSearch(
        categoryIds: List<Int>? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        keyword: String? = null,
        minRating: Int? = null,
        sortBy: String? = null
    ) {
        viewModelScope.launch{
            try {
                _searchProducts.value = productRepository.getProductSearch(categoryIds, minPrice, maxPrice, keyword, minRating, sortBy)
            } catch (e: HttpException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }



    init {
        getBanner()
        getTopSaleProduct()
        getTopRateProduct()
        getAllProducts()
    }
}