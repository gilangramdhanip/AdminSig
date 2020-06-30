package com.emoji.adminsig.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emoji.adminsig.R
import com.emoji.adminsig.activities.DestinationDetailActivity
import com.emoji.adminsig.models.Destination
import kotlinx.android.synthetic.main.list_item.view.*


class PencarianAdapter(private val destinationList: ArrayList<Destination>): RecyclerView.Adapter<PencarianAdapter.ListViewHolder>(), Filterable {

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
                        .load("https://siwita-lombok.000webhostapp.com/rest_api/rest-server-sig/assets/foto/"+destination.img_destination)
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
                        row.name_destination!!.toLowerCase().contains(charSearch.toLowerCase())
                        resultlist.add(row)

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
