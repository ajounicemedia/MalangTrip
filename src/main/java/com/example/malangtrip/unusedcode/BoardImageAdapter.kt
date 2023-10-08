package com.example.malangtrip.unusedcode

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.malangtrip.R
//복수의 이미지 사용할 때
class BoardImageAdapter(private val imageList: List<String>) : RecyclerView.Adapter<BoardImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_board_img)
//        init {
//            imageView.setOnClickListener {
//                val context = itemView.context
//                val imageUrl = imageList[adapterPosition]
//
//                // Create a Dialog
//                val builder = AlertDialog.Builder(context)
//                val dialogView = LayoutInflater.from(context).inflate(R.layout.n_community_image_dialog, null)
//
//                val imageViewExpanded: ImageView = dialogView.findViewById(R.id.imageViewExpanded)
//
//
//                Glide.with(context)
//                    .load(imageUrl)
//                    .into(imageViewExpanded)
//
//
//                builder.setView(dialogView)
//                    .setPositiveButton("뒤로가기") { dialog, _ ->
//                        dialog.dismiss()
//                    }.setCancelable(true)
//
//
//
//                builder.setView(dialogView)
//                    .setNegativeButton("다운로드") { dialog, _ ->
//                        downloadImage(context, imageUrl)
//                    }
//                builder.create().show()
//            }
//            }
        }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adatper_board_image, parent, false)
        return ImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageList[position]
        Glide.with(holder.imageView.context)
            .load(imageUrl)
            .into(holder.imageView)
    }

    override fun getItemCount() = imageList.size
    private fun downloadImage(context: Context, imageUrl: String) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(imageUrl)
        val request = DownloadManager.Request(downloadUri)

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setTitle("Downloading")
            .setDescription("Downloading image...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${System.currentTimeMillis()}")
            .setAllowedOverMetered(true)

        downloadManager.enqueue(request)
    }
}