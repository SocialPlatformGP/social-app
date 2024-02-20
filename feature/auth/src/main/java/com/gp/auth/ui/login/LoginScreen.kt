package com.gp.auth.ui.login

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gp.auth.R
import com.gp.posts.fontFamily
import com.gp.socialapp.theme.AppTheme

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    navigateToSignUp: () -> Unit,
    navigateToForgotPassword: () -> Unit,
    onSignInWithGoogle: () -> Unit,
    onSignInClicked: () -> Unit
) {
    val state by viewModel.loginStateFlow.collectAsState()
    androidx.compose.material3.Scaffold (
    ){ paddingValues ->
        LoginContent(
            paddingValues = paddingValues,
            onSignInWithGoogle = {
                onSignInWithGoogle()
            },
            state = state,
            navigateToSignUp = navigateToSignUp,
            navigateToForgotPassword = navigateToForgotPassword,
            onEmailChange = { viewModel.updateEmail(it) },
            onPasswordChange = { viewModel.updatePassword(it) },
            onSignIn = onSignInClicked

        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    paddingValues: PaddingValues,
    onSignInWithGoogle: () -> Unit,
    onSignIn: () -> Unit,
    state: LoginUiState,
    navigateToSignUp: () -> Unit,
    navigateToForgotPassword: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ,
        verticalArrangement = Arrangement.Center,
    ) {

        var passwordVisible by remember { mutableStateOf(false) }

//        Icon(
//            modifier = Modifier
//                                .fillMaxWidth()
//                .size(300.dp)
//
//                .wrapContentWidth(Alignment.CenterHorizontally),
//            painter = painterResource(id = R.drawable.logolight),
//            contentDescription = null,
//            tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
//        )

        androidx.compose.material3.Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.Start)
                .padding(16.dp),
            text = "Login",
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = fontFamily,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
        )


            androidx.compose.material3.OutlinedTextField(
            shape = RoundedCornerShape(32.dp),
            value = state.email,
            onValueChange = { onEmailChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            label = {
                androidx.compose.material3.Text(
                    text = "Email",
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                    },
            leadingIcon = {
                androidx.compose.material3.Icon(
                    Icons.Filled.Email,
                    contentDescription = null,
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = state.emailError.isNotEmpty(),
        )
        if (state.emailError.isNotEmpty()) {
            androidx.compose.material3.Text(
                text = state.emailError,
                color = MaterialTheme.colors.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp),
            )
        }

        androidx.compose.material3.OutlinedTextField(
            value = state.password,
            onValueChange = { onPasswordChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            label = { androidx.compose.material3.Text(text = "Password", color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            ) },
                        colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
            ),
            leadingIcon = {
                androidx.compose.material3.Icon(
                    Icons.Filled.Lock, contentDescription = null,
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Hide password" else "Show password"

                androidx.compose.material3.IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    androidx.compose.material3.Icon(
                        imageVector = image,
                        description,
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            },
            isError = state.passwordError.isNotEmpty(),
            shape = RoundedCornerShape(32.dp)

            )
        if (state.passwordError.isNotEmpty()) {
            androidx.compose.material3.Text(
                text = state.passwordError,
                color = MaterialTheme.colors.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }


        androidx.compose.material3.TextButton(
            onClick = navigateToForgotPassword,
            modifier = Modifier.padding(start = 16.dp),
        ) {
            androidx.compose.material3.Text(
                text = "Forgot Password?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(textDecoration = TextDecoration.Underline),
                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        androidx.compose.material3.Button(
            onClick = onSignIn,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(50.dp),
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
            )

        ) {
            androidx.compose.material3.Text(
                text = "Login",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }


Box (
    contentAlignment = Alignment.Center,
){
    androidx.compose.material3.Divider()
    androidx.compose.material3.Text(
        text = "   Or, Login with   ",
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
            .background(androidx.compose.material3.MaterialTheme.colorScheme.surface),
        color = androidx.compose.material3.MaterialTheme.colorScheme.secondary,

        fontSize = 16.sp,

    )
}

        androidx.compose.material3.OutlinedButton(
            shape = RoundedCornerShape(32.dp),
            onClick = { onSignInWithGoogle() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .height(50.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.google),
                contentDescription = null,
                tint = Color.Unspecified
            )
            androidx.compose.material3.Text(
                text = "Sign In with Google", fontSize = 18.sp,

            )
        }
        Row(
            modifier = Modifier.padding(start = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            androidx.compose.material3.Text(text = "Don't have an account yet ? ", fontSize = 16.sp)
            androidx.compose.material3.TextButton(onClick = navigateToSignUp) {
                androidx.compose.material3.Text(
                    text = "Sign Up",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(textDecoration = TextDecoration.Underline),
                )
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AppTheme {
        Surface {
            LoginContent(
                paddingValues = PaddingValues(0.dp),
                onSignInWithGoogle = { },
                navigateToSignUp = {},
                navigateToForgotPassword = {},
                onEmailChange = {},
                onPasswordChange = {},
                onSignIn = {},
                state = LoginUiState()
            )
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun LoginScreenPreviewNight() {
    AppTheme {
        androidx.compose.material3.Surface {
            LoginContent(
                paddingValues = PaddingValues(0.dp),
                onSignInWithGoogle = { },
                navigateToSignUp = {},
                navigateToForgotPassword = {},
                onEmailChange = {},
                onPasswordChange = {},
                onSignIn = {},
                state = LoginUiState()
            )
        }
    }
}
