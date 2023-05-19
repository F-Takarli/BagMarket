package com.example.bagmarket.model.net

import com.example.bagmarket.model.data.LoginResponse
import com.example.bagmarket.model.repository.TokenInMemory
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthChecker : Authenticator, KoinComponent {
    private val apiService: ApiService by inject()
    override fun authenticate(route: Route?, response: Response): Request? {
        if (TokenInMemory.token != null &&
               !response.request.url.pathSegments.last().equals("refreshToken",false)) {
            val result = refreshToken()
            if (result) {
          // مشکل رفرش بوده و حل شده
                return response.request
            }

        }
        return null
    }

    private fun refreshToken(): Boolean {
        val request: retrofit2.Response<LoginResponse> = apiService.refreshToken().execute()
        if (request.body() != null) {
            if (request.body()!!.success) {
                return true
            }
        }
        return false
    }
}