package com.emoji.adminsig.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KabupatenResponse(
    var status: Boolean,
    var data: Array<Kabupaten>
):Parcelable