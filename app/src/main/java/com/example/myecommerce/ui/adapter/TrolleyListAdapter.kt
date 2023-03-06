package com.example.myecommerce.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myecommerce.data.local.room.ProductEntity
import com.example.myecommerce.databinding.ItemListTrolleyBinding
import com.example.myecommerce.utils.Utils.currency

class TrolleyListAdapter(
    private val onAddItem: (ProductEntity) -> Unit,
    private val onDeleteItem: (ProductEntity) -> Unit,
    private val onMinItem: (ProductEntity) -> Unit,
    private val onCheckedItem: (ProductEntity) -> Unit
) : ListAdapter<ProductEntity, TrolleyListAdapter.TrolleyListViewHolder>(DIFF_CALLBACK) {

    inner class TrolleyListViewHolder(val binding: ItemListTrolleyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductEntity) {
            with(binding) {
                Glide.with(itemView.context).load(item.image).into(ivProductTrolley)
                tvProductNameTrolley.text = item.nameProduct
                tvProductPriceTrolley.text = item.price?.currency()
                tvQuantityTrolley.text = item.quantity.toString()
                cbTrolleyList.isChecked = item.isChecked
                btnDeleteTrolley.setOnClickListener {
                    onDeleteItem.invoke(item)
                }
                btnIncrementTrolley.setOnClickListener {
                    val stock = item.stock
                    val quantity = item.quantity
                    if (stock == quantity) {
                        btnIncrementTrolley.isClickable = false
                        Toast.makeText(itemView.context, "Out of stock.", Toast.LENGTH_SHORT).show()
                    } else{
                        onAddItem.invoke(item)
                    }

                    Log.d("check btn add trolley", item.quantity.toString())
                }
                btnTrolleyDecrement.setOnClickListener {
                    val quantity = item.quantity
                    if (quantity != 1) {
                        onMinItem.invoke(item)
                    } else {
                        btnTrolleyDecrement.isClickable = false
                    }
                    Log.d("check btn min trolley", item.quantity.toString())
                }
                cbTrolleyList.setOnClickListener {
                    onCheckedItem.invoke(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrolleyListViewHolder {
        val binding = ItemListTrolleyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrolleyListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrolleyListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<ProductEntity> =
            object : DiffUtil.ItemCallback<ProductEntity>() {
                override fun areItemsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean {
                    return oldItem.nameProduct == newItem.nameProduct
                }

                override fun areContentsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }

}