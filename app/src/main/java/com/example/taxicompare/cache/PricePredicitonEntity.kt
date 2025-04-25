package com.example.taxicompare.cache

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.*


class PricePredictionRepository(
    private val dao: PricePredictionDao
) {

    private val CACHE_LIFETIME = 5 * 60 * 1000L // 5 min in millis

    suspend fun getPredictCache(
        fetchLambda: suspend () -> List<Int>
    ): List<Int> {
        val timestamp = System.currentTimeMillis() + CACHE_LIFETIME
        val cache = dao.getByTimestamp(timestamp)
        if (cache != null) {
            return Gson().fromJson(cache.prices, object : TypeToken<List<Int>>() {}.type)
        }
        val prices = fetchLambda()
        dao.insert(
            PricePredictionEntity(
                prices = Gson().toJson(prices),
                timestamp = System.currentTimeMillis()
            )
        )
        dao.deleteByTimestamp(timestamp)
        return prices
    }
}

@Entity(tableName = "price_prediction")
@TypeConverters(PriceListConverters::class)
data class PricePredictionEntity(
    @PrimaryKey
    val timestamp: Long,
    val prices: String
)


class PriceListConverters {
    @TypeConverter
    fun fromPricesList(value: List<Int>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toPricesList(value: String): List<Int> {
        val listType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(value, listType)
    }
}

@Dao
interface PricePredictionDao {
    @Query("SELECT * FROM price_prediction WHERE timestamp > :timestamp LIMIT 1")
    suspend fun getByTimestamp(timestamp: Long): PricePredictionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prediction: PricePredictionEntity)

    @Query("DELETE FROM price_prediction WHERE timestamp < :timestamp")
    suspend fun deleteByTimestamp(timestamp: Long)
}
