package com.gp.auth.ui.registration

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gp.auth.R
import com.gp.socialapp.theme.AppTheme
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@Composable
fun UserInfoScreen(
    viewModel: UserInformationViewModel,
    onContinueClicked: () -> Unit = {},
    onProfileImageClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()




    androidx.compose.material3.Scaffold(

    ) { paddingValues ->
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.material3.Text(
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
            androidx.compose.material3.Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
//                    .background(
//                        color = Color.White,
//                        shape = MaterialTheme.shapes.small
//                    ),
//                tint = MaterialTheme.colors.primary
            )
        }
        Row {
            androidx.compose.material3.OutlinedTextField(
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
                ),
                shape = RoundedCornerShape(32.dp),

                value = state.firstName,
                onValueChange = onFirstNameChange,
                label = {
                    androidx.compose.material3.Text(
                        text = "First Name",
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer.copy(
                            alpha = 0.7f
                        )

                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
            )
            androidx.compose.material3.OutlinedTextField(
                shape = RoundedCornerShape(32.dp),
                value = state.lastName,
                onValueChange = onLastNameChange,
                label = {
                    androidx.compose.material3.Text(
                        text = "Last Name",
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer.copy(
                            alpha = 0.7f
                        )

                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
            )
        }
        androidx.compose.material3.OutlinedTextField(
            shape = RoundedCornerShape(32.dp),

            value = state.phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = {
                androidx.compose.material3.Text(
                    text = "Phone Number",
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer.copy(
                        alpha = 0.7f
                    )

                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            leadingIcon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.PhoneAndroid,
                    contentDescription = null,
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        )
        Box(
            modifier = Modifier.clickable {
                dateDialogState.show()
            }
        ) {
            androidx.compose.material3.OutlinedTextField(
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
                ),
                shape = RoundedCornerShape(32.dp),

                value = state.birthDate?.let { SimpleDateFormat("dd/MM/yyyy").format(it) } ?: "",
                onValueChange = {},
                label = {
                    androidx.compose.material3.Text(
                        text = "Date of Birth",
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer.copy(
                            alpha = 0.7f
                        )

                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clickable {
                        dateDialogState.show()
                    },
                leadingIcon = {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = null,
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                maxLines = 1,
                textStyle = MaterialTheme.typography.body1.copy(fontSize = 14.sp),
                readOnly = true,
//                enabled = false
            )
        }



        androidx.compose.material3.OutlinedTextField(
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
            ),
            shape = RoundedCornerShape(32.dp),

            value = state.bio,
            onValueChange = onBioChange,
            label = {
                androidx.compose.material3.Text(
                    text = "About",
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer.copy(
                        alpha = 0.7f
                    )

                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            leadingIcon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            maxLines = 3,
            textStyle = MaterialTheme.typography.body1.copy(fontSize = 14.sp)
        )
        androidx.compose.material3.Button(
            shape = RoundedCornerShape(32.dp),

            onClick = onContinueClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
            )
        ) {
            androidx.compose.material3.Text(
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
    AppTheme {
        Surface {
            UserInfoContent(
                paddingValues = PaddingValues(),
                state = UserInformationUIState(
                    pfpLocalURI = Uri.EMPTY,
                ),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun UserInfoScreenPreviewNight() {
    AppTheme {
        Surface {
            UserInfoContent(
                paddingValues = PaddingValues(),
                state = UserInformationUIState(
                    pfpLocalURI = Uri.EMPTY,
                ),
            )
        }
    }
}


