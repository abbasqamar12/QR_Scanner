package com.vmstechs.hpqrscanner.register

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fmcg.retrofit.ApiService
import com.vmstechs.hpqrscanner.R
import com.vmstechs.hpqrscanner.user_details.JoinConferenceResponse
import com.vmstechs.hpqrscanner.user_details.UserDetailsRequest
import com.vmstechs.hpqrscanner.utils.ErrorResponseUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLConnection

class RegistrationViewModel : ViewModel() {

    private val apiService = ApiService.getNetworkService()
    private var job: Job? = null
    val joinConferenceResponse = MutableLiveData<JoinConferenceResponse>()
    val registerUserResponse = MutableLiveData<RegistrationResponse>()
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

    fun refresh(
        context: Context,
        userName: String,
        companyName: String,
        designation: String,
        email: String,
        city: String,
        timeSlot: String,
        profileFile: File
        // profile: String?
    ) {
        if (userName.isNotEmpty()) {
            if (companyName.isNotEmpty()) {
                if (designation.isNotEmpty()) {
                    if (email.isNotEmpty()) {
                        if (city.isNotEmpty()) {
                            if (timeSlot.isNotEmpty()) {
                                //  if (profile.isNullOrEmpty()) {

                                val userNameStr = RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    userName
                                )
                                val companyNameStr = RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    companyName
                                )
                                val designationStr = RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    designation
                                )
                                val emailStr = RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    email
                                )
                                val cityStr =
                                    RequestBody.create(
                                        MediaType.parse("multipart/form-data"),
                                        city
                                    )
                                val timeSlotStr = RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    timeSlot
                                )

                                val profileMap = HashMap<String, RequestBody>()
                                profileMap["full_name"] = userNameStr
                                profileMap["company_name"] = companyNameStr
                                profileMap["designation"] = designationStr
                                profileMap["email"] = emailStr
                                profileMap["city"] = cityStr
                                profileMap["timeslot"] = timeSlotStr

                                Log.d("MVVM", "Profile Image is not Null, Image: $profileFile")
                                /* val profileUri = Uri.parse(profile)
                                 // Check if fileUri is null or file is not selected
                                 val profileFile: File = if (profileUri == null) {
                                     // Use dummy image from resources
                                     val dummyBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.profile_placeholder)
                                     val dummyFile = File(context.cacheDir, "dummy_image.png")
                                     saveBitmapToFile(dummyBitmap, dummyFile)
                                     dummyFile
                                 } else {
                                     // Use selected image
                                     File(profileUri.path ?: "")
                                 }*/

                                val filePart: MultipartBody.Part? = MultipartBody.Part.createFormData("filename1", profileFile.name, RequestBody.create(MediaType.parse(URLConnection.guessContentTypeFromName(profileFile.name)), profileFile))
                                requestRegisterUser(profileMap, filePart)

                                /*if (profile!= null) {
                                    Log.d("MVVM", "Profile Image is not Null, Image: $profile")
                                    val profileUri = Uri.parse(profile)
                                    val profileFile = File(profileUri?.path?:"")
                                    val filePart: MultipartBody.Part? = MultipartBody.Part.createFormData("filename1", profileFile.name, RequestBody.create(MediaType.parse(URLConnection.guessContentTypeFromName(profileFile.name)), profileFile))
                                    requestRegisterUser(profileMap, filePart)
                                } else {
                                    Log.d("MVVM", "Profile Image is Null, Image: $profile")
                                    requestRegisterUser(profileMap, null)
                                }*/


                            } else {
                                resultError.value = "Please select a time slot"
                            }
                        } else {
                            resultError.value = "Please select city"
                        }
                    } else {
                        resultError.value = "Please enter your valid email"
                    }
                } else {
                    resultError.value = "Please enter your designation"
                }
            } else {
                resultError.value = "Please enter company name"
            }
        } else {
            resultError.value = "Please enter User Name"
        }
    }


    private fun requestRegisterUser(
        profileMap: HashMap<String, RequestBody>,
        filePart: MultipartBody.Part?
    ) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            supervisorScope {
                try {
                    val response = apiService.requestRegisterUser(profileMap, filePart)
                    withContext(Dispatchers.Main) {
                        Log.d(
                            "MVVM",
                            "Registration Success : ${response.isSuccessful} response : ${response.body()}"
                        )

                        if (response.code() == 200) {
                            if (response.body()?.status!!) {
                                registerUserResponse.value = response.body()
                                loading.value = false
                            } else {
                                Log.e("ERROR", "Register Error: ${response.body()?.message}")
                                onError(response.body()?.message!!)
                            }
                        } else if (response.code() == 400) {
                            val errorMessage = ErrorResponseUtil.getError(response.errorBody()!!)
                            Log.e("ERROR", "Register Error: $errorMessage")
                            onError(errorMessage)

                        } else if (response.code() == 401) {
                            val errorMessage = ErrorResponseUtil.getError(response.errorBody()!!)
                            Log.e("ERROR", "Register Error: $errorMessage")
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


    private fun requestRegisterUserWithOutImage(registerUserRequest: RegisterUserRequest) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            supervisorScope {
                try {
                    val response = apiService.requestRegisterWithoutImage(registerUserRequest)
                    withContext(Dispatchers.Main) {
                        Log.d(
                            "MVVM",
                            "Registration withOut Image Success : ${response.isSuccessful} response : ${response.body()}"
                        )

                        if (response.code() == 200) {
                            if (response.body()?.status!!) {
                                registerUserResponse.value = response.body()
                                loading.value = false
                            } else {
                                Log.e("ERROR", "Register Error: ${response.body()?.message}")
                                onError(response.body()?.message!!)
                            }
                        } else if (response.code() == 400) {
                            val errorMessage = ErrorResponseUtil.getError(response.errorBody()!!)
                            Log.e("ERROR", "Register Error: $errorMessage")
                            onError(errorMessage)

                        } else if (response.code() == 401) {
                            val errorMessage = ErrorResponseUtil.getError(response.errorBody()!!)
                            Log.e("ERROR", "Register Error: $errorMessage")
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
                            "Join Conference Success : ${response.isSuccessful} response : ${response.body()}"
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


    private fun onError(message: String) {
        Log.d("MVVM", "Registration Error Response $message")
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

    fun saveBitmapToFile(bitmap: Bitmap, file: File) {
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
        out.close()
    }

}