package uz.mrx.aripro.domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.mrx.aripro.data.remote.websocket.CourierWebSocketClient

@Module
@InstallIn(SingletonComponent::class)
object WebSocketModule {

    @Provides
    fun provideCourierWebSocketClient(): CourierWebSocketClient {
        return CourierWebSocketClient()
    }

}
