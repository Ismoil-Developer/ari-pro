package uz.mrx.aripro.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uz.mrx.aripro.domain.usecase.order.OrderUseCase
import uz.mrx.aripro.domain.usecase.order.impl.OrderUseCaseImpl
import uz.mrx.aripro.domain.usecase.profile.ProfileUseCase
import uz.mrx.aripro.domain.usecase.profile.impl.ProfileUseCaseImpl
import uz.mrx.aripro.domain.usecase.register.RegisterUseCase
import uz.mrx.aripro.domain.usecase.register.impl.RegisterUseCaseImpl

@Module
@InstallIn(ViewModelComponent::class)
interface UseCaseModule {

    @Binds
    fun bindRegisterUseCase(impl: RegisterUseCaseImpl): RegisterUseCase

    @Binds
    fun bindOrderUseCase(impl: OrderUseCaseImpl): OrderUseCase

    @Binds
    fun bindProfileUseCase(impl:ProfileUseCaseImpl): ProfileUseCase

}
