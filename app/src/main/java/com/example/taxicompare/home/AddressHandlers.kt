package com.example.taxicompare.home

import android.os.SystemClock.sleep
import com.example.taxicompare.model.Address

import androidx.compose.ui.focus.FocusState
import com.example.taxicompare.api.Request2
import com.example.taxicompare.model.Point
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun addressFieldHandlers(
    inputSetter: (String) -> Unit,
    addrSetter: (Address?) -> Unit,
    fieldType: FieldType,
    suggestionsSetter: (List<Address>) -> Unit,
    loadingSetter: (Boolean) -> Unit,
    searchJobSetter: (Job?) -> Unit,
    coroutineScope: CoroutineScope,
    getInput: () -> String,
    prevJobProvider: () -> Job?,
    setFocusedField: (FieldType?) -> Unit
) : Pair<(String) -> Unit, (FocusState) -> Unit>
{
    val onValueChange: (String) -> Unit = { value ->
        inputSetter(value)
        addrSetter(null)
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
                coroutineScope,
                onResult = { suggestionsSetter(it); loadingSetter(false) },
                setLoading = { loadingSetter(it) },
                storeJob = { searchJobSetter(it) },
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
                    coroutineScope,
                    onResult = { suggestionsSetter(it) },
                    setLoading = { loadingSetter(it) },
                    storeJob = { searchJobSetter(it) },
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
    coroutineScope: CoroutineScope,
    onResult: (List<Address>) -> Unit,
    setLoading: (Boolean) -> Unit,
    storeJob: (Job) -> Unit,
    prevJob: Job?
) {
    setLoading(true)
    prevJob?.cancel()
    val job = coroutineScope.launch {
        delay(100) // debounce
        val recentList = GetLastAddresses()
        onResult(recentList)
        setLoading(false)
    }
    storeJob(job)
}

suspend fun GetLastAddresses(): List<Address> {
    sleep(1000)
    return listOf<Address>(
        Address("last",/*"last"*/ Point(1.0, 1.0)),
        Address("one",/*"last"*/ Point(1.0, 1.0)),
        Address("last",/*"last"*/ Point(1.0, 1.0)),
        Address("one",/*"last"*/ Point(1.0, 1.0)),
        Address("last",/*"last"*/ Point(1.0, 1.0))
    )
}
