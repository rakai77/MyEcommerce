package com.example.myecommerce.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ProductEntity::class], version = 1, exportSchema = false)

abstract class ProductDatabase : RoomDatabase() {

    abstract fun productDatabaseDao(): ProductDao
}