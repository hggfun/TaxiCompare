package com.example.taxicompare.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [PricePredictionEntity::class], version = 1)
@TypeConverters(PriceListConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pricePredictionDao(): PricePredictionDao
}
