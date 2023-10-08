package com.example.malangtrip.nav.home.local

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.malangtrip.nav.home.MainHome
import com.example.malangtrip.nav.home.TripAdapter
import com.example.malangtrip.nav.home.TripText
import com.example.malangtrip.key.TripInfo
import com.example.malangtrip.R
import com.example.malangtrip.databinding.FragmentJejuBinding
import com.example.malangtrip.key.DBKey
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MainJeju : Fragment(){

    private var _binding: FragmentJejuBinding?=null
    private val binding get()=_binding!!
    private lateinit var jejuTripAdapter : TripAdapter
    private val jejuTripList = mutableListOf<TripInfo>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJejuBinding.inflate(inflater,container,false)
        val root: View = binding.root
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setTitle("제주도로 떠나요")
        setHasOptionsMenu(true)
        loadTripData()
        val recyclerView: RecyclerView = binding.rvDriverList
        jejuTripAdapter = TripAdapter(jejuTripList){ it->
            val intent = Intent(context,TripText::class.java)
            intent.putExtra("trip_Id",it.tripId)
            intent.putExtra("driver_Id",it.tripWriterId)
            startActivity(intent)
        }
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = jejuTripAdapter




        return root
    }


    private fun loadTripData() {


        Firebase.database.reference.child(DBKey.Trip_Info)
            .addListenerForSingleValueEvent(object:
                ValueEventListener {

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    jejuTripList.clear()

                    snapshot.children.forEach { parentSnapshot ->
                        parentSnapshot.children.forEach { childSnapshot ->
                            val jeju_Trip = childSnapshot.getValue<TripInfo>()
                            jeju_Trip ?: return
                            jeju_Trip.local?.let { it1 -> Log.d("여행 잘 배껴오나", it1) }
                            if(jeju_Trip.local=="jeju")
                            {
                                jejuTripList.add(jeju_Trip)
                            }
                        }
                    }
                    jejuTripList.reverse()
                    jejuTripAdapter.notifyDataSetChanged()
                }
            })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                //requireActivity().onBackPressed()
                val homeFragment = MainHome()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragmentContainer, homeFragment)
                transaction.addToBackStack(null)
                transaction.commit()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}