package com.gp.auth.ui.registration

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gp.auth.R
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

@Composable
fun UserInfoScreen(
    viewModel: UserInformationViewModel,
    onContinueClicked: () -> Unit = {},
    onProfileImageClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()




    Scaffold { paddingValues ->
        UserInfoContent(
            paddingValues = paddingValues,
            state = state,
            onFirstNameChange = { viewModel.onFirstNameChange(it) },
            onLastNameChange = { viewModel.onLastNameChange(it) },
            onProfileImageClicked = onProfileImageClicked,
            onPhoneNumberChange = { viewModel.onPhoneNumberChange(it) },
            onBioChange = { viewModel.onBioChange(it) },
            onDateOfBirthChange = { localDate ->
                viewModel.onBirthDateChange(
                    Date.from(
                        localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                    )
                )
            },
            onContinueClicked = onContinueClicked
        )

    }
}

@Composable
fun UserInfoContent(
    paddingValues: PaddingValues,
    state: UserInformationUIState,
    onFirstNameChange: (String) -> Unit = {},
    onLastNameChange: (String) -> Unit = {},
    onProfileImageClicked: () -> Unit = {},
    onPhoneNumberChange: (String) -> Unit = {},
    onDateOfBirthChange: (LocalDate) -> Unit = {},
    onBioChange: (String) -> Unit = {},
    onContinueClicked: () -> Unit = {},
) {
    val dateDialogState = rememberMaterialDialogState()
    var pickedDate by remember { mutableStateOf(LocalDate.now()) }
    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("dd/MM/yyyy")
                .format(pickedDate)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Complete your profile",
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(align = Alignment.CenterHorizontally),
            fontSize = MaterialTheme.typography.h4.fontSize
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .wrapContentWidth(align = Alignment.CenterHorizontally)
                .clickable {
                    onProfileImageClicked()
                }
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(
                        if (state.pfpLocalURI == Uri.EMPTY) R.drawable.baseline_person_24 else state.pfpLocalURI
                    )
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                placeholder = painterResource(id = R.drawable.baseline_person_24)

            )
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .background(
                        color = Color.White,
                        shape = MaterialTheme.shapes.small
                    ),
                tint = MaterialTheme.colors.primary
            )
        }
        Row {
            OutlinedTextField(
                value = state.firstName,
                onValueChange = onFirstNameChange,
                label = { Text(text = "First Name") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
            )
            OutlinedTextField(
                value = state.lastName,
                onValueChange = onLastNameChange,
                label = { Text(text = "Last Name") },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
            )
        }
        OutlinedTextField(
            value = state.phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = { Text(text = "Phone Number") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.PhoneAndroid,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary
                )
            }
        )
        Box(
            modifier = Modifier.clickable {
                dateDialogState.show()
            }
        ) {
            OutlinedTextField(
                value = state.birthDate?.let { SimpleDateFormat("dd/MM/yyyy").format(it) } ?: "",
                onValueChange = {},
                label = { Text(text = "Date of Birth") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clickable {
                        dateDialogState.show()
                    },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary
                    )
                },
                maxLines = 1,
                textStyle = MaterialTheme.typography.body1.copy(fontSize = 14.sp),
                readOnly = true,
                enabled = false
            )
        }



        OutlinedTextField(
            value = state.bio,
            onValueChange = onBioChange,
            label = { Text(text = "About") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary
                )
            },
            maxLines = 3,
            textStyle = MaterialTheme.typography.body1.copy(fontSize = 14.sp)
        )
        Button(
            onClick = onContinueClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(52.dp)
        ) {
            Text(
                text = "Save",
                fontSize = 18.sp,
            )

        }
        MaterialDialog(
            dialogState = dateDialogState,
            buttons = {
                positiveButton(text = "Ok") {
//                    Toast.makeText(context, "Date picked", Toast.LENGTH_SHORT).show()
                }
                negativeButton(text = "Cancel")

            }
        ) {
            datepicker(
                initialDate = LocalDate.now(),
                title = "Select Date",
                allowedDateValidator = { date ->
                    date.isBefore(LocalDate.now())
                },
                onDateChange = { date ->
                    pickedDate = date
                    onDateOfBirthChange(date)
                }
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserInfoScreenPreview() {
    UserInfoContent(
        paddingValues = PaddingValues(),
        state = UserInformationUIState(
            pfpLocalURI = Uri.EMPTY,
        ),

        )

}

private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    return formatter.format(Date(millis))
}
