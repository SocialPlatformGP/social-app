package com.gp.auth.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    navigateToSignUp: () -> Unit,
    navigateToForgotPassword: () -> Unit,
    onSignInWithGoogle: () -> Unit,
    onSignInClicked: () -> Unit
) {
    val state by viewModel.loginStateFlow.collectAsState()
    Scaffold {paddingValues ->
        LoginContent(
            paddingValues = paddingValues,
            onSignInWithGoogle = {
                onSignInWithGoogle()
            },
            state = state,
            navigateToSignUp = navigateToSignUp,
            navigateToForgotPassword = navigateToForgotPassword,
            onEmailChange = {viewModel.updateEmail(it)  },
            onPasswordChange = { viewModel.updatePassword(it) },
            onSignIn = onSignInClicked

        )
    }

}

@Composable
fun LoginContent(
    paddingValues: PaddingValues,
    onSignInWithGoogle: () -> Unit,
    onSignIn : () -> Unit,
    state: LoginUiState,
    navigateToSignUp: () -> Unit,
    navigateToForgotPassword: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.Center,
    ) {

        var passwordVisible by remember { mutableStateOf(false) }


        Image(painter = painterResource(id = R.drawable.login), contentDescription = null)

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.welcome_back),
            fontSize = 42.sp,
            color = Color(0xff222f86)
        )


        OutlinedTextField(
            value = state.email,
            onValueChange = { onEmailChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            label = { Text(text = "Email", color = Color(0xff222f86).copy( alpha = 0.7f)) },
            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = Color(0xff222f86)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = state.emailError.isNotEmpty(),
        )
        if(state.emailError.isNotEmpty()){
            Text(
                text = state.emailError,
                color = MaterialTheme.colors.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp),
            )
        }

        OutlinedTextField(
            value = state.password,
            onValueChange = { onPasswordChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            label = { Text(text = "Password",color=Color(0xff222f86).copy( alpha = 0.7f)) },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null,tint=Color(0xff222f86)) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description, tint = Color(0xff222f86))
                }
            },
            isError = state.passwordError.isNotEmpty(),

        )
        if(state.passwordError.isNotEmpty()){
            Text(
                text = state.passwordError,
                color = MaterialTheme.colors.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }


        TextButton(
            onClick = navigateToForgotPassword,
            modifier = Modifier
                .padding(start = 16.dp),
        ) {
            Text(
                text = "Forgot Password?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(textDecoration = TextDecoration.Underline),
                color = Color(0xff222f86)
            )
        }

        Button(
            onClick = onSignIn,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xff222f86),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Login",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }



        Text(
            text = "Or, Login with...",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .wrapContentWidth(Alignment.CenterHorizontally),
            fontSize = 16.sp,
            color = Color(0xff222f86).copy( alpha = 0.7f)
        )
        OutlinedButton(
            onClick = { onSignInWithGoogle() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, androidx.compose.ui.graphics.Color.Black)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.google),
                contentDescription = null,
                tint = androidx.compose.ui.graphics.Color.Unspecified,
            )
            Text(
                text = "Sign In with Google",
                fontSize = 18.sp,
                color = Color(0xff222f86)
            )
        }
        Row(
            modifier = Modifier
                .padding(start = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Don't have an account yet ? ", fontSize = 16.sp)
            TextButton(onClick = navigateToSignUp) {
                Text(
                    text = "Sign Up",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(textDecoration = TextDecoration.Underline),
                    color =Color(0xff222f86).copy( alpha = 0.7f)
                )
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginContent(
        paddingValues = PaddingValues(0.dp),
        onSignInWithGoogle = { },
        navigateToSignUp = {},
        navigateToForgotPassword = {},
        onEmailChange = {},
        onPasswordChange = {},
        onSignIn = {},
        state = LoginUiState(),

    )

}
