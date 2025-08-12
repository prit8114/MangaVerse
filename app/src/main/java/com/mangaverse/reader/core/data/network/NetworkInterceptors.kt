package com.mangaverse.reader.core.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor that adds authentication headers to requests
 */
@Singleton
class AuthInterceptor @Inject constructor() : Interceptor {
    
    // Token will be provided by the auth repository
    private var authToken: String? = null
    
    fun setToken(token: String?) {
        authToken = token
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip authentication for login/register endpoints
        if (shouldSkipAuth(originalRequest)) {
            return chain.proceed(originalRequest)
        }
        
        // Add auth token if available
        val requestBuilder = originalRequest.newBuilder()
        authToken?.let {
            requestBuilder.header("Authorization", "Bearer $it")
        }
        
        // Add common headers
        requestBuilder.header("Accept", "application/json")
        requestBuilder.header("Content-Type", "application/json")
        
        return chain.proceed(requestBuilder.build())
    }
    
    private fun shouldSkipAuth(request: Request): Boolean {
        val path = request.url.encodedPath
        return path.contains("/auth/login") || path.contains("/auth/register")
    }
}

/**
 * Interceptor that checks for network connectivity before proceeding with requests
 */
class NetworkConnectionInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isNetworkAvailable()) {
            throw NoConnectivityException()
        }
        return chain.proceed(chain.request())
    }
    
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}

/**
 * Exception thrown when there is no network connectivity
 */
class NoConnectivityException : IOException("No network connection available")