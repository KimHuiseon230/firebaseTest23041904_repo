package com.example.firebasetest23041904

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebasetest23041904.databinding.PictureLayoutBinding

class PictureAdapter(val context: Context, val pictureList: MutableList<PictureData>) :
    RecyclerView.Adapter<PictureAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding =PictureLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CustomViewHolder(binding)
    }

    override fun getItemCount(): Int = pictureList.size

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val pictureData = pictureList.get(position)
        val binding = (holder as CustomViewHolder).binding
        binding.tvEmail.text = pictureData.email
        binding.tvDate.text = pictureData.date
        binding.tvContent.text = pictureData.content
        // 이미지는 로드가 되지 않았고 파이어베이스에 저장된 스토리지 이미지명 만 가져온 상태(pictureData.docID)
        val pictureDAO = PictureDAO()
        val pictureRef = pictureDAO.storage!!.reference.child("images/${pictureData.docID}.png")
        pictureRef.downloadUrl.addOnCompleteListener {
            if (it.isSuccessful){
                Glide.with(context).load(it.result).into(binding.ivPicture)
            }
        }
    }
    class CustomViewHolder(val binding: PictureLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}