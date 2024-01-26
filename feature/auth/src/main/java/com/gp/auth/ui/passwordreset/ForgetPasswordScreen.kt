package com.gp.auth.ui.passwordreset

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Shapes
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ForgetPasswordScreen(
    viewModel: PasswordResetViewModel,
    onSendResetEmail: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    Scaffold {paddingValues ->
        ForgetPasswordContent(
            modifier = Modifier.padding(paddingValues),
            state = state,
            onEmailChange = { viewModel.onEmailChange(it) },
            onSendResetEmail = onSendResetEmail
        )

    }

}

@Composable
fun ForgetPasswordContent(
    modifier: Modifier = Modifier,
    state : PasswordResetUiState= PasswordResetUiState(),
    onEmailChange: (String) -> Unit={},
    onSendResetEmail: () -> Unit = {}
) {
    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
    ){
        Text(
            text ="Reset your password",
            modifier= Modifier
                .fillMaxWidth()
                .wrapContentWidth(
                    Alignment.CenterHorizontally
                ),
            style = MaterialTheme.typography.h4,
            maxLines = 1,
        )
        OutlinedTextField(
            value =state.email ,
            onValueChange ={onEmailChange(it)},
            label = { Text(text = "Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            singleLine = true,
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Email, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
    .height(50.dp)
            ,
            shape = RoundedCornerShape(5.dp),
            onClick = onSendResetEmail
        ) {
            Text(
                text = "Send Reset Email",
                fontSize = 16.sp
            )
            
        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgetPasswordScreenPreview() {
    ForgetPasswordContent()

}