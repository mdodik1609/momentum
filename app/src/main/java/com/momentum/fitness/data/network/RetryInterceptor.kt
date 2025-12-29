package com.momentum.fitness.data.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.math.pow

/**
 * Retry interceptor with exponential backoff
 * Retries failed requests up to 3 times with increasing delays
 */
class RetryInterceptor(
    private val maxRetries: Int = 3,
    private val baseDelayMs: Long = 1000
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        var lastException: IOException? = null

        for (attempt in 0..maxRetries) {
            try {
                val response = chain.proceed(request)
                
                // Retry on server errors (5xx) or network errors
                if (response.isSuccessful || attempt == maxRetries) {
                    return response
                }

                // Close the response body to free resources
                response.close()

                // Calculate exponential backoff delay
                if (attempt < maxRetries) {
                    val delayMs = baseDelayMs * (2.0.pow(attempt.toDouble())).toLong()
                    Thread.sleep(delayMs)
                }
            } catch (e: IOException) {
                lastException = e
                
                // Don't retry on certain exceptions
                if (e is SocketTimeoutException && attempt < maxRetries) {
                    val delayMs = baseDelayMs * (2.0.pow(attempt.toDouble())).toLong()
                    Thread.sleep(delayMs)
                    continue
                }
                
                // If this is the last attempt, throw the exception
                if (attempt == maxRetries) {
                    throw e
                }
            }
        }

        throw lastException ?: IOException("Request failed after $maxRetries retries")
    }
}







