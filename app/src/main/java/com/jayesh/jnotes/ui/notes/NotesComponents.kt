package com.jayesh.jnotes.ui.notes

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jayesh.jnotes.R
import com.jayesh.jnotes.ui.models.Note
import com.jayesh.jnotes.ui.models.NoteContent
import com.jayesh.jnotes.util.timeAgo

@ExperimentalFoundationApi //animateItemPlacement
@Composable
fun NoteList(notes: List<Note>, onItemClick: (String) -> Unit) {
    LazyColumn {
        itemsIndexed(
            items = notes,
            key = { _, note -> note.id },
            itemContent = { _, note ->
                NoteItem(
                    note = note,
                    onItemClick = onItemClick,
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = LinearOutSlowInEasing,
                        )
                    )
                )
            }
        )
    }
}

@Composable
fun NoteItem(
    note: Note,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit
) {
    Surface(elevation = 2.dp,
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onItemClick(note.id) }
            .height(IntrinsicSize.Min)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
        ) {
            if (!note.isTitleTextEmpty) {
                Text(
                    text = note.title,
                    style = if (!note.isContentTextEmpty) {
                        MaterialTheme.typography.h6
                    } else {
                        MaterialTheme.typography.body1
                    },
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 2.dp)
                )
            }
            if (!note.isContentTextEmpty) {
                Text(
                    text = note.content.text,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(
                        top = if (!note.isTitleTextEmpty) 8.dp else 0.dp
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = note.lastEdit.timeAgo(),
                    style = MaterialTheme.typography.caption
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(4.dp)
                .background(color = MaterialTheme.colors.onSurface)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoteItemPreview() {
    NoteItem(
        Note(
            title = "Jetpack compose",
            content = NoteContent("Currently learning jetpack compose in android")
        )
    ) {}
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NoteItemPreviewNightMode() {
    NoteItem(
        Note(
            title = "Jetpack compose",
            content = NoteContent("Currently learning jetpack compose in android")
        )
    ) {}
}

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

