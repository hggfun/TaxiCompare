package com.example.taxicompare.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxicompare.api.GetLastAddresses
import com.example.taxicompare.api.Request
import com.example.taxicompare.model.Address
import com.example.taxicompare.model.UserRequest
import com.example.taxicompare.model.Point
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.isNotEmpty

enum class FieldType {
    DEPARTURE, ARRIVAL
}

@Composable
fun AnimatedSearchCard(
    onClick: () -> Unit
) {
    val wordsList = listOf("В Кремль", "На работу", "В университет", "Домой")
    var currentWordIndex by remember { mutableStateOf(0) }
    var visibleLettersCount by remember { mutableStateOf(0) }
    var isAppearing by remember { mutableStateOf(true) }
    val currentWord = wordsList[currentWordIndex]

    LaunchedEffect(currentWordIndex, visibleLettersCount, isAppearing) {
        delay(100L)

        if (isAppearing) {
            if (visibleLettersCount < currentWord.length) {
                visibleLettersCount++
            } else {
                delay(1000L)
                isAppearing = false
            }
        } else {
            if (visibleLettersCount > 0) {
                visibleLettersCount--
            } else {
                currentWordIndex = (currentWordIndex + 1) % wordsList.size
                isAppearing = true
            }
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            onClick = onClick
        ) {
            Text(
                text = "Куда поедем?",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Я здесь",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Left
                )
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentWord.take(visibleLettersCount),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Left
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedCardWithBottomSheet(
    onNavigateToTripDetails: (String, String, UserRequest) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    AnimatedSearchCard(
        onClick = { showSheet = true }
    )

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            modifier = Modifier
                .fillMaxHeight(0.95f)
        ) {
            AddressSelection(onNavigateToTripDetails)
        }
    }
}

@Composable
fun AddressSelection(
    onAddressesSelected: (String, String, UserRequest) -> Unit
) {
    var departureInput by remember { mutableStateOf("") }
    var arrivalInput by remember { mutableStateOf("") }
    var departureAddr by remember { mutableStateOf<Address?>(null) }
    var arrivalAddr by remember { mutableStateOf<Address?>(null) }

    var focusedField by remember { mutableStateOf<FieldType?>(null) }
    var suggestions by remember { mutableStateOf<List<Address>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        delay(200L)
        focusRequester.requestFocus()
    }

    val (onDepartureValueChange, onDepartureFocusChanged) = addressFieldHandlers(
        inputSetter = { departureInput = it },
        addrSetter = { departureAddr = it },
        fieldType = FieldType.DEPARTURE,
        suggestionsSetter = { suggestions = it },
        loadingSetter = { loading = it },
        searchJobSetter = { searchJob = it },
        coroutineScope = coroutineScope,
        getInput = { departureInput },
        prevJobProvider = { searchJob },
        setFocusedField = { focusedField = it }
    )

    val (onArrivalValueChange, onArrivalFocusChanged) = addressFieldHandlers(
        inputSetter = { arrivalInput = it },
        addrSetter = { arrivalAddr = it },
        fieldType = FieldType.ARRIVAL,
        suggestionsSetter = { suggestions = it },
        loadingSetter = { loading = it },
        searchJobSetter = { searchJob = it },
        coroutineScope = coroutineScope,
        getInput = { arrivalInput },
        prevJobProvider = { searchJob },
        setFocusedField = { focusedField = it }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = departureInput,
            onValueChange = onDepartureValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .focusRequester(focusRequester)
                .onFocusChanged(onDepartureFocusChanged),
            singleLine = true,
            label = { Text("Откуда") }
        )
        OutlinedTextField(
            value = arrivalInput,
            onValueChange = onArrivalValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp)
                .onFocusChanged(onArrivalFocusChanged),
            singleLine = true,
            label = { Text("Куда") }
        )

        // Suggestion List (Below both fields)
        if ((focusedField != null) && (loading || suggestions.isNotEmpty())) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (loading) {
                    Row(Modifier.padding(20.dp)) {
                        Spacer(Modifier.width(12.dp))
                        Text("Ищем адреса")
                    }
                } else {
                    suggestions.forEach { address ->
                        Text(
                            /*address.description + ", " + */address.name,
                            modifier = Modifier
                                .clickable {
                                    if (focusedField == FieldType.DEPARTURE) {
                                        departureInput = address.name
                                        departureAddr = address
                                    } else if (focusedField == FieldType.ARRIVAL) {
                                        arrivalInput = address.name
                                        arrivalAddr = address
                                    }
                                    suggestions = emptyList()
                                    loading = false
                                    if (arrivalAddr != null && departureAddr != null) {
                                        onAddressesSelected(
                                            arrivalAddr!!.name,
                                            departureAddr!!.name,
                                            UserRequest(
                                                null,
                                                arrivalAddr!!,
                                                departureAddr!!,
                                                0
                                            )
                                        )
                                    }

                                }
                                .fillMaxWidth()
                                .padding(20.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimatedCardScreen2() {
    AnimatedCardWithBottomSheet(
        onNavigateToTripDetails = {a, b, c -> a + b}
    )
}