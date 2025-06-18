package uz.mrx.aripro.data.repository.profile.impl

import android.content.Context
import android.net.Uri
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import uz.mrx.aripro.data.remote.api.ProfileApi
import uz.mrx.aripro.data.remote.response.profile.ContactResponse
import uz.mrx.aripro.data.remote.response.profile.DeliveryHomeResponse
import uz.mrx.aripro.data.remote.response.profile.ProfileResponse
import uz.mrx.aripro.data.repository.profile.ProfileRepository
import uz.mrx.aripro.utils.ResultData
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(private val api: ProfileApi, @ApplicationContext private val context: Context) :
    ProfileRepository {


    override suspend fun getProfile() = channelFlow<ResultData<DeliveryHomeResponse>> {

        try {

            val response = api.getProfile()

            if (response.isSuccessful) {

                val profileResponse = response.body()
                if (profileResponse != null) {
                    trySend(ResultData.success(profileResponse))
                } else {
                    close(Exception("No data found for the provided ID"))
                }
            } else {
                val errorMessage = "Error ${response.code()}: ${response.message()}"
                Log.e("API_ERROR", errorMessage)
                close(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("API_EXCEPTION", "Exception while fetching data by ID: ${e.message}")
            close(e)
        }
    }

    override suspend fun getProfilePage() = channelFlow<ResultData<ProfileResponse>> {
        try {

            val response = api.getProfilePage()

            if (response.isSuccessful) {

                val profileResponse = response.body()
                if (profileResponse != null) {
                    trySend(ResultData.success(profileResponse))
                } else {
                    close(Exception("No data found for the provided ID"))
                }
            } else {
                val errorMessage = "Error ${response.code()}: ${response.message()}"
                Log.e("API_ERROR", errorMessage)
                close(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("API_EXCEPTION", "Exception while fetching data by ID: ${e.message}")
            close(e)
        }

    }

    override suspend fun getContact() = channelFlow<ResultData<ContactResponse>> {
        try {
            val response = api.getContact()

            if (response.isSuccessful){
                val baseResponse = response.body()
                if (baseResponse != null) {

                    trySend(ResultData.success(baseResponse))
                } else {
                    trySend(ResultData.messageText("Unknown error"))
                }
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Not load"
                trySend(ResultData.error(Throwable(errorMessage)))
            }
        } catch (e: Exception) {
            trySend(ResultData.error(e))
        }
    }.catch { emit(ResultData.error(it)) }


}