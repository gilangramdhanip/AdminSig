package com.emoji.adminsig.models

data class SignInBody(
    val username: String,
    val password: String,
    val id_admin: String)