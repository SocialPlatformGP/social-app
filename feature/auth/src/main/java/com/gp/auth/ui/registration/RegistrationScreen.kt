package com.gp.auth.ui.registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel,
    onNavigateToLoginScreen: () -> Unit,
    onCreateAccount: () -> Unit
) {
    val state by viewModel.registrationUiState.collectAsState()
    Scaffold { paddingValues ->
        RegistrationContent(
            paddingValues = paddingValues,
            state = state,
            onNavigateToLoginScreen = onNavigateToLoginScreen,
            onCreateAccount = onCreateAccount,
            onEmailChange = { viewModel.onEmailChange(it) },
            onPasswordChange = { viewModel.onPasswordChange(it) },
            onRePasswordChange = { viewModel.rePasswordChange(it) }

        )
    }
}

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
        Text(
            text = "Create Account",
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            fontSize = 36.sp,
            color=Color(0xff222f86)
        )
        OutlinedTextField(
            value = state.email,
            onValueChange = { onEmailChange(it) },
            label = { Text(text = "Email",color=Color(0xff222f86).copy( alpha = 0.7f)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = null,
                    tint = Color(0xff222f86)
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(
            value = state.password,
            onValueChange = { onPasswordChange(it) },
            label = { Text(text = "Password",color=Color(0xff222f86).copy( alpha = 0.7f)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = Color(0xff222f86)
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description, tint = Color(0xff222f86))
                }
            }
        )
        OutlinedTextField(
            value = state.rePassword,
            onValueChange = { onRePasswordChange(it) },
            label = { Text(text = "Re-Password",color=Color(0xff222f86).copy( alpha = 0.7f)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = Color(0xff222f86)
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description, tint = Color(0xff222f86))
                }
            }
        )
        Button(
            onClick = onCreateAccount,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xff222f86),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Create Account",
                fontSize = 18.sp
            )
        }
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Already have an account?",
                modifier = Modifier
                    .padding( end = 8.dp),
                fontSize = 18.sp
            )
            TextButton(
                onClick = onNavigateToLoginScreen,
            ){
                Text(
                    text = "Sign In",
                    fontSize = 18.sp,
                    color = Color(0xff222f86)
                )
            }

        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegistrationScreenPreview() {
    RegistrationContent(
        paddingValues = PaddingValues(),
        state = RegistrationUiState(),
        onNavigateToLoginScreen = {},
        onCreateAccount = {},
        onEmailChange = {},
        onPasswordChange = {},
        onRePasswordChange = {}
    )

}

