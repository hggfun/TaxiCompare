package com.example.taxicompare.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [PricePredictionEntity::class, TripEntity::class], version = 3)
@TypeConverters(PriceListConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pricePredictionDao(): PricePredictionDao
    abstract fun tripsDao(): TripsDao
}
