package com.aaseem18.ellof.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aaseem18.ellof.ui.theme.MustardYellow
import com.aaseem18.ellof.ui.theme.NudeSurface
import com.aaseem18.ellof.ui.theme.TextPrimary
import com.aaseem18.ellof.ui.theme.TextSecondary

@Composable
fun DeleteReminderDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NudeSurface,
        title = {
            Text(
                text = "Delete reminder?",
                color = TextPrimary
            )
        },
        text = {
            Text(
                text = "This reminder will be permanently removed.",
                color = TextSecondary
            )
        },
        confirmButton = {
            Text(
                text = "Delete",
                color = Color.Black,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onConfirm() }
            )
        },
        dismissButton = {
            Text(
                text = "Cancel",
                color = TextSecondary,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onDismiss() }
            )
        }
    )
}
