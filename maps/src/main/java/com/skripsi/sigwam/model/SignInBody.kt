package com.skripsi.sigwam.model

data class SignInBody(
    val id_wisatawan: String,
    val email: String,
    val nama: String,
    val password: String
    )