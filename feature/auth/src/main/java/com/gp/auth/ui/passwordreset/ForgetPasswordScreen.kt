package com.gp.auth.ui.passwordreset

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gp.socialapp.theme.AppTheme

@Composable
fun ForgetPasswordScreen(
    viewModel: PasswordResetViewModel,
    onSendResetEmail: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    androidx.compose.material3.Scaffold { paddingValues ->
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
    state: PasswordResetUiState = PasswordResetUiState(),
    onEmailChange: (String) -> Unit = {},
    onSendResetEmail: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        androidx.compose.material3.Text(
            text = "Reset your password",
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(
                    Alignment.CenterHorizontally
                ),
            style = MaterialTheme.typography.h4,
            maxLines = 1,
        )
        androidx.compose.material3.OutlinedTextField(
            shape = RoundedCornerShape(32.dp),
            value = state.email,
            onValueChange = { onEmailChange(it) },
            label = { androidx.compose.material3.Text(text = "Email",) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            singleLine = true,
            leadingIcon = {
                androidx.compose.material3.Icon(imageVector = Icons.Filled.Email, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        androidx.compose.material3.Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .height(54.dp),
            shape = RoundedCornerShape(32.dp),
            onClick = onSendResetEmail,
            colors = ButtonDefaults.buttonColors(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
            )
        ) {
            androidx.compose.material3.Icon(imageVector = Icons.Filled.Send , contentDescription =null )
            Spacer(modifier = Modifier.width(16.dp))
            androidx.compose.material3.Text(
                text = "Send Reset Email",
                fontSize = 16.sp
            )

        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgetPasswordScreenPreview() {
    AppTheme {
        Surface {
            ForgetPasswordContent()
        }
    }
}
@Preview(showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun ForgetPasswordScreenPreviewNight() {
    AppTheme {
        Surface {
            ForgetPasswordContent()
        }
    }
}