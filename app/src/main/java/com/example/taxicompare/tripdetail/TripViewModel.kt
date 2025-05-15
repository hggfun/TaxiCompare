package com.example.taxicompare.tripdetail

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taxicompare.api.GetPricePredict
import com.example.taxicompare.cache.PricePredictionRepository
import com.example.taxicompare.cache.TripEntity
import com.example.taxicompare.cache.TripsRepository
import com.example.taxicompare.model.Address
import com.example.taxicompare.model.ExtendedTripInfo
import com.example.taxicompare.model.UserRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class TripViewModel(
    private val pricePredictionRepository: PricePredictionRepository,
    private val tripsRepository: TripsRepository
) : ViewModel() {
    var prices by mutableStateOf<List<Int>>(emptyList())
        private set
    fun loadPredictions(price: Int) {
        viewModelScope.launch {
            prices = pricePredictionRepository.getPredictCache() { GetPricePredict(this@TripViewModel, price) }
        }
    }

    var request: UserRequest? by mutableStateOf(null)
        private set
    fun setUserRequest(userRequest: UserRequest) {
        request = userRequest
    }
    var extendedTripInfo: ExtendedTripInfo? by mutableStateOf(null)
        private set
    fun updateExtendedTripInfo(tripInfo: ExtendedTripInfo) {
        extendedTripInfo = tripInfo
    }

    private val _trips = MutableStateFlow<List<TripEntity>>(emptyList())
    val trips: StateFlow<List<TripEntity>> = _trips
    fun loadTrips() {
        viewModelScope.launch {
            _trips.value = tripsRepository.getTripsCache()
        }
    }
    fun saveTrip(departure: Address, arrival: Address) {
        viewModelScope.launch {
            tripsRepository.setTripsCache(departure, arrival)
            loadTrips()
        }
    }
}

class TripViewModelFactory(
    private val pricePredictionRepository: PricePredictionRepository,
    private val tripsRepository: TripsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripViewModel(
                pricePredictionRepository,
                tripsRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
