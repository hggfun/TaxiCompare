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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.taxicompare.model.Address
import com.example.taxicompare.model.UserRequest
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.delay

enum class FieldType {
    DEPARTURE, ARRIVAL
}

fun attemptNavigation(
    departureText: String,
    arrivalText: String,
    keyboardController: SoftwareKeyboardController?,
    onNavigateToTripDetails: (String, String, UserRequest) -> Unit
) {
    if (departureText.isNotBlank() && arrivalText.isNotBlank()) {
        keyboardController?.hide()
        // TODO normal user request
        val userRequest = UserRequest(
            location = Point(1.0, 1.0),
            departure = Address("test_name", Point(55.751591, 37.714939)),
            arrival = Address("test_name", Point(55.753975, 37.648425)),
            tariff = 0
        )
        onNavigateToTripDetails(departureText, arrivalText, userRequest)
    }
}

@Composable
fun DepartureArrivalCard(
    departureText: String,
    arrivalText: String,
    onFocusChange: (FieldType) -> Unit,
    onDepartureTextChange: (String) -> Unit,
    onArrivalTextChange: (String) -> Unit,
    onDepartureMapClick: () -> Unit,
    onArrivalMapClick: () -> Unit,
    onDonePressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LocationInputField(
                hint = "Departure",
                text = departureText,
                onFocusChange = { onFocusChange(FieldType.DEPARTURE) },
                onTextChange = onDepartureTextChange,
                onMapClick = onDepartureMapClick,
                onDonePressed = onDonePressed
            )

            Spacer(modifier = Modifier.height(4.dp))

            LocationInputField(
                hint = "Arrival",
                text = arrivalText,
                onFocusChange = { onFocusChange(FieldType.ARRIVAL) },
                onTextChange = onArrivalTextChange,
                onMapClick = onArrivalMapClick,
                onDonePressed = onDonePressed,
                autofocus = true
            )
        }
    }
}

@Composable
fun RecentAddressCard(
    recentAddresses: List<String>,
    onAddressClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (recentAddresses.isEmpty()) return

    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = "Последние поездки",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )
        recentAddresses.take(6).forEachIndexed { index, address ->
            if (index > 0) {
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            }
            Text(
                text = address,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAddressClick(address) }
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun LocationInputField(
    hint: String,
    text: String,
    onFocusChange: () -> Unit,
    onTextChange: (String) -> Unit,
    onMapClick: () -> Unit,
    onDonePressed: () -> Unit,
    autofocus: Boolean = false
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                if (it.isFocused) {
                    onFocusChange()
                }
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            TextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text(hint, color = Color.Gray.copy(alpha = 0.7f)) },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        onDonePressed()
                    }
                )
            )

            Text(
                text = "on map",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .padding(end = 12.dp)
                    .clickable { onMapClick() }
            )
        }
    }

    LaunchedEffect(autofocus) {
        if (autofocus) {
            focusRequester.requestFocus()
            delay(200L)
            keyboardController?.show()
        }
    }
}

@Composable
fun AnimatedSearchCard(
    onClick: () -> Unit
) {
    val wordsList = listOf("Кремль", "Работа", "Университет", "Домой")
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
    val keyboardController = LocalSoftwareKeyboardController.current


    var departureText by remember { mutableStateOf("") }
    var arrivalText by remember { mutableStateOf("") }
    var focusedField by remember { mutableStateOf(FieldType.ARRIVAL) }
    var showSheet by remember { mutableStateOf(false) }

    AnimatedSearchCard(
        onClick = { showSheet = true }
    )

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            modifier = Modifier
                .fillMaxHeight(0.8f)
        ) {
            DepartureArrivalCard(
                departureText = departureText,
                arrivalText = arrivalText,
                onFocusChange = { fieldType -> focusedField = fieldType },
                onDepartureTextChange = { departureText = it },
                onArrivalTextChange = { arrivalText = it },
                onDepartureMapClick = {
                    // TODO: later implement choosing departure location from map
                },
                onArrivalMapClick = {
                    // TODO: later implement choosing arrival location from map
                },
                onDonePressed = { attemptNavigation(departureText, arrivalText, keyboardController, onNavigateToTripDetails) },
                modifier = Modifier.fillMaxWidth()
            )

            RecentAddressCard(
                recentAddresses = GetLastAddresses(),
                onAddressClick = { address ->
                    if (focusedField == FieldType.DEPARTURE) {
                        departureText = address
                    } else {
                        arrivalText = address
                    }
                }
            )
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