package com.emoji.adminsig.models

data class LoginResponse (
    var status: Boolean,
    var data : SignInBody
)