package com.jayesh.jnotes.ui.notesListing

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jayesh.jnotes.R

@Composable
fun IndicatorChip(
    modifier: Modifier = Modifier,
    shape: Shape,
    text: String,
    color: Color,
    @DrawableRes icon: Int
) {
    Surface(
        modifier = modifier
            .border(BorderStroke(1.dp, color), shape = shape)
            .padding(2.dp),
        color = Color.Transparent
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(horizontal = 2.dp),
                painter = painterResource(id = icon),
                tint = color,
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(start = 2.dp, end = 4.dp),
                text = text.uppercase(),
                color = color,
            )
        }
    }
}

@Composable
fun FilledIndicatorChip(
    modifier: Modifier = Modifier,
    shape: Shape,
    text: String,
    backgroundColor: Color,
    contentColor: Color
) {
    Surface(
        modifier = modifier.padding(4.dp),
        color = backgroundColor,
        shape = shape
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 2.dp),
            text = text.uppercase(),
            color = contentColor
        )
    }
}

@Composable
fun ExtraSessionFilledIndicatorChip(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    text: String,
    backgroundColor: Color = Color(0x80444444),
    contentColor: Color = Color.White
) {
    FilledIndicatorChip(
        modifier = modifier,
        shape = shape,
        text = text,
        backgroundColor = backgroundColor,
        contentColor = contentColor
    )
}

@Composable
fun FillingFastIndicatorChip(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    text: String,
    color: Color,
    @DrawableRes icon: Int = R.drawable.ic_icon_filling_fast
) {
    IndicatorChip(
        text = text,
        color = color,
        icon = icon,
        shape = shape,
        modifier = modifier
    )
}

/** Preview **/

@Preview(showBackground = true)
@Composable
fun PreviewFillingFastIndicatorChip() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FillingFastIndicatorChip(
            text = "Filling Fast",
            color = Color(0xff82d848),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewExtraSessionFilledIndicatorChip() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExtraSessionFilledIndicatorChip(text = "Extra Session")
    }
}

