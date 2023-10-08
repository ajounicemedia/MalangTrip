package com.example.malangtrip.nav.chat.chatinside

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.malangtrip.key.ChatPageInfo
import com.example.malangtrip.key.UserInfo
import com.example.malangtrip.databinding.AdapterChatMessageBinding
import com.example.malangtrip.nav.chat.chatlist.ChatListAdapter.Companion.differ
import com.example.malangtrip.nav.community.boardscreen.BoardAdapter
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ChatInsideAdapter : ListAdapter<ChatPageInfo, ChatInsideAdapter.ViewHolder>(differ){
        //Chat_Screen에서 정보 받아오기
    var friendItem: UserInfo?=null

    inner class ViewHolder(private val binding: AdapterChatMessageBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: ChatPageInfo)
        {
            if(item.userId==friendItem?.userId)
            {
                friendItem?.userId?.let { getImageData(it,binding.ivProfilePhoto) }
                binding.tvUserName.isVisible = true
                binding.tvUserName.text = friendItem?.nickname
                binding.tvMessage.text = item.message
                binding.tvMessage.gravity = Gravity.START
            }else
            {
                binding.ivProfilePhoto.isVisible=false
                binding.tvUserName.isVisible = false
                binding.tvMessage.text = item.message
                binding.tvMessage.gravity = Gravity.END
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AdapterChatMessageBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
    companion object{
        val differ = object: DiffUtil.ItemCallback<ChatPageInfo>()
        {
            override fun areContentsTheSame(oldItem: ChatPageInfo, newItem: ChatPageInfo): Boolean {
                return oldItem.chatId == newItem.chatId
            }

            override fun areItemsTheSame(oldItem: ChatPageInfo, newItem: ChatPageInfo): Boolean {
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
