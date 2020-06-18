package com.skripsi.sigwam.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Wisatawan (
    var id_wisatawan: Int?,
    var email: String? = null,
    var nama: String? = null,
    var password: String? = null
):Parcelable