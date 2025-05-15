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
import androidx.navigation.NavController
import com.example.taxicompare.api.GetPricePredict
import com.example.taxicompare.cache.PricePredictionRepository
import com.example.taxicompare.cache.TripEntity
import com.example.taxicompare.cache.TripsRepository
import com.example.taxicompare.model.Address
import com.example.taxicompare.model.ExtendedTripInfo
import com.example.taxicompare.model.UserRequest
import com.example.taxicompare.navigation.BottomNavItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class TripViewModel(
    private val navController: NavController,
    private val pricePredictionRepository: PricePredictionRepository,
    private val tripsRepository: TripsRepository
) : ViewModel() {
    fun getNavController(): NavController {
        return navController
    }

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

    private val _selectedNavItem = MutableStateFlow<BottomNavItem>(BottomNavItem.Taxi)
    val selectedNavItem = _selectedNavItem.asStateFlow()
    fun setNavItem(item: BottomNavItem) {
        _selectedNavItem.value = item
    }
}

class TripViewModelFactory(
    private val navController: NavController,
    private val pricePredictionRepository: PricePredictionRepository,
    private val tripsRepository: TripsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripViewModel(
                navController,
                pricePredictionRepository,
                tripsRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
