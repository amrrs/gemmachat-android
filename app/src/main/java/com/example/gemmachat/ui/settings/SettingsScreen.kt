package com.example.gemmachat.ui.settings

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemmachat.R
import com.example.gemmachat.ui.theme.AccentPurple
import com.example.gemmachat.ui.theme.AccentViolet
import com.example.gemmachat.ui.theme.BgDark
import com.example.gemmachat.ui.theme.BgMid
import com.example.gemmachat.ui.theme.Divider
import com.example.gemmachat.ui.theme.ErrorRed
import com.example.gemmachat.ui.theme.GlassBg
import com.example.gemmachat.ui.theme.GlassBorder
import com.example.gemmachat.ui.theme.TextMuted
import com.example.gemmachat.ui.theme.TextPrimary
import com.example.gemmachat.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
) {
    var cleared by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val appVersion = remember { appVersionName(context) }

    Box(
        Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(listOf(BgDark, BgMid, BgDark))),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            // Top bar
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Text(
                    stringResource(R.string.settings),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }

            Box(Modifier.fillMaxWidth().height(1.dp).background(Divider))

            Column(
                Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                // Model card
                Text("Model", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                        .background(GlassBg)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .size(44.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(AccentPurple, AccentViolet),
                                    start = Offset.Zero,
                                    end = Offset(44f, 44f),
                                ),
                                shape = CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("G", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Gemma 4 E2B", color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                        Text("LiteRT-LM · on-device inference", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(Modifier.height(24.dp))
                Text("Data", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))

                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                        .background(GlassBg),
                ) {
                    SettingsRow(
                        icon = Icons.Outlined.DeleteSweep,
                        label = stringResource(R.string.clear_chats),
                        description = "Remove all conversation history",
                        tint = TextPrimary,
                        onClick = { viewModel.clearChatHistory(); cleared = true },
                    )
                    if (cleared) {
                        Text(
                            "Chats cleared",
                            color = AccentViolet,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(start = 56.dp, bottom = 4.dp),
                        )
                    }
                    Box(Modifier.fillMaxWidth().padding(start = 56.dp, end = 16.dp).height(1.dp).background(Divider))
                    SettingsRow(
                        icon = Icons.Outlined.Delete,
                        label = stringResource(R.string.delete_model),
                        description = "Re-download required to chat again",
                        tint = ErrorRed,
                        onClick = { viewModel.deleteModel() },
                    )
                }

                Spacer(Modifier.height(24.dp))
                Text(
                    stringResource(R.string.settings_privacy_note),
                    color = TextMuted,
                    style = MaterialTheme.typography.bodyMedium,
                )

                Spacer(Modifier.height(24.dp))
                Text(
                    stringResource(R.string.about),
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(8.dp))

                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                        .background(GlassBg),
                ) {
                    SettingsInfoRow(
                        icon = Icons.Outlined.Info,
                        label = stringResource(R.string.about_app_title),
                        description = stringResource(R.string.about_app_description),
                        supporting = stringResource(R.string.about_version, appVersion),
                    )
                    SettingsDivider()
                    SettingsLinkRow(
                        icon = Icons.Outlined.Person,
                        label = stringResource(R.string.about_creator_title),
                        description = stringResource(R.string.about_creator_description),
                        tint = TextPrimary,
                        onClick = { uriHandler.openUri("https://x.com/1littlecoder") },
                    )
                    SettingsDivider()
                    SettingsLinkRow(
                        icon = Icons.Outlined.Public,
                        label = stringResource(R.string.about_litert_title),
                        description = stringResource(R.string.about_litert_description),
                        tint = TextPrimary,
                        onClick = { uriHandler.openUri("https://huggingface.co/litert-community/gemma-4-E2B-it-litert-lm") },
                    )
                    SettingsDivider()
                    SettingsLinkRow(
                        icon = Icons.Outlined.Public,
                        label = stringResource(R.string.about_gemma_title),
                        description = stringResource(R.string.about_gemma_description),
                        tint = TextPrimary,
                        onClick = { uriHandler.openUri("https://deepmind.google/models/gemma/gemma-4/") },
                    )
                    SettingsDivider()
                    SettingsInfoRow(
                        icon = Icons.Outlined.Security,
                        label = stringResource(R.string.about_privacy_title),
                        description = stringResource(R.string.about_privacy_description),
                    )
                    SettingsDivider()
                    SettingsInfoRow(
                        icon = Icons.Outlined.Memory,
                        label = stringResource(R.string.about_runtime_title),
                        description = stringResource(R.string.about_runtime_description),
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    description: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        Column(Modifier.padding(start = 14.dp)) {
            Text(label, color = tint, fontWeight = FontWeight.Medium)
            Text(description, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun SettingsInfoRow(
    icon: ImageVector,
    label: String,
    description: String,
    supporting: String? = null,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(icon, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(22.dp))
        Column(
            modifier = Modifier
                .padding(start = 14.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(label, color = TextPrimary, fontWeight = FontWeight.Medium)
            Text(description, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            supporting?.let {
                Text(it, color = TextMuted, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun SettingsLinkRow(
    icon: ImageVector,
    label: String,
    description: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        Column(
            modifier = Modifier
                .padding(start = 14.dp)
                .weight(1f),
        ) {
            Text(label, color = tint, fontWeight = FontWeight.Medium)
            Text(description, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
        Icon(
            Icons.AutoMirrored.Outlined.OpenInNew,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(start = 56.dp, end = 16.dp)
            .height(1.dp)
            .background(Divider),
    )
}

private fun appVersionName(context: Context): String =
    runCatching {
        val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0),
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
        info.versionName ?: info.longVersionCode.toString()
    }.getOrDefault("1.0")
