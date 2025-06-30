package uz.mrx.aripro.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.mrx.aripro.data.repository.order.OrderRepository
import uz.mrx.aripro.data.repository.order.impl.OrderRepositoryImpl
import uz.mrx.aripro.data.repository.profile.ProfileRepository
import uz.mrx.aripro.data.repository.profile.impl.ProfileRepositoryImpl
import uz.mrx.aripro.data.repository.register.RegisterRepository
import uz.mrx.aripro.data.repository.register.impl.RegisterRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @[Binds Singleton]
    fun bindRegisterRepository(impl: RegisterRepositoryImpl): RegisterRepository

    @[Binds Singleton]
    fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository

    @[Binds Singleton]
    fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

}