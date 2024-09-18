package com.vmstechs.hpqrscanner.register

data class RegistrationResponse(
    val data: UserData,
    val message: String,
    val status: Boolean
)