package com.emoji.adminsig.helpers

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emoji.adminsig.R
import com.emoji.adminsig.activities.DestinationDetailActivity
import com.emoji.adminsig.activities.DestinationListActivity
import com.emoji.adminsig.models.Destination
import com.emoji.adminsig.services.ServiceBuilder
import kotlinx.android.synthetic.main.activity_destiny_create.view.*
import kotlinx.android.synthetic.main.activity_destiny_list.*
import kotlinx.android.synthetic.main.activity_destiny_list.view.*
import kotlinx.android.synthetic.main.list_item.view.*
import kotlin.coroutines.coroutineContext


class DestinationAdapter(private val destinationList: ArrayList<Destination>): RecyclerView.Adapter<DestinationAdapter.ListViewHolder>(), Filterable {

	internal var filterListResult: ArrayList<Destination>

	init{
		this.filterListResult = destinationList
	}

	fun setData(items: ArrayList<Destination>){
		filterListResult.clear()
		filterListResult.addAll(items)
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): ListViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
		return ListViewHolder(view)
	}

	override fun onBindViewHolder(holder: ListViewHolder, position: Int) {

		holder.bind(filterListResult[position])

	}

	override fun getItemCount(): Int {
		return filterListResult.size
	}

	inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		fun bind(destination: Destination) {
			with(itemView){
				txv_destination.text = destination.name_destination
				txv_dest_desc.text = destination.desc_destination
				txv_cat.text = destination.id_kategori

				if(destination.img_destination == ""){
					iv_home.setImageResource(R.drawable.undraw_journey_lwlj)
				}else{
				Glide.with(context)
					.load("http://siwita.000webhostapp.com/rest_api/rest-server-sig/assets/foto/"+destination.img_destination)
					.apply(RequestOptions().override(100, 100))
					.into(iv_home)
				}
				itemView.setOnClickListener {
					val intent = Intent(context, DestinationDetailActivity::class.java)
					intent.putExtra(DestinationDetailActivity.EXTRA_DETAIl, destination)
					context.startActivity(intent)
				}

			}
		}
	}

	override fun getFilter(): Filter {
		return object:Filter(){
			override fun performFiltering(constraint: CharSequence?): FilterResults {
				val charSearch = constraint.toString()
				if(charSearch.isEmpty())
					filterListResult = destinationList
				else {
					val resultlist = ArrayList<Destination>()
					for (row in destinationList) {
						if (row.id_kabupaten!!.toLowerCase().contains(charSearch.toLowerCase()) || row.id_kecamatan!!.toLowerCase().contains(charSearch.toLowerCase()))
							resultlist.add(row)

						else if(row.name_destination!!.toLowerCase().contains(charSearch.toLowerCase())) {
							resultlist.add(row)
						}
					}
					filterListResult = resultlist
				}
				val filterResult = Filter.FilterResults()
				filterResult.values = filterListResult
				return filterResult
			}

			override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
				filterListResult = results!!.values as ArrayList<Destination>
				notifyDataSetChanged()
			}

		}
	}
}
