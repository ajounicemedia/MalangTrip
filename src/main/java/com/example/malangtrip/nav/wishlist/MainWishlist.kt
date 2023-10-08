package com.example.malangtrip.nav.wishlist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.malangtrip.MainScreen
import com.example.malangtrip.nav.home.MainHome
import com.example.malangtrip.nav.home.TripAdapter
import com.example.malangtrip.key.TripInfo
import com.example.malangtrip.R
import com.example.malangtrip.databinding.FragmentWishlistBinding
import com.example.malangtrip.key.DBKey
import com.example.malangtrip.nav.home.TripText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

//찜목록 메인
class MainWishlist : Fragment(){
    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var myWishListAdapter : TripAdapter
    private val myWishlist = mutableListOf<TripInfo>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {


        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // 액션바 설정, 이름변경
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.title = "찜목록"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
        loadWishlistData()
        Log.d("내 위시리스트", myWishlist.toString())
        val recyclerView: RecyclerView = binding.rvWishlist
        myWishListAdapter = TripAdapter(myWishlist){
            val intent = Intent(context, TripText::class.java)
            intent.putExtra("trip_Id",it.tripId)
            intent.putExtra("driver_Id",it.tripWriterId)
            startActivity(intent)
        }
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = myWishListAdapter




        // 뒤로가기 버튼 처리 기본 뒤로가기 버튼 눌렀을 때 홈 프래그먼트로
        root.isFocusableInTouchMode = true
        root.requestFocus()
        root.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                // 현재 프래그먼트가 액티비티에 연결되어 있을 때에만 동작
                if (isAdded) {
                    val mainActivity = activity as? MainScreen
                    mainActivity?.binding?.navigationView?.selectedItemId = R.id.item_home
                }

                val homeFragment = MainHome()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragmentContainer, homeFragment)
                transaction.addToBackStack(null)
                transaction.commit()

                true
            } else {
                false
            }
        }
        return root
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
    private fun loadWishlistData() {

        val curruntId = Firebase.auth.currentUser?.uid ?: ""
        Firebase.database.reference.child(DBKey.My_Wishlist).child(curruntId)
            .addValueEventListener(object: ValueEventListener {

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Query canceled: $error")
                }

        override fun onDataChange(snapshot: DataSnapshot) {
                        myWishlist.clear()


                            snapshot.children.forEach {snapshot->
                                val myWishlist = snapshot.getValue<TripInfo>()
                                myWishlist ?: return
                                myWishlist.local?.let { it1 -> Log.d("여행 잘 배껴오나", it1) }
                                this@MainWishlist.myWishlist.add(myWishlist)

                            }
                        myWishlist.reverse()
                        myWishListAdapter.notifyDataSetChanged()
                }
            })
    }
}