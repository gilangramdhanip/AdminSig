package com.emoji.adminsig.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Destination(
	var id_destination: Int,
	var name_destination: String? = null,
	var lat_destination: String? = null,
	var lng_destination: String? = null,
	var address_destination: String? = null,
	var desc_destination: String? = null,
	var category_destination: String? = null,
	var img_destination: String? = null,
	var id_kecamatan: String? = null,
	var id_admin: String? = null
) :Parcelable



