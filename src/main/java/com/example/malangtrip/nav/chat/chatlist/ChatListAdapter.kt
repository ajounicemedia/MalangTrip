package com.example.malangtrip.nav.chat.chatlist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.malangtrip.key.ChatListInfo
import com.example.malangtrip.key.GetTime
import com.example.malangtrip.databinding.AdapterChatListBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ChatListAdapter(private val onclick: (ChatListInfo)->Unit) : ListAdapter<ChatListInfo, ChatListAdapter.ViewHolder>(differ){

    inner class ViewHolder(private val binding: AdapterChatListBinding):RecyclerView.ViewHolder(binding.root){
        //채팅방 목록에 채팅 불러오기
        fun bind(item: ChatListInfo)
        {
            //프사인데 나중에 기능 추가할 때 쓰기
            //item?.friend_Id?.let { getImageData(it,binding.profileImageView) }
            binding.tvNickname.text = item.friendName
            binding.tvLastMassage.text = item.lastMessage
            binding.tvTime.text = GetTime.getTime()
            binding.root.setOnClickListener {
                onclick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AdapterChatListBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
    companion object{
        val differ = object: DiffUtil.ItemCallback<ChatListInfo>()
        {
            override fun areContentsTheSame(oldItem: ChatListInfo, newItem: ChatListInfo): Boolean {
                return oldItem.chatRoomId == newItem.chatRoomId
            }

            override fun areItemsTheSame(oldItem: ChatListInfo, newItem: ChatListInfo): Boolean {
                return oldItem == newItem
            }
        }
    }
    private fun getImageData(key : String,imageView: ImageView){

        // Reference to an image file in Cloud Storage
        val storageReference = Firebase.storage.reference.child(key + ".png")

        storageReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(imageView.context)
                .load(uri)
                .into(imageView)
        }.addOnFailureListener {
            // Handle any errors
        }


    }
}