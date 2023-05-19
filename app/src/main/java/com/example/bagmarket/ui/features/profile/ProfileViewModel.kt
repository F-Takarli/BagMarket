package com.example.bagmarket.ui.features.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bagmarket.model.data.Ads
import com.example.bagmarket.model.data.Product
import com.example.bagmarket.model.repository.product.ProductRepository
import com.example.bagmarket.model.repository.product.ProductRepositoryImpl
import com.example.bagmarket.model.repository.user.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    val email = mutableStateOf("")
    val address = mutableStateOf("")
    val postalCode = mutableStateOf("")
    val loginTime = mutableStateOf("")

    val showLocationDialog = mutableStateOf(false)

    fun loadUserData() {

        email.value = userRepository.getUsername()!!
        loginTime.value = userRepository.getUserLoginTime()

        val location = userRepository.getUserLocation()
        address.value = location.first
        postalCode.value = location.second
    }

    fun signOut() {
        userRepository.signOut()
    }

    fun setUserLocation(address: String, postalCode: String) {
        userRepository.saveUserLocation(address, postalCode)
    }



}