package com.example.bagmarket.model.data

data class LoginResponse(
    val expiresAt: Int,
    val success:Boolean,
    val token:String,
    val message:String
)
