package uz.mrx.aripro.domain.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.mrx.aripro.data.remote.api.OrderApi
import uz.mrx.aripro.data.remote.api.ProfileApi
import uz.mrx.aripro.data.remote.api.RegisterApi
import uz.mrx.aripro.utils.RequestInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    fun provideLanguageInterceptor(): Interceptor =
        Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Accept-Language", "uz") // Tilni o'zgartirish mumkin
                .build()
            chain.proceed(request)
        }

    @Provides
    fun provideClient(
        loggingInterceptor: HttpLoggingInterceptor,
        languageInterceptor: Interceptor,
        requestInterceptor: RequestInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.HOURS) // Aloqa o‘rnatish uchun maksimal kutish vaqti
            .readTimeout(1, TimeUnit.HOURS)    // Javobni o‘qish uchun maksimal kutish vaqti
            .writeTimeout(1, TimeUnit.HOURS)   // Ma’lumot yozish uchun maksimal kutish vaqti
            .addInterceptor(loggingInterceptor)
            .addInterceptor(languageInterceptor)
            .addInterceptor(requestInterceptor)
            .build()

    @Provides
    fun provideGson(): GsonConverterFactory =
        GsonConverterFactory.create(GsonBuilder().setLenient().create())

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, gsonConverterFactory: GsonConverterFactory): Retrofit =
        Retrofit.Builder()
            .baseUrl("http://ari-delivery.uz/")
            .addConverterFactory(gsonConverterFactory)
            .client(client)
            .build()

    @Provides
    @Singleton
    fun provideRegisterApi(retrofit: Retrofit): RegisterApi =
        retrofit.create(RegisterApi::class.java)

    @Provides
    @Singleton
    fun provideProfileApi(retrofit: Retrofit): ProfileApi =
        retrofit.create(ProfileApi::class.java)

    @Provides
    @Singleton
    fun provideOrderApi(retrofit: Retrofit): OrderApi =
        retrofit.create(OrderApi::class.java)

}