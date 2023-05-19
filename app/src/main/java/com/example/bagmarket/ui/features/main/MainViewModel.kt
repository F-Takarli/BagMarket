package com.example.bagmarket.ui.features.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bagmarket.model.data.Ads
import com.example.bagmarket.model.data.CheckOut
import com.example.bagmarket.model.data.Product
import com.example.bagmarket.model.repository.cart.CartRepository
import com.example.bagmarket.model.repository.product.ProductRepository
import com.example.bagmarket.model.repository.product.ProductRepositoryImpl
import com.example.bagmarket.model.repository.user.UserRepository
import com.example.bagmarket.util.coroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    isInternetConnected: Boolean
) : ViewModel() {
    val dataProducts = mutableStateOf<List<Product>>(listOf())
    val dataAds = mutableStateOf<List<Ads>>(listOf())
    val showProgressBar = mutableStateOf(true)
    val badgeNumber = mutableStateOf(0)
    val showPaymentResultDialog = mutableStateOf(false)
    val checkoutData = mutableStateOf(CheckOut(null, null))
    init {
        refreshAllDataFromNet(isInternetConnected)
    }

    private fun refreshAllDataFromNet(isInternetConnected: Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            if (isInternetConnected)
                showProgressBar.value = true
           // delay(5000)

            val newDataProducts=async { productRepository.getAllProducts(isInternetConnected)}
            val newDataAds=async { productRepository.getAllAds(isInternetConnected) }


            updateData(newDataProducts.await(),newDataAds.await())

            showProgressBar.value = false

        }
    }

    private fun updateData(products:List<Product>, ads:List<Ads>) {
        dataProducts.value=products
        dataAds.value=ads
    }
    fun loadBadgeNumber() {
        viewModelScope.launch(coroutineExceptionHandler) {
            badgeNumber.value = cartRepository.getCartSize()
        }

    }

    fun getPaymentStatus(): Int {
        return cartRepository.getPurchaseStatus()
    }

    fun setPaymentStatus(status: Int) {
        cartRepository.setPurchaseStatus(status)
    }
    fun getCheckoutData() {

        viewModelScope.launch(coroutineExceptionHandler) {

            val result = cartRepository.checkOut(cartRepository.getOrderId())
            if (result.success!!) {
                checkoutData.value = result
                showPaymentResultDialog.value = true
            }

        }

    }

}