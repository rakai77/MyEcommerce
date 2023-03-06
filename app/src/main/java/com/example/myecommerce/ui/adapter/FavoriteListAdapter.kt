package com.example.myecommerce.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myecommerce.data.response.DataItemFavorite
import com.example.myecommerce.databinding.ItemListProductBinding
import com.example.myecommerce.utils.Utils.currency
import com.example.myecommerce.utils.Utils.formattingDate

class FavoriteListAdapter(private val onClickItem: (Int) -> Unit) : ListAdapter<DataItemFavorite, FavoriteListAdapter.FavoriteListViewHolder>(DIFF_CALLBACK) {

    inner class FavoriteListViewHolder(val binding: ItemListProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataItemFavorite) {
            with(binding) {
                iconFavorite.visibility = View.GONE

                Glide.with(itemView.context)
                    .load(item.image)
                    .into(ivProductImage)
                tvProductName.isSelected = true
                tvProductName.text = item.nameProduct
                tvProductPrize.text = item.harga.currency()
                tvItemRating.rating = item.rate.toFloat()
                tvDate.text = formattingDate( item.date)
                iconFavorite.visibility = View.VISIBLE

                itemView.setOnClickListener {
                    onClickItem.invoke(item.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteListViewHolder {
        val binding = ItemListProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<DataItemFavorite> =
            object : DiffUtil.ItemCallback<DataItemFavorite>() {
                override fun areItemsTheSame(
                    oldItem: DataItemFavorite,
                    newItem: DataItemFavorite
                ): Boolean {
                    return oldItem.nameProduct == newItem.nameProduct
                }

                override fun areContentsTheSame(
                    oldItem: DataItemFavorite,
                    newItem: DataItemFavorite
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}