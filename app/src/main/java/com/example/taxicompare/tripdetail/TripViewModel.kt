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
import kotlinx.coroutines.launch

class TripViewModel(
    private val repository: PricePredictionRepository
) : ViewModel() {
    var prices by mutableStateOf<List<Int>>(emptyList())
        private set

    fun loadPredictions() {
        viewModelScope.launch {
            prices = repository.getPredictCache() { GetPricePredict() }
        }
    }
}

class TripViewModelFactory(
    private val repository: PricePredictionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
