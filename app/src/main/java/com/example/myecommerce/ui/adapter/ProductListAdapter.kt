package com.example.myecommerce.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myecommerce.data.response.DataItem
import com.example.myecommerce.databinding.ItemListProductBinding
import com.example.myecommerce.utils.Utils.currency
import com.example.myecommerce.utils.Utils.formattingDate

class ProductListAdapter(private val onClickItem: (Int) -> Unit) : ListAdapter<DataItem, ProductListAdapter.ProductListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val binding = ItemListProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ProductListViewHolder(val binding: ItemListProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataItem) {
            with(binding) {
                iconFavorite.visibility = View.GONE

                Glide.with(itemView.context)
                    .load(item.image)
                    .into(ivProductImage)
                tvProductName.isSelected = true
                tvProductName.text = item.nameProduct
                tvProductPrize.text = item.harga.currency()
                tvItemRating.rating = item.rate.toFloat()
                tvDate.text = formattingDate(item.date)

                itemView.setOnClickListener {
                    onClickItem.invoke(item.id)
                }
            }
        }
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