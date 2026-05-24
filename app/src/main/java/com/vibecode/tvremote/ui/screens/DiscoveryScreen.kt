package com.vibecode.tvremote.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibecode.tvremote.DiscoveredTv
import com.vibecode.tvremote.RemoteViewModel
import com.vibecode.tvremote.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryScreen(
    viewModel: RemoteViewModel,
    onNavigateToRemote: () -> Unit
) {
    var manualIp by remember { mutableStateOf("") }
    val haptic = LocalHapticFeedback.current
    val discoveredTvs = viewModel.discoveredTvs
    val isScanning = viewModel.isScanning

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
                            colors = listOf(GlowCyan.copy(alpha = 0.15f), Color.Transparent),
                            radius = size.width * 0.8f
                        ),
                        center = Offset(size.width * 0.1f, size.height * 0.1f)
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(GlowPurple.copy(alpha = 0.15f), Color.Transparent),
                            radius = size.width * 0.8f
                        ),
                        center = Offset(size.width * 0.9f, size.height * 0.8f)
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Icon(
                imageVector = Icons.Default.Cast,
                contentDescription = "Cast Info",
                tint = GlowCyan,
                modifier = Modifier
                    .size(64.dp)
                    .drawBehind {
                        drawCircle(
                            color = GlowCyan.copy(alpha = 0.2f),
                            radius = size.maxDimension * 0.7f
                        )
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Vibe Connect",
                color = PureWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Text(
                text = "Discover or enter your Samsung TV address",
                color = MutedText,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, GlassBorder, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkCardBg)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MANUAL IP CONNECTION",
                        color = MutedText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = manualIp,
                        onValueChange = { manualIp = it },
                        placeholder = { Text("e.g. 192.168.1.100", color = MutedText.copy(alpha = 0.5f)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedBorderColor = GlowCyan,
                            unfocusedBorderColor = GlassBorder,
                            cursorColor = GlowCyan
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (manualIp.isNotEmpty()) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.connectManually(manualIp)
                                onNavigateToRemote()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(GlowCyan, GlowPurple)
                                )
                            )
                    ) {
                        Text(
                            text = "Connect Manually",
                            color = ObsidianBg,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DISCOVERED DEVICES",
                    color = MutedText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.startSubnetScan()
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(GlassWhite, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Scan",
                        tint = GlowCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (isScanning) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = GlowCyan,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Scanning Wi-Fi network for TVs...",
                            color = MutedText,
                            fontSize = 14.sp
                        )
                    }
                } else if (discoveredTvs.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = "No TVs",
                            tint = MutedText.copy(alpha = 0.4f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No TVs found. Tap refresh to scan.",
                            color = MutedText,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(discoveredTvs) { tv ->
                            TvDeviceCard(
                                tv = tv,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.selectTv(tv)
                                    onNavigateToRemote()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TvDeviceCard(
    tv: DiscoveredTv,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = DarkCardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Tv,
                contentDescription = "TV",
                tint = GlowCyan,
                modifier = Modifier
                    .size(40.dp)
                    .background(GlassWhite, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = tv.name,
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                tv.model?.let {
                    Text(
                        text = "Model: $it",
                        color = MutedText,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = tv.ip,
                    color = MutedText.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }

            Icon(
                imageVector = Icons.Default.Cast,
                contentDescription = "Connect",
                tint = GlowPurple,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
