package com.example.foodapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.foodapp.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FullNameTextField(
    fullName: String,
    onFullNameChange: (String) -> Unit
) {
    OutlinedTextField(
        value = fullName,
        onValueChange = { onFullNameChange(it) },
        label = { Text(text = stringResource(id = R.string.ho_ten))},
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EmailOrPhoneTextField(
    title:String,
    emailOrPhone: String,
    onPhoneOrEmailChange: (String) -> Unit
) {
    OutlinedTextField(
        value = emailOrPhone,
        onValueChange = { onPhoneOrEmailChange(it) },
        label = { Text(text =title) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PasswordTextField(
    label: Int? = null,
    password: String,
    onPasswordChange: (String) -> Unit,
    onImeActionPerformed: () -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val image = if (isPasswordVisible) {
        painterResource(R.drawable.visibility_off)
    } else {
        painterResource(R.drawable.visibility)
    }

    val contentDescription = if (isPasswordVisible) {
        "Hide password"
    } else {
        "Show password"
    }

    val stringRes = label ?: R.string.password_hint

    OutlinedTextField(
        value = password,
        onValueChange = { onPasswordChange(it) },
        label = { Text(text = stringResource(id = stringRes)) },
        singleLine = true,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeActionPerformed()
                softwareKeyboardController?.hide()
            }
        ),
        trailingIcon = {
            IconButton(
                onClick = { isPasswordVisible = !isPasswordVisible }
            ) {
                Image(
                    painter = image,
                    contentDescription = contentDescription,
                )
            }
        }
    )
}

@Composable
fun GoogleLoginButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .width(170.dp)
            .height(56.dp)
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(8.dp)

    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Icon Google
            Icon(
                painter = painterResource(id = R.drawable.google_logo),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
            // Chữ "Google"
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = R.string.login_with_google),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun TwitterLoginButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .width(170.dp)
            .height(56.dp)
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(8.dp)

    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.twitter_logo),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = R.string.login_with_twitter),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}