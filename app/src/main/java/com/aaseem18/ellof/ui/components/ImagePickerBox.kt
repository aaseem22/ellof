package com.aaseem18.ellof.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aaseem18.ellof.ui.theme.Divider
import com.aaseem18.ellof.ui.theme.NudeSurface
import com.aaseem18.ellof.ui.theme.TextSecondary

@Composable
fun ImagePickerBox(
    text: String = "Pick Image", modifier: Modifier = Modifier, onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = NudeSurface,
        border = BorderStroke(1.dp, Divider)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text, color = TextSecondary
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ImagePickerBoxPreview() {
    ImagePickerBox(
        onClick = {})
}
