package com.skripsi.sigwam

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skripsi.sigwam.adapter.DestinationAdapter
import com.skripsi.sigwam.model.Destination
import com.skripsi.sigwam.model.MainViewModel
import com.skripsi.sigwam.service.DestinationService
import com.skripsi.sigwam.service.ServiceBuilder
import kotlinx.android.synthetic.main.fragment_list.*

/**
 * A simple [Fragment] subclass.
 */


class ListFragment : Fragment() {

    private lateinit var destinationAdapter: DestinationAdapter
    private lateinit var mainViewModel: MainViewModel

    private val destination = ArrayList<Destination>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    private fun loadDestinationList(){
        destinationAdapter = DestinationAdapter(destination)
        destinationAdapter.notifyDataSetChanged()

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            MainViewModel::class.java
        )

        showLoading(true)

        mainViewModel.setDestination()
        rvMaps.setHasFixedSize(true)
        rvMaps.layoutManager = LinearLayoutManager(requireContext())
        rvMaps.adapter = destinationAdapter

        mainViewModel.getDestination().observe(this, Observer { destination ->
            if(destination!=null){
                destinationAdapter.setData(destination)
                showLoading(false)
            }
        })
    }

    private fun showLoading(state: Boolean){
        if(state){
            progressbar.visibility= View.VISIBLE
        }
        else{
            progressbar.visibility= View.GONE
        }
    }

    override fun onResume() {
        super.onResume()

        loadDestinationList()
    }


}
