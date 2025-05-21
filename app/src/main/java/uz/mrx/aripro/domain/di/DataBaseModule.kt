package uz.mrx.aripro.domain.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.mrx.aripro.data.local.shp.MySharedPreference
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {


    @[Provides Singleton]
    fun provideSharedPrefs(
        @ApplicationContext context: Context,
        sharedPreferences: SharedPreferences
    ): MySharedPreference =
        MySharedPreference(context, sharedPreferences)

    @[Provides Singleton]
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("app_data", Context.MODE_PRIVATE)

}