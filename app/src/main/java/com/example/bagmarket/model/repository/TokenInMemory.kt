package com.example.bagmarket.model.repository

object TokenInMemory {
    var userName: String? = null
        private set

    var token: String? = null
        private set

    fun refreshToken(userName: String?, newToken: String?) {
        TokenInMemory.userName = userName
        token = newToken
    }
}