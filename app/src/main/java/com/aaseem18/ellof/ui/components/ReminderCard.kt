package com.aaseem18.ellof.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.aaseem18.ellof.ui.theme.NudeBackground
import com.aaseem18.ellof.ui.theme.NudeSurface
import com.aaseem18.ellof.ui.theme.TextPrimary
import com.aaseem18.ellof.ui.theme.TextSecondary

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.aaseem18.ellof.ui.theme.Divider
import com.aaseem18.ellof.ui.theme.MustardYellow

@Composable
fun ReminderCard(
    title: String,
    time: String,
    imageUri: String?,
    isCompleted: Boolean,
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = NudeSurface
        ),
        elevation = CardDefaults.cardElevation(0.dp), // Softer: remove elevation
        shape = RoundedCornerShape(12.dp) // Softer: increased corner radius
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp) // Consistent: standardized to 16dp
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ✅ Tick button (LEFT – iOS style)
            IconButton(
                onClick = onCompleteClick,
                modifier = Modifier.size(40.dp) // Consistent: defined touch target
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Mark completed",
                    tint = if (isCompleted) MustardYellow else Divider, // Sparing yellow: only when active
                    modifier = Modifier.size(24.dp) // Consistent icon size
                )
            }

            Spacer(modifier = Modifier.width(8.dp)) // Consistent spacing

            // 📸 Image
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp) // Slightly larger for better visibility
                        .clip(RoundedCornerShape(12.dp)) // Match card corner radius
                )
                Spacer(modifier = Modifier.width(12.dp)) // Consistent spacing
            }

            // 📝 Text content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (isCompleted) TextSecondary else TextPrimary,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = if (isCompleted)
                            androidx.compose.ui.text.style.TextDecoration.LineThrough
                        else
                            null
                    )
                )

                Spacer(modifier = Modifier.height(4.dp)) // Consistent spacing

                Text(
                    text = time,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // 🗑 Delete (RIGHT)
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(40.dp) // Consistent: defined touch target
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete reminder",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp) // Slightly smaller, less prominent
                )
            }
        }
    }
}