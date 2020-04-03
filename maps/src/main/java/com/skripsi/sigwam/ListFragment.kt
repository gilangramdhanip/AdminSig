package com.skripsi.sigwam

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list.*

/**
 * A simple [Fragment] subclass.
 */


class ListFragment : Fragment() {

    companion object{
        const val EXTRA_USER_MAP = "EXTRA_USER_MAP"
        private const val TAG = "ListFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val userMaps = generateSampleData()

//        rvMaps.layoutManager = LinearLayoutManager(context)
//        rvMaps.adapter = DestinationAdapter(requireContext(), userMaps, object : DestinationAdapter.OnClickListener{
//            override fun onItemClick(position: Int) {
//                Log.i(TAG, "OnItemClick $position")
//
//                val mMapsFragment = MapsFragment()
//
//                val mBundle = Bundle()
//                mBundle.putString(EXTRA_USER_MAP, userMaps[position].toString())
//
//                mMapsFragment.arguments = mBundle
//                val mFragmentManager = fragmentManager
//                mFragmentManager?.beginTransaction()?.apply {
//                    add(
//                        R.id.frame_container,
//                        mMapsFragment,
//                        MapsFragment::class.java.simpleName
//                    )
//                    commit()
//                }
//            }
//        })
    }

//    private fun generateSampleData(): List<UserMap> {
//        return listOf(
//            UserMap(
//                "Memories from University",
//                listOf(
//                    Destination("Branner Hall", "Best dorm at Stanford", 37.426, -122.163),
//                    Destination("Gates CS building", "Many long nights in this basement", 37.430, -122.173),
//                    Destination("Pinkberry", "First date with my wife", 37.444, -122.170)
//                )
//            ),
//            UserMap("January vacation planning!",
//                listOf(
//                    Destination("Tokyo", "Overnight layover", 35.67, 139.65),
//                    Destination("Ranchi", "Family visit + wedding!", 23.34, 85.31),
//                    Destination("Singapore", "Inspired by \"Crazy Rich Asians\"", 1.35, 103.82)
//                )),
//            UserMap("Singapore travel itinerary",
//                listOf(
//                    Destination("Gardens by the Bay", "Amazing urban nature park", 1.282, 103.864),
//                    Destination("Jurong Bird Park", "Family-friendly park with many varieties of birds", 1.319, 103.706),
//                    Destination("Sentosa", "Island resort with panoramic views", 1.249, 103.830),
//                    Destination("Botanic Gardens", "One of the world's greatest tropical gardens", 1.3138, 103.8159)
//                )
//            ),
//            UserMap("My favorite places in the Midwest",
//                listOf(
//                    Destination("Chicago", "Urban center of the midwest, the \"Windy City\"", 41.878, -87.630),
//                    Destination("Rochester, Michigan", "The best of Detroit suburbia", 42.681, -83.134),
//                    Destination("Mackinaw City", "The entrance into the Upper Peninsula", 45.777, -84.727),
//                    Destination("Michigan State University", "Home to the Spartans", 42.701, -84.482),
//                    Destination("University of Michigan", "Home to the Wolverines", 42.278, -83.738)
//                )
//            ),
//            UserMap("Restaurants to try",
//                listOf(
//                    Destination("Champ's Diner", "Retro diner in Brooklyn", 40.709, -73.941),
//                    Destination("Althea", "Chicago upscale dining with an amazing view", 41.895, -87.625),
//                    Destination("Shizen", "Elegant sushi in San Francisco", 37.768, -122.422),
//                    Destination("Citizen Eatery", "Bright cafe in Austin with a pink rabbit", 30.322, -97.739),
//                    Destination("Kati Thai", "Authentic Portland Thai food, served with love", 45.505, -122.635)
//                )
//            )
//        )
//    }

}
