package com.skripsi.sigwam.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Destination(
	var id_destination: Int,
	var name_destination: String,
	var lat_destination: String? = null,
	var lng_destination: String? = null,
	var address_destination: String? = null,
	var desc_destination: String? = null,
	var id_kategori: String? = null,
	var img_destination: String? = null,
	var id_kabupaten: String? = null,
	var id_kecamatan: String? = null
) :Parcelable {
	override fun toString() = this.name_destination
}



