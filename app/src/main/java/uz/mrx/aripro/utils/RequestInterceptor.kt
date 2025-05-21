package uz.mrx.aripro.utils

import okhttp3.Interceptor
import okhttp3.Response
import uz.mrx.aripro.data.local.shp.MySharedPreference
import javax.inject.Inject

class RequestInterceptor @Inject constructor(
    private val sharedPrefs: MySharedPreference
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = sharedPrefs.token
        val requestBuilder = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("accept", "application/json")

        if (accessToken.isNotEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $accessToken")
        }

        return chain.proceed(requestBuilder.build())
    }
}
