package uz.mrx.aripro.presentation.direction.login

interface LoginScreenDirection {

    suspend fun openConfirmScreen(phoneNumber:String, code:String)




}