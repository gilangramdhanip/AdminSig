package com.emoji.adminsig.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Kabupaten(
val id_kabupaten : String,
val name_kabupaten : String
) : Parcelable