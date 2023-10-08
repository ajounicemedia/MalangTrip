package com.example.malangtrip.nav.community.boardscreen

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.malangtrip.key.CommunityItem
import com.example.malangtrip.R
import com.example.malangtrip.databinding.AdapterBoardListBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class BoardAdapter(private val everyBoardList: MutableList<CommunityItem>,private val onclick: (CommunityItem) ->Unit) : RecyclerView.Adapter<BoardAdapter.ViewHolder>()
{

    var myBoardInfo: MutableList<CommunityItem> = everyBoardList
    class ViewHolder(private val binding: AdapterBoardListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item:CommunityItem,onclick: (CommunityItem) ->Unit)
        {
            binding.tvWritingTitle.text=item.title
            binding.tvWritingContent.text=item.content
            binding.tvNickname.text=item.userName
            binding.tvTime.text=item.time+" / 댓글 수" +item.commentNum+"개"
            binding.root.setOnClickListener {
                onclick(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AdapterBoardListBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(myBoardInfo[position],onclick)
    }

    override fun getItemCount(): Int {
        return myBoardInfo.size
    }
}