package com.example.myecommerce.data.model

data class DataStock(
    val id_user: String,
    val data_stock: List<DataStockItem>
)

data class DataStockItem(
    val id_product: String,
    val stock: Int?
)