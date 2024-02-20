package com.gp.auth.ui.registration

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gp.posts.fontFamily
import com.gp.socialapp.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel,
    onNavigateToLoginScreen: () -> Unit,
    onCreateAccount: () -> Unit
) {
    val state by viewModel.registrationUiState.collectAsState()
    RegistrationScreen(
        state = state,
        onNavigateToLoginScreen = onNavigateToLoginScreen,
        onCreateAccount = onCreateAccount,
        onEmailChange = { viewModel.onEmailChange(it) },
        onPasswordChange = { viewModel.onPasswordChange(it) },
        onRePasswordChange = { viewModel.rePasswordChange(it) }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    state: RegistrationUiState,
    onNavigateToLoginScreen: () -> Unit,
    onCreateAccount: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRePasswordChange: (String) -> Unit
) {
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = {
                },
                navigationIcon = {
                    androidx.compose.material3.IconButton(
                        modifier = Modifier.padding(
                            start = 8.dp,
                            top = 16.dp

                        ),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        onClick = onNavigateToLoginScreen
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        RegistrationContent(
            paddingValues = paddingValues,
            state = state,
            onNavigateToLoginScreen = onNavigateToLoginScreen,
            onCreateAccount = onCreateAccount,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            onRePasswordChange = onRePasswordChange

        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationContent(
    paddingValues: PaddingValues,
    state: RegistrationUiState,
    onNavigateToLoginScreen: () -> Unit,
    onCreateAccount: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRePasswordChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        var passwordVisible by remember { mutableStateOf(false) }
        androidx.compose.material3.Text(
            text = "Sign Up",
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.Start),
            fontSize = 36.sp,
            fontFamily = fontFamily,
        )
        androidx.compose.material3.OutlinedTextField(
            shape = RoundedCornerShape(32.dp),
            value = state.email,
            onValueChange = { onEmailChange(it) },
            label = {
                androidx.compose.material3.Text(
                    text = "Email",
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            leadingIcon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        androidx.compose.material3.OutlinedTextField(
            shape = RoundedCornerShape(32.dp),
            value = state.password,
            onValueChange = { onPasswordChange(it) },
            label = {
                androidx.compose.material3.Text(
                    text = "Password",
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            leadingIcon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Hide password" else "Show password"

                androidx.compose.material3.IconButton(onClick = {
                    passwordVisible = !passwordVisible
                }) {
                    androidx.compose.material3.Icon(
                        imageVector = image, description,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        )
        androidx.compose.material3.OutlinedTextField(
            shape = RoundedCornerShape(32.dp),
            value = state.rePassword,
            onValueChange = { onRePasswordChange(it) },
            label = {
                androidx.compose.material3.Text(
                    text = "Re-Password",
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            leadingIcon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Hide password" else "Show password"

                androidx.compose.material3.IconButton(onClick = {
                    passwordVisible = !passwordVisible
                }) {
                    androidx.compose.material3.Icon(
                        imageVector = image, description,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        )
        androidx.compose.material3.Button(
            shape = RoundedCornerShape(32.dp),
            onClick = onCreateAccount,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
            ),
        ) {
            androidx.compose.material3.Text(
                text = "Create Account",
                fontSize = 18.sp
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Text(
                text = "Already have an account?",
                modifier = Modifier
                    .padding(end = 8.dp),
                fontSize = 18.sp
            )
            androidx.compose.material3.TextButton(
                onClick = onNavigateToLoginScreen,
            ) {
                androidx.compose.material3.Text(
                    text = "Sign In",
                    fontSize = 18.sp,
                )
            }

        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegistrationScreenPreview() {
    AppTheme {
        Surface {
            RegistrationScreen(
                state = RegistrationUiState(),
                onNavigateToLoginScreen = {},
                onCreateAccount = {},
                onEmailChange = {},
                onPasswordChange = {},
                onRePasswordChange = {}
            )
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun RegistrationScreenPreviewNight() {
    AppTheme {
        Surface {
            RegistrationScreen(

                state = RegistrationUiState(),
                onNavigateToLoginScreen = {},
                onCreateAccount = {},
                onEmailChange = {},
                onPasswordChange = {},
                onRePasswordChange = {}
            )
        }
    }
}
