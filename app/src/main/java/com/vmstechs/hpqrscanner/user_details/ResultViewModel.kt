package com.vmstechs.hpqrscanner.user_details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fmcg.retrofit.ApiService
import com.vmstechs.hpqrresult.home.AllJoinedUserResponse
import com.vmstechs.hpqrscanner.utils.ErrorResponseUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class ResultViewModel : ViewModel() {
    private val apiService = ApiService.getNetworkService()
    private var job: Job? = null

    val allUsersResponse = MutableLiveData<AllJoinedUserResponse>()

    val joinConferenceResponse = MutableLiveData<JoinConferenceResponse>()
    val userDetailsResponse = MutableLiveData<UserDetailsResponse>()
    val resultError = MutableLiveData<String?>()
    val loading = MutableLiveData<Boolean>()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        when (exception) {
            is IOException -> {
                // Handle network error
                println("Network Error: ${exception.message}")
                Log.e("ERROR", "Network Error: ${exception.message}")
            }

            is HttpException -> {
                // Handle HTTP error
                println("HTTP Error: ${exception.message}")
                Log.e("ERROR", "HTTP Error: ${exception.message}")
            }

            else -> {
                // Handle other exceptions
                println("Unknown Error: ${exception.message}")
                Log.e("ERROR", "Unknown Error: ${exception.message}")
            }
        }
    }

    fun refreshUserDetails(userId: String) {
        if (isUserIdValid(userId)) {
            requestUserDetails(UserDetailsRequest(sid = userId))
        } else {
            resultError.value = "Please enter valid registered email"
        }
    }

    private fun requestUserDetails(userDetailsRequest: UserDetailsRequest) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            supervisorScope {
                try {
                    val response = apiService.requestUserDetails(userDetailsRequest)
                    withContext(Dispatchers.Main) {
                        Log.d(
                            "MVVM",
                            "Send OTP Success : ${response.isSuccessful} response : ${response.body()}"
                        )

                        if (response.code() == 200) {
                                if (response.body()?.status!!) {
                                    userDetailsResponse.value = response.body()
                                    loading.value = false
                                } else {
                                    Log.e("ERROR", "Password Error: ${response.body()?.message}")
                                    onError(response.body()?.message!!)
                                }
                        } else if (response.code() == 400) {
                            val errorMessage = ErrorResponseUtil.getError(response.errorBody()!!)
                            Log.e("ERROR", "Password Error: $errorMessage")
                            onError(errorMessage)

                        } else if (response.code() == 401) {
                            val errorMessage = ErrorResponseUtil.getError(response.errorBody()!!)
                            Log.e("ERROR", "Password Error: $errorMessage")
                            onError(errorMessage)
                        } else {
                            val errorMessage = ErrorResponseUtil.getError(response.errorBody()!!)
                            Log.e("ERROR", "Error: $errorMessage")
                            onError(errorMessage)
                        }
                    }

                } catch (e: IOException) {
                    Log.e("ERROR", "Network Error: ${e.message}")
                    onError("Network Error: ${e.message}")
                } catch (e: HttpException) {
                    Log.e("ERROR", "HTTP Error: ${e.message}")
                    onError("HTTP Error: ${e.message}")
                } catch (e: Exception) {
                    Log.e("ERROR", "Unknown Error: ${e.message}")
                    onError("Unknown Error: ${e.message}")
                }
            }
        }
    }


    fun refreshUserJoinConference(userId: String) {
        if (isUserIdValid(userId)) {
            requestJoinConference(UserDetailsRequest(sid = userId))
        } else {
            resultError.value = "Invalid QR code"
        }
    }

    private fun requestJoinConference(userDetailsRequest: UserDetailsRequest) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            supervisorScope {
                try {
                    val response = apiService.requestJoinConference(userDetailsRequest)
                    withContext(Dispatchers.Main) {
                        Log.d(
                            "MVVM",
                            "Send OTP Success : ${response.isSuccessful} response : ${response.body()}"
                        )

                        if (response.code() == 200) {
                            if (response.body()?.status!!) {
                                joinConferenceResponse.value = response.body()
                                loading.value = false
                            } else {
                                Log.e("ERROR", "Password Error: ${response.body()?.message}")
                                onError(response.body()?.message!!)
                            }

                        } else if (response.code() == 400) {
                            val errorMessage = ErrorResponseUtil.getError(response.errorBody()!!)
                            Log.e("ERROR", "Password Error: $errorMessage")
                            onError(errorMessage)

                        } else if (response.code() == 401) {
                            val errorMessage = ErrorResponseUtil.getError(response.errorBody()!!)
                            Log.e("ERROR", "Password Error: $errorMessage")
                            onError(errorMessage)
                        } else {
                            val errorMessage = ErrorResponseUtil.getError(response.errorBody()!!)
                            Log.e("ERROR", "Error: $errorMessage")
                            onError(errorMessage)
                        }
                    }

                } catch (e: IOException) {
                    Log.e("ERROR", "Network Error: ${e.message}")
                    onError("Network Error: ${e.message}")
                } catch (e: HttpException) {
                    Log.e("ERROR", "HTTP Error: ${e.message}")
                    onError("HTTP Error: ${e.message}")
                } catch (e: Exception) {
                    Log.e("ERROR", "Unknown Error: ${e.message}")
                    onError("Unknown Error: ${e.message}")
                }
            }
        }
    }


    fun refreshAllUsers() {

            requestAllJoinedConference()

    }

    private fun requestAllJoinedConference() {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            supervisorScope {
                try {
                    val response = apiService.requestAllJoinedUsers()
                    withContext(Dispatchers.Main) {
                        Log.d(
                            "MVVM",
                            "Send OTP Success : ${response.isSuccessful} response : ${response.body()}"
                        )

                        if (response.code() == 200) {
                            if (response.body()?.status!!) {
                                allUsersResponse.value = response.body()
                                loading.value = false
                            } else {
                                Log.e("ERROR", "Password Error: ${response.body()?.message}")
                                onError(response.body()?.message!!)
                            }

                        } else if (response.code() == 400) {
                            val errorMessage = ErrorResponseUtil.getError(response.errorBody()!!)
                            Log.e("ERROR", "Password Error: $errorMessage")
                            onError(errorMessage)

                        } else if (response.code() == 401) {
                            val errorMessage = ErrorResponseUtil.getError(response.errorBody()!!)
                            Log.e("ERROR", "Password Error: $errorMessage")
                            onError(errorMessage)
                        } else {
                            val errorMessage = ErrorResponseUtil.getError(response.errorBody()!!)
                            Log.e("ERROR", "Error: $errorMessage")
                            onError(errorMessage)
                        }
                    }

                } catch (e: IOException) {
                    Log.e("ERROR", "Network Error: ${e.message}")
                    onError("Network Error: ${e.message}")
                } catch (e: HttpException) {
                    Log.e("ERROR", "HTTP Error: ${e.message}")
                    onError("HTTP Error: ${e.message}")
                } catch (e: Exception) {
                    Log.e("ERROR", "Unknown Error: ${e.message}")
                    onError("Unknown Error: ${e.message}")
                }
            }
        }
    }




    private fun onError(message: String) {
        Log.d("MVVM", "Send OTP Error Response $message")
        resultError.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    // A placeholder username validation check
    private fun isUserIdValid(userId: String): Boolean {
        return userId.isNotEmpty()
    }
}