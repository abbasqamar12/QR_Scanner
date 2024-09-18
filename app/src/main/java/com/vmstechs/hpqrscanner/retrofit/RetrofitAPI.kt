package com.vmstechs.hpqrscanner.retrofit

import com.vmstechs.hpqrresult.home.AllJoinedUserResponse
import com.vmstechs.hpqrresult.home.NewUserResponse
import com.vmstechs.hpqrscanner.register.RegisterUserRequest
import com.vmstechs.hpqrscanner.register.RegistrationResponse
import com.vmstechs.hpqrscanner.user_details.JoinConferenceResponse
import com.vmstechs.hpqrscanner.user_details.UserDetailsRequest
import com.vmstechs.hpqrscanner.user_details.UserDetailsResponse
import com.vmstechs.hpqrscanner.utils.NetworkConst
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap


interface RetrofitAPI {

    /* @GET(NetworkConst.NEW_JOINED_USER)
     suspend fun requestNewJoiner(): Response<CompanyListResponse>
 */

    @POST(NetworkConst.USER_DETAILS)
    suspend fun requestUserDetails(@Body requestModel: UserDetailsRequest): Response<UserDetailsResponse>

    @POST(NetworkConst.JOIN_CONFERENCE)
    suspend fun requestJoinConference(@Body requestModel: UserDetailsRequest): Response<JoinConferenceResponse>

    @GET(NetworkConst.NEW_JOINED_USER)
    suspend fun requestNewJoiner(): Response<NewUserResponse>

    @GET(NetworkConst.ALL_JOINED_USERS)
    suspend fun requestAllJoinedUsers(): Response<AllJoinedUserResponse>

    @Multipart
    @POST(NetworkConst.REGISTER_USER)
    suspend fun requestRegisterUser(
        @PartMap profileMap: HashMap<String, RequestBody>?,
        @Part profilePhoto: MultipartBody.Part?
    ): Response<RegistrationResponse>


    @POST(NetworkConst.REGISTER_USER)
    suspend fun requestRegisterWithoutImage(@Body requestModel: RegisterUserRequest): Response<RegistrationResponse>


}
