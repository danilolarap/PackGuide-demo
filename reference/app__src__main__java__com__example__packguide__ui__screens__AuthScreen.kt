package com.example.packguide.ui.screens

import com.example.packguide.ui.theme.PackGuideColors

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.example.packguide.ui.localization.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.packguide.data.auth.AuthRepository
import com.example.packguide.data.auth.AuthResult
import com.example.packguide.data.auth.AuthValidation
import com.example.packguide.settings.AppLanguage
import com.example.packguide.settings.AppSettings
import com.example.packguide.ui.components.AsciiButton
import com.example.packguide.ui.components.DashedDivider
import com.example.packguide.ui.components.HudHeader
import com.example.packguide.ui.components.LedIndicator
import com.example.packguide.ui.components.SettingsDialog
import com.example.packguide.ui.localization.Text as LocalizedText
import com.example.packguide.ui.localization.LocalAppLanguage
import com.example.packguide.ui.localization.localizedText
import com.example.packguide.ui.theme.AccentGreenLed
import com.example.packguide.ui.theme.AccentRedLed
import com.example.packguide.ui.theme.TechMonospaceFontFamily
import com.example.packguide.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    repository: AuthRepository,
    settings: AppSettings,
    onThemeChange: (Boolean) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onAuthenticated: () -> Unit
) {
    var isRegistering by rememberSaveable { mutableStateOf(false) }
    var username by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val submit = {
        val cleanUser = username.trim()
        error = AuthValidation.usernameError(cleanUser)
            ?: if (isRegistering) AuthValidation.passwordError(password)
            else if (password.isEmpty()) "Ingresa tu contraseña."
            else null
            ?: if (isRegistering && password != confirmPassword) {
                "Las contraseñas no coinciden."
            } else null
        if (error == null && !isLoading) {
            isLoading = true
            scope.launch {
                val result = if (isRegistering) repository.register(cleanUser, password)
                else repository.login(cleanUser, password)
                isLoading = false
                when (result) {
                    AuthResult.Success -> onAuthenticated()
                    AuthResult.InvalidInput -> error = "Revisa el usuario y la contraseña e intenta de nuevo."
                    AuthResult.InvalidCredentials -> error = "Usuario o contraseña incorrectos."
                    AuthResult.UsernameTaken -> {
                        isRegistering = false
                        confirmPassword = ""
                        error = "Ese usuario ya existe. Inicia sesión con esa cuenta."
                    }
                    AuthResult.StorageError -> error = "No se pudo acceder a la base local. Intenta de nuevo."
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(PackGuideColors.background).safeDrawingPadding().imePadding()
            .verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LedIndicator(color = AccentRedLed, isPulsing = false)
            Spacer(Modifier.width(10.dp))
            LocalizedText("PACKGUIDE // ACCESS", style = Typography.labelMedium, color = PackGuideColors.secondaryText)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { showSettings = true }) {
                Icon(
                    Icons.Outlined.Settings,
                    contentDescription = localizedText("Configuración", LocalAppLanguage.current),
                    tint = PackGuideColors.primaryText
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        HudHeader(
            title = if (isRegistering) "CREAR CUENTA" else "INICIAR SESIÓN",
            sysCode = "",
            statusText = "", horizontalPadding = 0.dp
        )
        Spacer(Modifier.height(20.dp))
        Spacer(Modifier.height(22.dp))

        Column(
            modifier = Modifier.fillMaxWidth().background(PackGuideColors.surface, RoundedCornerShape(14.dp))
                .border(1.dp, PackGuideColors.border, RoundedCornerShape(14.dp)).padding(20.dp)
        ) {
            Text("[ CREDENCIALES ]", style = Typography.labelMedium, color = PackGuideColors.primaryText)
            Spacer(Modifier.height(18.dp))
            AuthField(
                value = username,
                onValueChange = { username = it; error = null },
                label = "USUARIO",
                isUsername = true,
                supportingText = if (isRegistering) {
                    "Recomendado: letras, números, punto, guion o guion bajo."
                } else null,
                enabled = !isLoading
            )
            Spacer(Modifier.height(12.dp))
            AuthField(
                value = password,
                onValueChange = {
                    password = if (isRegistering) it.take(45) else it
                    error = null
                },
                label = "CONTRASEÑA",
                secret = true,
                supportingText = if (isRegistering) "1 a 45 caracteres: solo letras y números." else null,
                isPasswordVisible = passwordVisible,
                onPasswordVisibilityChange = { passwordVisible = it },
                onImeAction = { if (!isRegistering) submit() },
                enabled = !isLoading
            )
            if (isRegistering) {
                Spacer(Modifier.height(12.dp))
                AuthField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it.take(45)
                        error = null
                    },
                    label = "CONFIRMAR CONTRASEÑA",
                    secret = true,
                    isPasswordVisible = confirmPasswordVisible,
                    onPasswordVisibilityChange = { confirmPasswordVisible = it },
                    onImeAction = submit,
                    enabled = !isLoading
                )
            }
            if (error != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = error.orEmpty(),
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                    style = Typography.bodyMedium,
                    color = AccentRedLed
                )
            }
            Spacer(Modifier.height(20.dp))
            AsciiButton(
                text = when {
                    isLoading && isRegistering -> "[ CREANDO CUENTA... ]"
                    isLoading -> "[ VALIDANDO... ]"
                    isRegistering -> "[ REGISTRAR ]"
                    else -> "[ ENTRAR ]"
                },
                onClick = submit,
                modifier = Modifier.fillMaxWidth(),
                isAccentRed = false,
                enabled = !isLoading
            )
            if (isLoading) {
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(Modifier.size(16.dp), color = AccentRedLed, strokeWidth = 2.dp)
                    Spacer(Modifier.width(10.dp))
                    Text("VERIFICANDO CREDENCIALES...", style = Typography.labelSmall, color = PackGuideColors.secondaryText)
                }
            }
            Spacer(Modifier.height(10.dp))
            AsciiButton(
                text = if (isRegistering) "[ YA TENGO CUENTA ]" else "[ CREAR CUENTA ]",
                onClick = {
                    isRegistering = !isRegistering
                    confirmPassword = ""
                    confirmPasswordVisible = false
                    error = null
                },
                modifier = Modifier.fillMaxWidth(),
                isPrimary = false,
                enabled = !isLoading
            )
        }
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            LedIndicator(color = AccentGreenLed, isPulsing = false)
            Spacer(Modifier.width(8.dp))
            Text("CUENTA ALMACENADA EN ESTE DISPOSITIVO", style = Typography.labelSmall, color = PackGuideColors.secondaryText)
        }
        if (showSettings) {
            SettingsDialog(
                language = settings.language,
                isDarkTheme = settings.isDarkTheme,
                showLogout = false,
                onToggleTheme = { onThemeChange(!settings.isDarkTheme) },
                onToggleLanguage = {
                    onLanguageChange(if (settings.language == AppLanguage.Spanish) AppLanguage.English else AppLanguage.Spanish)
                },
                onLogout = {},
                onDismiss = { showSettings = false }
            )
        }
    }
}

@Composable
private fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    secret: Boolean = false,
    isUsername: Boolean = false,
    supportingText: String? = null,
    isPasswordVisible: Boolean = false,
    onPasswordVisibilityChange: (Boolean) -> Unit = {},
    onImeAction: () -> Unit = {},
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        label = { Text(label, fontFamily = TechMonospaceFontFamily) },
        supportingText = supportingText?.let { help ->
            { Text(help, style = Typography.labelSmall, color = PackGuideColors.secondaryText) }
        },
        singleLine = true,
        visualTransformation = if (secret && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (secret) KeyboardType.Password else KeyboardType.Text,
            imeAction = if (secret) ImeAction.Done else ImeAction.Next,
            capitalization = if (isUsername) KeyboardCapitalization.None else KeyboardCapitalization.Sentences,
            autoCorrectEnabled = false
        ),
        keyboardActions = KeyboardActions(onDone = { onImeAction() }),
        trailingIcon = if (secret) {
            {
                TextButton(onClick = { onPasswordVisibilityChange(!isPasswordVisible) }) {
                    Text(if (isPasswordVisible) "OCULTAR" else "VER", style = Typography.labelSmall)
                }
            }
        } else null,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PackGuideColors.primaryText,
            unfocusedBorderColor = PackGuideColors.border,
            unfocusedContainerColor = PackGuideColors.surfaceVariant.copy(alpha = 0.35f),
            focusedLabelColor = PackGuideColors.primaryText,
            cursorColor = AccentRedLed
        )
    )
}
