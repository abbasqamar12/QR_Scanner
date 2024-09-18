package com.vmstechs.hpqrscanner.register

data class RegisterUserRequest(
    val full_name: String,
    val company_name: String,
    val designation: String,
    val email: String,
    val city: String,
    val timeslot: String,


)
