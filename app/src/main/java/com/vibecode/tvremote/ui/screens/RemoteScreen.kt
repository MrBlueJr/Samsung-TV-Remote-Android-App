package com.vibecode.tvremote.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibecode.tvremote.RemoteViewModel
import com.vibecode.tvremote.SamsungTvClient
import com.vibecode.tvremote.ui.theme.*

data class AppShortcut(
    val name: String,
    val id: String,
    val color: Color,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteScreen(
    viewModel: RemoteViewModel,
    onNavigateBack: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val connectionState = viewModel.connectionState
    val tvName = viewModel.currentTvName ?: "Samsung TV"

    var showKeyboardDialog by remember { mutableStateOf(false) }
    var keyboardInputText by remember { mutableStateOf(TextFieldValue("")) }
    val focusManager = LocalFocusManager.current

    val apps = listOf(
        AppShortcut("YouTube", "11101200001", GlowCyan, Icons.Default.PlayArrow),
        AppShortcut("Netflix", "11101200007", PowerPink, Icons.Default.Movie),
        AppShortcut("Prime Video", "3201512006785", AccentOrange, Icons.Default.VideoLibrary),
        AppShortcut("Spotify", "3201606009684", Color(0xFF1DB954), Icons.Default.MusicNote),
        AppShortcut("Disney+", "3201901017640", GlowPurple, Icons.Default.Slideshow)
    )

    val statusText = when (connectionState) {
        SamsungTvClient.State.DISCONNECTED -> "Disconnected"
        SamsungTvClient.State.CONNECTING -> "Connecting..."
        SamsungTvClient.State.PAIRING -> "Pairing: Check TV!"
        SamsungTvClient.State.CONNECTED -> "Connected"
        SamsungTvClient.State.ERROR -> "Connection Failed"
    }

    val statusColor = when (connectionState) {
        SamsungTvClient.State.DISCONNECTED -> MutedText
        SamsungTvClient.State.CONNECTING -> AccentOrange
        SamsungTvClient.State.PAIRING -> GlowPurple
        SamsungTvClient.State.CONNECTED -> GlowCyan
        SamsungTvClient.State.ERROR -> PowerPink
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(GlowPurple.copy(alpha = 0.12f), Color.Transparent),
                            radius = size.width * 0.7f
                        ),
                        center = Offset(size.width * 0.2f, size.height * 0.8f)
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(GlowCyan.copy(alpha = 0.12f), Color.Transparent),
                            radius = size.width * 0.7f
                        ),
                        center = Offset(size.width * 0.8f, size.height * 0.2f)
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onNavigateBack()
                    },
                    modifier = Modifier.background(GlassWhite, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = PureWhite
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = tvName,
                        color = PureWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                                .drawBehind {
                                    drawCircle(
                                        color = statusColor.copy(alpha = 0.5f),
                                        radius = size.maxDimension * 1.5f
                                    )
                                }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.forgetTv()
                        onNavigateBack()
                    },
                    modifier = Modifier.background(GlassWhite, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Forget",
                        tint = PowerPink
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkCardBg)
                    .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RemoteCircleButton(
                    icon = Icons.Default.PowerSettingsNew,
                    label = "Power",
                    tint = PowerPink,
                    glowColor = PowerPink,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.sendKey("KEY_POWER")
                    }
                )

                RemoteCircleButton(
                    icon = Icons.Default.Input,
                    label = "Source",
                    tint = GlowCyan,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.sendKey("KEY_SOURCE")
                    }
                )

                RemoteCircleButton(
                    icon = Icons.Default.Keyboard,
                    label = "Keyboard",
                    tint = GlowPurple,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showKeyboardDialog = true
                    }
                )

                RemoteCircleButton(
                    icon = Icons.Default.VolumeMute,
                    label = "Mute",
                    tint = MutedText,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.sendKey("KEY_MUTE")
                    }
                )
            }

            Spacer(modifier = Modifier.weight(0.5f))

            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(DarkCardBg)
                    .border(2.dp, GlassBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(GlowPurple.copy(alpha = 0.05f), Color.Transparent),
                                    radius = size.width * 0.5f
                                ),
                                center = Offset(size.width * 0.5f, size.height * 0.5f)
                            )
                        }
                )

                DpadDirectionButton(
                    icon = Icons.Default.KeyboardArrowUp,
                    modifier = Modifier.align(Alignment.TopCenter),
                    contentDescription = "Up",
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.sendKey("KEY_UP")
                    }
                )

                DpadDirectionButton(
                    icon = Icons.Default.KeyboardArrowDown,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    contentDescription = "Down",
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.sendKey("KEY_DOWN")
                    }
                )

                DpadDirectionButton(
                    icon = Icons.Default.KeyboardArrowLeft,
                    modifier = Modifier.align(Alignment.CenterStart),
                    contentDescription = "Left",
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.sendKey("KEY_LEFT")
                    }
                )

                DpadDirectionButton(
                    icon = Icons.Default.KeyboardArrowRight,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    contentDescription = "Right",
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.sendKey("KEY_RIGHT")
                    }
                )

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(GlowCyan.copy(alpha = 0.9f), GlowPurple.copy(alpha = 0.9f))
                            )
                        )
                        .border(1.dp, GlassBorder, CircleShape)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.sendKey("KEY_ENTER")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "OK",
                        color = ObsidianBg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .width(72.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(DarkCardBg)
                        .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.sendKey("KEY_VOLUP")
                    }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Vol Up", tint = GlowCyan)
                    }
                    Text(
                        text = "VOL",
                        color = MutedText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.sendKey("KEY_VOLDOWN")
                    }) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Vol Down", tint = GlowCyan)
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.sendKey("KEY_RETURN")
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(DarkCardBg, CircleShape)
                            .border(1.dp, GlassBorder, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Undo, contentDescription = "Back", tint = PureWhite)
                    }

                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.sendKey("KEY_HOME")
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(DarkCardBg, CircleShape)
                            .border(1.dp, GlassBorder, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = "Home", tint = PureWhite)
                    }
                }

                Column(
                    modifier = Modifier
                        .width(72.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(DarkCardBg)
                        .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.sendKey("KEY_CHUP")
                    }) {
                        Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "CH Up", tint = GlowPurple)
                    }
                    Text(
                        text = "CH",
                        color = MutedText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.sendKey("KEY_CHDOWN")
                    }) {
                        Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "CH Down", tint = GlowPurple)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "QUICK APPS",
                    color = MutedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(apps) { app ->
                        AppShortcutCapsule(app = app) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.launchApp(app.id)
                        }
                    }
                }
            }
        }

        if (showKeyboardDialog) {
            AlertDialog(
                onDismissRequest = { showKeyboardDialog = false },
                containerColor = Color(0xFF13131A),
                modifier = Modifier.border(1.dp, GlassBorder, RoundedCornerShape(28.dp)),
                title = {
                    Text(
                        text = "TV Keyboard Input",
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Stream typed text directly into search fields on your TV.",
                            color = MutedText,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = keyboardInputText,
                            onValueChange = {
                                val oldVal = keyboardInputText.text
                                val newVal = it.text
                                keyboardInputText = it
                                
                                if (newVal.length > oldVal.length) {
                                    val addedChar = newVal.substring(oldVal.length)
                                    viewModel.sendText(addedChar)
                                } else if (newVal.length < oldVal.length) {
                                    viewModel.sendKey("KEY_BACKSPACE")
                                }
                            },
                            placeholder = { Text("Start typing...", color = MutedText.copy(alpha = 0.5f)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                focusManager.clearFocus()
                                showKeyboardDialog = false
                            }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = PureWhite,
                                unfocusedTextColor = PureWhite,
                                focusedBorderColor = GlowCyan,
                                unfocusedBorderColor = GlassBorder,
                                cursorColor = GlowCyan
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            keyboardInputText = TextFieldValue("")
                            showKeyboardDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GlowCyan)
                    ) {
                        Text("Done", color = ObsidianBg, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun RemoteCircleButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    glowColor: Color? = null,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(52.dp)
                .background(GlassWhite, CircleShape)
                .border(1.dp, GlassBorder, CircleShape)
                .drawBehind {
                    if (glowColor != null) {
                        drawCircle(
                            color = glowColor.copy(alpha = 0.15f),
                            radius = size.maxDimension * 0.7f
                        )
                    }
                }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = MutedText,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun DpadDirectionButton(
    icon: ImageVector,
    modifier: Modifier,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(60.dp)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = PureWhite,
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
fun AppShortcutCapsule(
    app: AppShortcut,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(DarkCardBg)
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = app.icon,
            contentDescription = app.name,
            tint = app.color,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = app.name,
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
