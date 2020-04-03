package com.emoji.adminsig.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KecamatanResponse(
    var status: Boolean,
    var data: Array<Kecamatan>
): Parcelable