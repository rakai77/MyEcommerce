package com.example.myecommerce.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myecommerce.data.response.ImageProductItem
import com.example.myecommerce.databinding.ItemImageSliderBinding

class ImageViewPagerAdapter(private val imageList: List<ImageProductItem>) :
    RecyclerView.Adapter<ImageViewPagerAdapter.ImageViewHolder>() {
    class ImageViewHolder(val binding: ItemImageSliderBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(imageProductItem: ImageProductItem) {
            binding.apply {
                Glide.with(itemView)
                    .load(imageProductItem.imageProduct)
                    .into(tvItemImageSlider)
                tvItemImgTitle.text = imageProductItem.titleProduct
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageList[position])
    }
}