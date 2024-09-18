package com.vmstechs.hpqrscanner.user_details

data class UserDetailsResponse(
    val message: String,
    val profile_data: ProfileData,
    val status: Boolean
)