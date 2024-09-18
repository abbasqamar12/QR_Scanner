package com.vmstechs.hpqrresult.home

data class AllJoinedUserResponse(
    val AllJoined_user_details: List<UserDetail>,
    val message: String,
    val status: Boolean
)