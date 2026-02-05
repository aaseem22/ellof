package com.aaseem18.ellof.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aaseem18.ellof.data.local.ReminderEntity
import com.aaseem18.ellof.ui.add.formatDateTime
import com.aaseem18.ellof.ui.components.DeleteReminderDialog
import com.aaseem18.ellof.ui.components.ReminderCard
import com.aaseem18.ellof.ui.theme.*
import com.aaseem18.ellof.viewmodel.ReminderViewModel

@Composable
fun HomeScreen(
    viewModel: ReminderViewModel,
    onAddClick: () -> Unit
) {
    val reminders by viewModel.reminders.collectAsState()
    var reminderToDelete by remember { mutableStateOf<ReminderEntity?>(null) }
    val context = LocalContext.current

    Scaffold(
        containerColor = NudeBackground, // Consistent background
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MustardYellow, // Sparing yellow: only on primary action
                contentColor = TextPrimary,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp) // Softer corners
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NudeBackground)
                .padding(padding)
        ) {
            if (reminders.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp), // Consistent: outer padding
                    verticalArrangement = Arrangement.spacedBy(12.dp) // Consistent: spacing between items
                ) {
                    items(reminders.size) { index ->
                        val reminder = reminders[index]

                        ReminderCard(
                            title = reminder.title,
                            time = formatDateTime(reminder.triggerTime),
                            imageUri = reminder.imageUri,
                            isCompleted = reminder.isCompleted,
                            onCompleteClick = {
                                viewModel.markCompleted(reminder, context)
                            },
                            onDeleteClick = {
                                reminderToDelete = reminder
                            }
                        )

                        // Subtle divider: only between items, not after last one
                        if (index < reminders.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(vertical = 6.dp), // Consistent spacing
                                thickness = 1.dp,
                                color = Divider.copy(alpha = 0.3f) // Very subtle
                            )
                        }
                    }
                }
            }
        }
    }

    // 🔴 Confirmation dialog
    reminderToDelete?.let { reminder ->
        DeleteReminderDialog(
            onConfirm = {
                viewModel.deleteReminder(reminder, context)
                reminderToDelete = null
            },
            onDismiss = {
                reminderToDelete = null
            }
        )
    }
}



@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp), // Consistent: padding for empty state
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No reminders yet 🌼",
            color = TextPrimary,
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp)) // Consistent spacing
        Text(
            text = "Tap + to add something important",
            color = TextSecondary,
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
        )
    }
}