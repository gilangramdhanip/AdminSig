package com.skripsi.sigwam.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Kecamatan(
    val id_kecamatan : String,
    val name_kecamatan : String,
    val id_kabupaten : String
) : Parcelable