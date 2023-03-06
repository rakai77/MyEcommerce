package com.example.myecommerce.ui.adapter.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myecommerce.data.response.DataItem
import com.example.myecommerce.databinding.ItemListProductBinding
import com.example.myecommerce.utils.Utils
import com.example.myecommerce.utils.Utils.currency

class ProductPagingAdapter(private val onClick: (Int) -> Unit) : PagingDataAdapter<DataItem, ProductPagingAdapter.PagingViewHolder>(DIFF_CALLBACK) {

    inner class PagingViewHolder(val binding: ItemListProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(result: DataItem) {
            binding.apply {
                Glide.with(itemView).load(result.image).into(ivProductImage)
                iconFavorite.visibility = View.GONE
                tvProductName.isSelected = true
                tvProductName.text = result.nameProduct
                tvProductPrize.text = result.harga.currency()
                tvItemRating.rating = result.rate.toFloat()
                tvDate.text = Utils.formattingDate(result.date)
                itemView.setOnClickListener {
                    onClick.invoke(result.id)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: PagingViewHolder, position: Int) {
        val result = getItem(position)
        if (result != null) {
            holder.bind(result)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingViewHolder {
        val binding = ItemListProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagingViewHolder(binding)
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<DataItem> =
            object : DiffUtil.ItemCallback<DataItem>() {
                override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                    return oldItem.nameProduct == newItem.nameProduct
                }

                override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                    return oldItem == newItem
                }
            }
    }
}

