package com.example.malangtrip.unusedcode.user

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.malangtrip.databinding.AdapterUserBinding
import com.example.malangtrip.key.UserInfo
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class UserAdapter(private val onClick:(UserInfo)->Unit) : ListAdapter<UserInfo, UserAdapter.ViewHolder>(
    differ
){

    inner class ViewHolder(private val binding: AdapterUserBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(item: UserInfo)
        {
            item?.userId?.let { getImageData(it,binding.ivProfilePhoto) }
            binding.tvNickname.text = item.nickname
            binding.tvDes.text = item.description

            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AdapterUserBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object{
        val differ = object: DiffUtil.ItemCallback<UserInfo>()
        {
            override fun areContentsTheSame(oldItem: UserInfo, newItem: UserInfo): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areItemsTheSame(oldItem: UserInfo, newItem: UserInfo): Boolean {
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