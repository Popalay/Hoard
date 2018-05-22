package com.github.popalay.store.rest

import com.github.popalay.store.BuildConfig
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

class UnsafeHttpClientBuilder {

    private var okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient().newBuilder()

    fun unsafe(): UnsafeHttpClientBuilder {
        try {
            val x509manager = object : X509TrustManager {
                override fun checkClientTrusted(c: Array<out X509Certificate>?, a: String?) = Unit
                override fun checkServerTrusted(c: Array<out X509Certificate>?, a: String?) = Unit
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            }

            val socketFactory = SSLContext.getInstance("SSL")
                .apply { init(null, arrayOf(x509manager), SecureRandom()) }
                .socketFactory

            okHttpClientBuilder
                .sslSocketFactory(socketFactory, x509manager)
                .hostnameVerifier { _, _ -> true }
        } finally {
            return this
        }
    }

    fun withLogger(): UnsafeHttpClientBuilder {
        val logInterceptor = LoggingInterceptor.Builder()
            .loggable(BuildConfig.DEBUG)
            .setLevel(Level.BASIC)
            .log(Platform.INFO)
            .request(REQUEST_LOG_TAG)
            .response(RESPONSE_LOG_TAG)
            .build()
        okHttpClientBuilder.addInterceptor(logInterceptor)
        return this
    }

    fun withAuthenticator(authenticator: Authenticator): UnsafeHttpClientBuilder {
        okHttpClientBuilder.authenticator(authenticator)
        return this
    }

    fun withInterceptor(interceptor: Interceptor): UnsafeHttpClientBuilder {
        okHttpClientBuilder.addInterceptor(interceptor)
        return this
    }

    fun withNetworkInterceptor(interceptor: Interceptor): UnsafeHttpClientBuilder {
        okHttpClientBuilder.addNetworkInterceptor(interceptor)
        return this
    }

    fun build(): OkHttpClient = okHttpClientBuilder.build()

    private companion object {
        private const val REQUEST_LOG_TAG = "Request"
        private const val RESPONSE_LOG_TAG = "Response"
    }
}