package com.example.taxicompare.home

import android.os.SystemClock.sleep
import androidx.compose.runtime.collectAsState
import com.example.taxicompare.model.Address

import androidx.compose.ui.focus.FocusState
import com.example.taxicompare.api.Request2
import com.example.taxicompare.cache.TripEntity
import com.example.taxicompare.model.Point
import com.example.taxicompare.tripdetail.TripViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun addressFieldHandlers(
    inputSetter: (String) -> Unit,
    addressSetter: (Address?) -> Unit,
    fieldType: FieldType,
    suggestionsSetter: (List<Address>) -> Unit,
    loadingSetter: (Boolean) -> Unit,
    searchJobSetter: (Job?) -> Unit,
    coroutineScope: CoroutineScope,
    getInput: () -> String,
    prevJobProvider: () -> Job?,
    setFocusedField: (FieldType?) -> Unit,
    recentAddresses: List<Address>
) : Pair<(String) -> Unit, (FocusState) -> Unit>
{
    val onValueChange: (String) -> Unit = { value ->
        inputSetter(value)
        addressSetter(null)
        setFocusedField(fieldType)
        if (value.isNotBlank()) {
            loadSuggestions(
                value, coroutineScope,
                onResult = { suggestionsSetter(it); loadingSetter(false) },
                setLoading = { loadingSetter(it) },
                storeJob = { searchJobSetter(it) },
                prevJob = prevJobProvider()
            )
        } else {
            loadRecents(
                recentAddresses = recentAddresses,
                onResult = { suggestionsSetter(it); loadingSetter(false) },
                prevJob = prevJobProvider()
            )
        }
    }
    val onFocusChanged: (FocusState) -> Unit = { f ->
        if (f.isFocused) {
            setFocusedField(fieldType)
            val input = getInput()
            if (input.isNotBlank()) {
                loadSuggestions(
                    input, coroutineScope,
                    onResult = { suggestionsSetter(it); loadingSetter(false) },
                    setLoading = { loadingSetter(it) },
                    storeJob = { searchJobSetter(it) },
                    prevJob = prevJobProvider()
                )
            } else {
                loadRecents(
                    recentAddresses = recentAddresses,
                    onResult = { suggestionsSetter(it); loadingSetter(false) },
                    prevJob = prevJobProvider()
                )
            }
        }
    }
    return onValueChange to onFocusChanged
}

private fun loadSuggestions(
    query: String,
    coroutineScope: CoroutineScope,
    onResult: (List<Address>) -> Unit,
    setLoading: (Boolean) -> Unit,
    storeJob: (Job) -> Unit,
    prevJob: Job?
) {
    setLoading(true)
    prevJob?.cancel()
    val job = coroutineScope.launch {
        delay(1000) // debounce
        val result = Request2(query)
        onResult(result)
        setLoading(false)
    }
    storeJob(job)
}

private fun loadRecents(
    recentAddresses: List<Address>,
    onResult: (List<Address>) -> Unit,
    prevJob: Job?
) {
    prevJob?.cancel()
    onResult(recentAddresses)
}
