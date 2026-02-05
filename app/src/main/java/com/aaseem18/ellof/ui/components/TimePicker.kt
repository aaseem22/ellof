package com.aaseem18.ellof.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.aaseem18.ellof.ui.theme.Divider
import com.aaseem18.ellof.ui.theme.MustardYellow
import com.aaseem18.ellof.ui.theme.NudeSurface
import com.aaseem18.ellof.ui.theme.TextPrimary
import kotlinx.coroutines.launch

@Composable
fun CustomTimePickerDialog(
    initialTime: Pair<Int, Int>, onDismiss: () -> Unit, onConfirm: (Int, Int) -> Unit
) {
    var selectedHour by remember { mutableStateOf(initialTime.first) }
    var selectedMinute by remember { mutableStateOf(initialTime.second) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(NudeSurface)
                .padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select Time",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Time Pickers Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hour Picker
                IOSStyleScrollPicker(
                    items = (0..23).toList(),
                    selectedItem = selectedHour,
                    onItemSelected = { selectedHour = it },
                    modifier = Modifier.weight(1f),
                    displayText = { String.format("%02d", it) })

                Text(
                    text = ":", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary
                )

                // Minute Picker
                IOSStyleScrollPicker(
                    items = (0..59).toList(),
                    selectedItem = selectedMinute,
                    onItemSelected = { selectedMinute = it },
                    modifier = Modifier.weight(1f),
                    displayText = { String.format("%02d", it) })
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Divider)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = { onConfirm(selectedHour, selectedMinute) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MustardYellow, contentColor = TextPrimary
                    )
                ) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
fun <T> IOSStyleScrollPicker(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    displayText: (T) -> String
) {
    val initialIndex = items.indexOf(selectedItem).takeIf { it >= 0 } ?: 0
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        listState.scrollToItem(initialIndex)
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val centerIndex = listState.firstVisibleItemIndex
            val selectedIndex = centerIndex.coerceIn(items.indices)
            onItemSelected(items[selectedIndex])

            coroutineScope.launch {
                listState.animateScrollToItem(selectedIndex)
            }
        }
    }

    Box(
        modifier = modifier.height(180.dp)
    ) {
        // Gradient overlays
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            NudeSurface, NudeSurface.copy(alpha = 0f)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            NudeSurface.copy(alpha = 0f), NudeSurface
                        )
                    )
                )
        )

        // Selection highlight
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.Center)
                .background(
                    MustardYellow.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)
                )
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 60.dp),
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        ) {
            items(items.size) { index ->
                val item = items[index]
                val isCenterItem = index == listState.firstVisibleItemIndex

                Text(
                    text = displayText(item),
                    fontSize = if (isCenterItem) 22.sp else 18.sp,
                    fontWeight = if (isCenterItem) FontWeight.Bold else FontWeight.Normal,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .alpha(if (isCenterItem) 1f else 0.4f)
                        .clickable {
                            onItemSelected(item)
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                            }
                        })
            }
        }
    }
}