package uz.mrx.aripro.presentation.direction.main

interface MainScreenDirection {

    suspend fun openProfileScreen()

    suspend fun openOrderDetailScreen()

    suspend fun openOrderDeliveryScreen()

}