package com.example.taxicompare.cache

import com.example.taxicompare.model.Address
import com.google.gson.Gson

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.TypeConverters


class TripsRepository(private val dao: TripsDao) {
    suspend fun getTripsCache(): List<TripEntity> = dao.getTripsCache()

    suspend fun setTripsCache(departure: Address, arrival: Address) {
        dao.setTripsCache(TripEntity(departure = departure, arrival = arrival))
    }
}

@Entity(tableName = "trips")
@TypeConverters(AddressConverters::class)
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val departure: Address,
    val arrival: Address
)

class AddressConverters {
    @TypeConverter
    fun fromAddress(address: Address): String = Gson().toJson(address)

    @TypeConverter
    fun toAddress(json: String): Address = Gson().fromJson(json, Address::class.java)
}

@Dao
interface TripsDao {
    @Query("SELECT * FROM trips ORDER BY id DESC")
    suspend fun getTripsCache(): List<TripEntity>

    @Insert
    suspend fun setTripsCache(trip: TripEntity)
}
