package com.emoji.adminsig.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.emoji.adminsig.R
import com.skripsi.sigwam.model.Kategori
import kotlinx.android.synthetic.main.item_row_category.view.*
import kotlinx.android.synthetic.main.list_item.view.*

class KategoriAdapter(private val destinationList: ArrayList<Kategori>,private val context: Context, private val onKategoriClicked: (Int) -> Unit): RecyclerView.Adapter<KategoriAdapter.ListViewHolder>(), Filterable {

    internal var filterListResult: ArrayList<Kategori>

    init{
        this.filterListResult = destinationList
    }

    fun setData(items: ArrayList<Kategori>) {

        filterListResult.clear()
        filterListResult.addAll(items)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType:   Int): ListViewHolder
    {
        return ListViewHolder.create(context, parent,   onKategoriClicked)
    }


 class ListViewHolder(itemView: View, private val onKategoriClicked: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
        init
        {
            itemView.setOnClickListener {
                onKategoriClicked(adapterPosition)
            }
        }

        fun bindView(kategori: Kategori)
        {
            itemView.tv_category.text = kategori.name_kategori
        }

        companion object
        {

            fun create(context: Context, parent: ViewGroup, onKategoriClicked: (Int) -> Unit): ListViewHolder
            {
                return ListViewHolder(LayoutInflater
                    .from(context).inflate(R.layout.item_row_category, parent, false), onKategoriClicked)
            }
        }
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if(charSearch.isEmpty())
                    filterListResult = destinationList
                else {
                    val resultlist = ArrayList<Kategori>()
                    for (row in destinationList) {
                        if (row.name_kategori!!.toLowerCase().contains(charSearch.toLowerCase()))
                            resultlist.add(row)
                    }
                    filterListResult = resultlist
                }
                val filterResult = Filter.FilterResults()
                filterResult.values = filterListResult
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterListResult = results!!.values as ArrayList<Kategori>
                notifyDataSetChanged()
            }

        }
    }

    override fun getItemCount(): Int {
        return filterListResult.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bindView(filterListResult[position])
    }

    fun getItem(position: Int): Kategori
    {
        return filterListResult[position]
    }

}