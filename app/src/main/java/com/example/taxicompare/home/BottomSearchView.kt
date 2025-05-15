package com.example.taxicompare.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.collectAsState
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
import com.example.taxicompare.cache.TripEntity
import com.example.taxicompare.model.Address
import com.example.taxicompare.model.UserRequest
import com.example.taxicompare.model.Point
import com.example.taxicompare.testingdata.GetTariffText
import com.example.taxicompare.testingdata.GetTariffs
import com.example.taxicompare.tripdetail.TripViewModel
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
                .padding(horizontal = 12.dp, vertical = 6.dp),
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
    viewModel: TripViewModel,
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
            AddressSelection(
                viewModel,
                onNavigateToTripDetails
            )
        }
    }
}

@Composable
fun AddressSelection(
    viewModel: TripViewModel,
    onAddressesSelected: (String, String, UserRequest) -> Unit
) {
    var departureInput by remember { mutableStateOf("") }
    var arrivalInput by remember { mutableStateOf("") }
    var departureAddress by remember { mutableStateOf<Address?>(null) }
    var arrivalAddress by remember { mutableStateOf<Address?>(null) }

    var focusedField by remember { mutableStateOf<FieldType?>(null) }
    var suggestions by remember { mutableStateOf<List<Address>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }
    var tariff by remember { mutableStateOf(0) }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        delay(200L)
        focusRequester.requestFocus()
    }

    val recentTrips: List<TripEntity> = viewModel.trips.collectAsState().value
    val recentAddresses: List<Address> = recentTrips
        .flatMap { listOf(it.departure, it.arrival) }  // Flatten all addresses
        .distinct()                                    // Unique addresses only
        .take(5)                                       // Max 5 items
        .toMutableList()

    val (onDepartureValueChange, onDepartureFocusChanged) = addressFieldHandlers(
        inputSetter = { departureInput = it },
        addressSetter = { departureAddress = it },
        fieldType = FieldType.DEPARTURE,
        suggestionsSetter = { suggestions = it },
        loadingSetter = { loading = it },
        searchJobSetter = { searchJob = it },
        coroutineScope = coroutineScope,
        getInput = { departureInput },
        prevJobProvider = { searchJob },
        setFocusedField = { focusedField = it },
        recentAddresses = recentAddresses
    )

    val (onArrivalValueChange, onArrivalFocusChanged) = addressFieldHandlers(
        inputSetter = { arrivalInput = it },
        addressSetter = { arrivalAddress = it },
        fieldType = FieldType.ARRIVAL,
        suggestionsSetter = { suggestions = it },
        loadingSetter = { loading = it },
        searchJobSetter = { searchJob = it },
        coroutineScope = coroutineScope,
        getInput = { arrivalInput },
        prevJobProvider = { searchJob },
        setFocusedField = { focusedField = it },
        recentAddresses = recentAddresses
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

        SelectTariff(
            0,
            { tariff = it }
        )

        if ((focusedField != null) && (loading || suggestions.isNotEmpty())) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (loading) {
                    Row(Modifier.padding(20.dp)) {
                        Spacer(Modifier.width(12.dp))
                        Text("Ищем адреса...")
                    }
                } else {
                    suggestions.forEach { address ->
                        Text(
                            address.description + ", " + address.name,
                            modifier = Modifier
                                .clickable {
                                    if (focusedField == FieldType.DEPARTURE) {
                                        departureInput = address.name
                                        departureAddress = address
                                    } else if (focusedField == FieldType.ARRIVAL) {
                                        arrivalInput = address.name
                                        arrivalAddress = address
                                    }
                                    suggestions = emptyList()
                                    loading = false
                                    if (arrivalAddress != null && departureAddress != null) {
                                        viewModel.saveTrip(
                                            departureAddress!!,
                                            arrivalAddress!!,
                                            tariff
                                        )
                                        onAddressesSelected(
                                            arrivalAddress!!.name,
                                            departureAddress!!.name,
                                            UserRequest(
                                                null,
                                                arrivalAddress!!,
                                                departureAddress!!,
                                                tariff
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTariff(
    selectedTariff: Int,
    onTariffSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var tariffs = GetTariffs()
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(selectedTariff) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 2.dp)
    ) {
        OutlinedTextField(
            value = tariffs[selected],
            onValueChange = {},
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            tariffs.forEachIndexed { index, name ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onTariffSelected(index)
                        expanded = false
                        selected = index
                    }
                )
                HorizontalDivider()
            }
        }
    }
}
