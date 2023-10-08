package com.example.malangtrip.nav.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.malangtrip.key.DriverInfo
import com.example.malangtrip.key.TripInfo
import com.example.malangtrip.R
import com.example.malangtrip.databinding.ActivityDriverProfileBinding
import com.example.malangtrip.key.DBKey
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage



class DriverProfile : AppCompatActivity() {
    lateinit var binding: ActivityDriverProfileBinding
    lateinit var driverTripAdapter : TripAdapter
    private val driverTripList = mutableListOf<TripInfo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val imageUrl = intent.getStringExtra("image_url")
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = imageUrl?.let { storageRef.child(it) }

//        if (imageUrl != null) {
//            getImageData(imageUrl)
//        }
        imageUrl?.let {
            val imageRef = storageRef.child(it)

            imageRef.downloadUrl.addOnSuccessListener { uri ->
                loadImageWithGlide(uri.toString(), binding.ivProfilePhoto)
            }.addOnFailureListener {
                // 이미지 URL을 얻어오는 데 실패했을 때의 동작을 여기에 작성합니다.
            }
        }
        val imageViewFromFB = binding.ivProfilePhoto
        val DriverKey = intent.getStringExtra("DriverKey").toString()
        if (imageUrl != null) {
            Log.d("Dsdfsdf",imageUrl)
        }
        loadTripData(DriverKey)
        getDriverData(DriverKey)
        if (imageUrl != null) {
            loadImageWithGlide(imageUrl,  imageViewFromFB)
        }
        //(DriverKey)


    }
    private fun getDriverData(Key: String)
    {
        val driverKey = Firebase.database.reference.child(DBKey.Driver).child(Key)
        driverKey.get().addOnSuccessListener {


            val driverProfile = it.getValue(DriverInfo::class.java)
            supportActionBar?.apply {
                title = "드라이버 닉네임 : " + driverProfile!!.nickname
                setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 표시
                //setHasOptionsMenu(true)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.my_home_back)
            }
            if (driverProfile != null) {
                binding.driverLocal.text = "서비스 지역 : " + driverProfile.local
                binding.driverDes.text = "상태 메세지 : " + driverProfile.description
                val recyclerView: RecyclerView = binding.driverTripList
                    driverTripAdapter = TripAdapter(driverTripList){ it->
                            val intent = Intent(this,TripText::class.java)
                            intent.putExtra("trip_Id",it.tripId)
                            intent.putExtra("driver_Id",it.tripWriterId)
                            startActivity(intent)
                        }
                        val LinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                        recyclerView.layoutManager = LinearLayoutManager
                        recyclerView.adapter = driverTripAdapter
            }

        }
    }
    private fun loadTripData(key: String) {


        Firebase.database.reference.child(DBKey.Trip_Info).child(key)
            .addListenerForSingleValueEvent(object:
                ValueEventListener {

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {//드라이버의 여행 띄우는 중이었음///////////
                    driverTripList.clear()

                    snapshot.children.forEach { parentSnapshot ->

                            val driverTrip = parentSnapshot.getValue<TripInfo>()
                        driverTrip ?: return
                        driverTrip.local?.let { it1 -> Log.d("여행 잘 배껴오나", it1) }

                        driverTripList.add(driverTrip)

                        }


                    //Driver_Trip_Adapter.notifyDataSetChanged()
                    if (::driverTripAdapter.isInitialized) {
                        driverTripAdapter.notifyDataSetChanged()
                    }
                }
            })
    }
    private fun getImageData(imageUrl : String) {
        val imageViewFromFB = binding.ivProfilePhoto

        Glide.with(this)
            .load(Uri.parse(imageUrl))
            .into(imageViewFromFB)

        Log.d("이미지 잘 불러옴", imageUrl)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }
    private fun loadImageWithGlide(imageUrl: String, imageView: ImageView)
    {
        Glide.with(this)
            .load(imageUrl)
            .into(imageView)
    }


}