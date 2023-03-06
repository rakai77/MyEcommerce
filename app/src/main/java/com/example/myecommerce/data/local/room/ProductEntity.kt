package com.example.myecommerce.data.local.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class ProductEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id : Int? = null,

    @ColumnInfo(name = "name_product")
    val nameProduct : String?,

    @ColumnInfo(name = "image")
    val image : String?,

    @ColumnInfo(name = "price")
    val price : String?,

    @ColumnInfo(name = "quantity")
    val quantity : Int? = 1,

    @ColumnInfo(name = "stock")
    val stock : Int?,

    @ColumnInfo(name = "total_prize")
    val totalPrice : Int? = null,

    @ColumnInfo(name = "is_checked")
    val isChecked: Boolean = false,
)