package com.example.myecommerce.di

import android.content.Context
import androidx.room.Room
import com.example.myecommerce.data.local.room.ProductDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideProductDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        ProductDatabase::class.java,
        "product_db"
    ).allowMainThreadQueries().build()

    @Singleton
    @Provides
    fun provideProductDao(database: ProductDatabase) = database.productDatabaseDao()

}