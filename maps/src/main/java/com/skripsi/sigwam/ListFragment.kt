package com.skripsi.sigwam

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.skripsi.sigwam.adapter.DestinationAdapter
import com.skripsi.sigwam.model.*
import com.skripsi.sigwam.service.DestinationService
import com.skripsi.sigwam.service.ServiceBuilder
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.fragment_maps.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */


class ListFragment : Fragment() {

    private val apiService = ServiceBuilder.create()
    private lateinit var kabupaten : String
    private lateinit var kecamatan : String

    lateinit var simpanNamaKab : String
    private lateinit var spinnerKab : Array<Kabupaten>
    private lateinit var spinnerKec : Array<Kecamatan>

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spin_kabupaten.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{


            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if(position == 0){
                    spin_kecamatan.visibility = View.GONE
                    kabupaten = ""
                    rvMaps.clearOnChildAttachStateChangeListeners()
                    loadDestinationList()
                }else{
                    spin_kecamatan.visibility = View.VISIBLE
                    kabupaten = spin_kabupaten.selectedItem.toString()
                    destinationAdapter.filter.filter(kabupaten)
                    simpanNamaKab = spinnerKab[position-1].id_kabupaten
                    setKecamatanSpinner(simpanNamaKab)
                }
            }

        }

        spin_kecamatan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if(position == 0){
                    kecamatan = ""
                    destinationAdapter.filter.filter(kabupaten)
                    setKecamatan(kabupaten, kecamatan)
                }else{
                    kecamatan = spin_kecamatan.selectedItem.toString()
                    destinationAdapter.filter.filter(kecamatan)
                    setKecamatan(kabupaten, kecamatan)
                }
            }

        }
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
                txv_jumlah_destinasi.text = destinationAdapter.itemCount.toString()

                btn_cari.setOnClickListener {
                    scView.visibility = View.VISIBLE
                    btn_cari.visibility = View.GONE

                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, destination)
                    scView.threshold=0
                    scView.setAdapter(adapter)
                    scView.setOnItemClickListener { adapterView, view, i, l ->
                        val a = scView.adapter.getItem(i) as Destination
                        destinationAdapter.setFlter(a)
                        txv_jumlah_destinasi.text = destinationAdapter.itemCount.toString()

                        if(scView.text.equals("")){
                            btn_clear.visibility = View.GONE
                        }else{
                            btn_clear.visibility = View.VISIBLE
                            btn_clear.setOnClickListener {
                                scView.text.clear()
                                btn_cari.visibility = View.VISIBLE
                                scView.visibility = View.GONE
                                btn_clear.visibility = View.GONE
                                initSpinnerKabupaten()
                            }
                        }
                    }
                }
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
        initSpinnerKabupaten()
    }

    private fun initSpinnerKabupaten(){

        apiService.getKabupaten().enqueue(object : Callback<KabupatenResponse> {
            override fun onFailure(call: Call<KabupatenResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show();
            }

            override fun onResponse(
                call: Call<KabupatenResponse>,
                response: Response<KabupatenResponse>
            ) {
                if(response.isSuccessful){
                    spinnerKab = response.body()!!.data
                    val listSpinner = ArrayList<String>(spinnerKab.size)
                    listSpinner.add("Semua Kabupaten")
                    spinnerKab.forEach {
                        listSpinner.add(it.name_kabupaten)
                    }

                    val adapter = context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, listSpinner) }
                    adapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spin_kabupaten.adapter = adapter

                    Log.d("KabupatenResponse", response.toString())
                }

                else{
                    Log.d("gagalresponse", response.toString())
                    Toast.makeText(requireContext(), "Gagal mengambil spinner", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }


    private fun setKecamatanSpinner(idkabupaten: String){
        apiService.getKecamatan(idkabupaten).enqueue(object : Callback<KecamatanResponse> {
            override fun onFailure(call: Call<KecamatanResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show();
            }

            override fun onResponse(
                call: Call<KecamatanResponse>,
                response: Response<KecamatanResponse>
            ) {
                if(response.isSuccessful){
                    spinnerKec = response.body()!!.data
                    val listSpinner = ArrayList<String>(spinnerKec.size)
                    listSpinner.add("Semua Kecamatan")
                    spinnerKec.forEach {
                        listSpinner.add(it.name_kecamatan)
                    }

                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listSpinner)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spin_kecamatan.adapter = adapter
                    txv_jumlah_destinasi.text = destinationAdapter.itemCount.toString()

                    Log.d("berhasilResponse", response.toString())
                }

                else{
                    Log.d("gagalresponse", response.toString())
                    Toast.makeText(requireContext(), "Gagal mengambil spinner", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    private fun setKecamatan(idkabupaten: String,idkecamatan: String){
        apiService.getKecamatanbyid(idkabupaten,idkecamatan).enqueue(object : Callback<KecamatanResponse>{
            override fun onFailure(call: Call<KecamatanResponse>, t: Throwable) {
                Toast.makeText(requireActivity(), "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<KecamatanResponse>,
                response: Response<KecamatanResponse>
            ) {
                if(response.isSuccessful){
                    txv_jumlah_destinasi.text = destinationAdapter.itemCount.toString()
                    Log.d("berhasilResponse", response.toString())
                }

                else{
                    Log.d("gagalresponse", response.toString())
                    Toast.makeText(requireActivity(), "Gagal mengambil spinner", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }


}
