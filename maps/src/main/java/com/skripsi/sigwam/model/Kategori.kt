package com.skripsi.sigwam.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Kategori(
    val id_kategori : String,
    val name_kategori : String
) : Parcelable{
    override fun toString() = this.name_kategori
}