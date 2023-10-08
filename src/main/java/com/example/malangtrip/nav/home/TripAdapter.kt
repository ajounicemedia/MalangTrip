package com.example.malangtrip.nav.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast


import androidx.recyclerview.widget.RecyclerView
import com.example.malangtrip.key.TripInfo
import com.example.malangtrip.key.WishlistInfo
import com.example.malangtrip.databinding.AdapterTripInfoBinding
import com.example.malangtrip.key.DBKey
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase



class TripAdapter(private var tripList: List<TripInfo>, private val onClick: (TripInfo) -> Unit) : RecyclerView.Adapter<TripAdapter.ViewHolder>(){

    inner class ViewHolder(private val binding: AdapterTripInfoBinding):RecyclerView.ViewHolder(binding.root){
        var bookmark = false
        val myId = Firebase.auth.currentUser?.uid ?: ""
        fun bind(item: TripInfo)
        {
            Firebase.database.getReference(DBKey.My_Wishlist).child(myId).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (childSnapshot in dataSnapshot.children) {

                            val wishlistKey = childSnapshot.getValue<WishlistInfo>()
                            // Log.d("ChildFound", "Child with key $wishlist_Key found!")
                           // if (wishlist_Key != null) {
                        childSnapshot.key?.let { Log.d("ChildFound", it) }
                        if (wishlistKey != null) {
                            if (wishlistKey.tripId == item.tripId) {
                                // 해당 아이템을 찾았을 때의 동작을 수행합니다.
                                // 예: Toast 메시지를 표시하거나, 다른 로직을 수행할 수 있습니다.
                                bookmark = true
                                binding.bookmarkBtn.setImageResource(android.R.drawable.btn_star_big_on)
                                Log.d("ChildFound", "Child with key $childSnapshot found!")

                                // }

                            }
                        }

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 쿼리 실행에 실패했을 때의 동작을 수행합니다.
                    // 예: 에러 메시지를 표시하거나, 오류 처리 로직을 수행할 수 있습니다.
                    Log.e("FirebaseError", "Query canceled: $databaseError")
                }
            })

            binding.tripTitle.text = "여행 제목 : " + item.title
            binding.tripSchedule.text = "여행 일정 : " + item.schedule
            binding.tripPriceText.text = "가격 : "+ item.price+"원"
            val key =  Firebase.database(DBKey.DB_URL).reference.push().key.toString()
            binding.bookmarkBtn.setOnClickListener{

                    if(item.tripWriterId==myId)
                    {
                        Toast.makeText(binding.root.context, "자신의 글은 북마크에 추가하지 못합니다", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                if(bookmark==false)
                {
                    bookmark = true
                    binding.bookmarkBtn.setImageResource(android.R.drawable.btn_star_big_on)

                    item.tripId?.let { it1 ->
                        Firebase.database.getReference(DBKey.My_Wishlist).child(myId).child(
                            it1
                        ).setValue(TripInfo(key,item.tripId,item.local,item.title,item.schedule,item.content,item.price))
                    }

                }
                else
                {
                    bookmark = false
                    binding.bookmarkBtn.setImageResource(android.R.drawable.btn_star_big_off)
                    item.tripId?.let { it1 ->
                        Firebase.database.getReference(DBKey.My_Wishlist).child(myId).child(
                            it1
                        ).removeValue()
                    }
                }
            }
            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AdapterTripInfoBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    }

    override fun getItemCount(): Int {
        return tripList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val currentItem = tripList[position]
        holder.bind(currentItem)
    }

}