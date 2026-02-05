package com.aaseem18.ellof.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.aaseem18.ellof.ui.theme.Divider
import com.aaseem18.ellof.ui.theme.MustardYellow
import com.aaseem18.ellof.ui.theme.NudeSurface
import com.aaseem18.ellof.ui.theme.TextPrimary
import java.util.Calendar

@Composable
fun CustomDatePickerDialog(
    initialDate: Calendar, onDismiss: () -> Unit, onConfirm: (Calendar) -> Unit
) {
    var selectedDay by remember { mutableStateOf(initialDate.get(Calendar.DAY_OF_MONTH)) }
    var selectedMonth by remember { mutableStateOf(initialDate.get(Calendar.MONTH)) }
    var selectedYear by remember { mutableStateOf(initialDate.get(Calendar.YEAR)) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(NudeSurface)
                .padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select Date",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date Pickers Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Day Picker
                IOSStyleScrollPicker(
                    items = (1..31).toList(),
                    selectedItem = selectedDay,
                    onItemSelected = { selectedDay = it },
                    modifier = Modifier.weight(1f),
                    displayText = { "$it" })

                // Month Picker
                IOSStyleScrollPicker(
                    items = listOf(
                    "Jan",
                    "Feb",
                    "Mar",
                    "Apr",
                    "May",
                    "Jun",
                    "Jul",
                    "Aug",
                    "Sep",
                    "Oct",
                    "Nov",
                    "Dec"
                ), selectedItem = listOf(
                    "Jan",
                    "Feb",
                    "Mar",
                    "Apr",
                    "May",
                    "Jun",
                    "Jul",
                    "Aug",
                    "Sep",
                    "Oct",
                    "Nov",
                    "Dec"
                )[selectedMonth], onItemSelected = { month ->
                    selectedMonth = listOf(
                        "Jan",
                        "Feb",
                        "Mar",
                        "Apr",
                        "May",
                        "Jun",
                        "Jul",
                        "Aug",
                        "Sep",
                        "Oct",
                        "Nov",
                        "Dec"
                    ).indexOf(month)
                }, modifier = Modifier.weight(1.2f), displayText = { it })

                // Year Picker
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                IOSStyleScrollPicker(
                    items = (currentYear..currentYear + 10).toList(),
                    selectedItem = selectedYear,
                    onItemSelected = { selectedYear = it },
                    modifier = Modifier.weight(1f),
                    displayText = { "$it" })
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
                    onClick = {
                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.YEAR, selectedYear)
                            set(Calendar.MONTH, selectedMonth)
                            set(Calendar.DAY_OF_MONTH, selectedDay)
                        }
                        onConfirm(calendar)
                    }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(
                        containerColor = MustardYellow, contentColor = TextPrimary
                    )
                ) {
                    Text("Done")
                }
            }
        }
    }
}
