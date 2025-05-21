package uz.mrx.aripro.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uz.mrx.aripro.presentation.direction.confirm.ConfirmScreenDirection
import uz.mrx.aripro.presentation.direction.confirm.impl.ConfirmScreenDirectionImpl
import uz.mrx.aripro.presentation.direction.intro.IntroScreenDirection
import uz.mrx.aripro.presentation.direction.intro.impl.IntroScreenDirectionImpl
import uz.mrx.aripro.presentation.direction.language.LanguageScreenDirection
import uz.mrx.aripro.presentation.direction.language.impl.LanguageScreenDirectionImpl
import uz.mrx.aripro.presentation.direction.login.LoginScreenDirection
import uz.mrx.aripro.presentation.direction.login.impl.LoginScreenDirectionImpl
import uz.mrx.aripro.presentation.direction.main.MainScreenDirection
import uz.mrx.aripro.presentation.direction.main.impl.MainScreenDirectionImpl
import uz.mrx.aripro.presentation.direction.splash.SplashScreenDirection
import uz.mrx.aripro.presentation.direction.splash.impl.SplashScreenDirectionImpl

@Module
@InstallIn(ViewModelComponent::class)
interface DirectionModule {

    @[Binds]
    fun bindSplashScreenDirection(impl: SplashScreenDirectionImpl): SplashScreenDirection

    @[Binds]
    fun bindLanguageScreenDirection(impl: LanguageScreenDirectionImpl): LanguageScreenDirection

    @[Binds]
    fun bindIntroScreenDirection(impl: IntroScreenDirectionImpl): IntroScreenDirection

    @[Binds]
    fun bindConfirmDirection(impl: ConfirmScreenDirectionImpl): ConfirmScreenDirection

    @[Binds]
    fun bindLoginDirection(impl: LoginScreenDirectionImpl): LoginScreenDirection

    @[Binds]
    fun bindMainScreenDirection(impl: MainScreenDirectionImpl): MainScreenDirection

}