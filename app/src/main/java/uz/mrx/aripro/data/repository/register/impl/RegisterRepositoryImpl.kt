package uz.mrx.aripro.data.repository.register.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import uz.mrx.aripro.data.remote.api.RegisterApi
import uz.mrx.aripro.data.remote.request.register.ConfirmRequest
import uz.mrx.aripro.data.remote.request.register.RegisterRequest
import uz.mrx.aripro.data.remote.response.register.ConfirmResponse
import uz.mrx.aripro.data.remote.response.register.RegisterResponse
import uz.mrx.aripro.data.repository.register.RegisterRepository
import uz.mrx.aripro.utils.ResultData
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(private val api: RegisterApi) : RegisterRepository {

    override suspend fun register(request: RegisterRequest) = channelFlow<ResultData<RegisterResponse>>{

        try {

            val response = api.postRegister(request)

            if (response.isSuccessful){

                val registerResponse = response.body()

                if (registerResponse != null){
                    trySend(ResultData.success(registerResponse))
                }else{
                    trySend(ResultData.messageText("Something went wrong"))
                }
            }else {
                val errorBody = response.errorBody()?.string()
                send(ResultData.error(Exception(errorBody)))
            }
        } catch (e: Exception) {
            send(ResultData.error(e))
        }
    }.catch { emit(ResultData.error(it)) }

    override suspend fun confirm(request: ConfirmRequest) = channelFlow<ResultData<ConfirmResponse>> {
        try {

            val response = api.postConfirm(request)

            if (response.isSuccessful){

                val registerResponse = response.body()

                if (registerResponse != null){
                    trySend(ResultData.success(registerResponse))
                }else{
                    trySend(ResultData.messageText("Something went wrong"))
                }
            }else {
                val errorBody = response.errorBody()?.string()
                send(ResultData.error(Exception(errorBody)))
            }
        } catch (e: Exception) {
            send(ResultData.error(e))
        }
    }.catch { emit(ResultData.error(it)) }

}
