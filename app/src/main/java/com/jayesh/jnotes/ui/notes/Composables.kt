package com.jayesh.jnotes.ui.notes

import android.content.res.Configuration
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.LocalOverScrollConfiguration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jayesh.jnotes.R
import com.jayesh.jnotes.ui.clearFocusOnKeyboardDismiss
import com.jayesh.jnotes.ui.models.Note
import com.jayesh.jnotes.ui.theme.JnotesTheme
import com.jayesh.jnotes.util.timeAgo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteList(
    notes: List<Note>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    scrollToTop: Boolean,
    onScrolledToTop: () -> Unit,
    onItemClick: (noteId: String) -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (query: String) -> Unit
) {
    val currentOnScrolledToTop by rememberUpdatedState(newValue = onScrolledToTop)
    val currentScrolledToTop by rememberUpdatedState(newValue = scrollToTop)

    LaunchedEffect(key1 = notes) {
        Log.e("CheckingSearch", "query")
        if (searchQuery.isNotEmpty()) onSearchQueryChanged(searchQuery)
    }

    LaunchedEffect(key1 = currentScrolledToTop) {
        if (currentScrolledToTop) {
            Log.e("NoteList", "scrolling to top")
            listState.scrollToItem(0)
            currentOnScrolledToTop.invoke()
        }
    }

    CompositionLocalProvider(LocalOverScrollConfiguration provides null) {
        LazyColumn(
            state = listState,
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = contentPadding
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.h4.copy(color = MaterialTheme.colors.onSurface),
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 32.dp)
                )
            }
            item {
                SearchComponent(
                    modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                    text = searchQuery,
                    onTextChange = onSearchQueryChanged,
                    onClearClick = { onSearchQueryChanged("") }
                )
            }
            itemsIndexed(
                items = notes,
                key = { _, note -> note.id },
                itemContent = { _, note ->
                    NoteItem(note = note, onItemClick = onItemClick)
                }
            )
            item {
                Spacer(
                    modifier = Modifier.height(
                        (LocalConfiguration.current.screenHeightDp / 6).dp
                    )
                )
            }
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onItemClick(note.id) }
            .fillMaxWidth(),
        color = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
        ) {
            val body1Medium = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Medium)
            if (!note.isTitleTextEmpty) {
                Text(
                    text = note.title,
                    style = body1Medium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    modifier = Modifier.padding(end = 2.dp, top = 4.dp)
                )
            }
            if (!note.isContentTextEmpty) {
                Text(
                    text = note.content.text,
                    style = if (!note.isTitleTextEmpty) MaterialTheme.typography.body2
                    else body1Medium,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .alpha(
                            if (!note.isTitleTextEmpty) ContentAlpha.medium
                            else ContentAlpha.high
                        ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = note.lastEdit.timeAgo(),
                style = MaterialTheme.typography.caption,
                modifier = Modifier
                    .padding(top = 6.dp, bottom = 8.dp)
                    .alpha(ContentAlpha.disabled)
            )
        }
    }
}

@Composable
fun SearchComponent(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onClearClick: () -> Unit,
    placeholderText: String = stringResource(id = R.string.search_notes),
    contentColor: Color = MaterialTheme.colors.onSurface
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val textIsBlank by derivedStateOf { text.isBlank() }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50)
    ) {
        val customTextSelectionColors = remember(key1 = contentColor) {
            TextSelectionColors(
                handleColor = contentColor,
                backgroundColor = contentColor.copy(alpha = 0.2f)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(10.dp)
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search icon",
                    modifier = Modifier.requiredSize(20.dp)
                )
                CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                    BasicTextField(
                        value = text,
                        onValueChange = onTextChange,
                        textStyle = MaterialTheme.typography.subtitle1.copy(color = contentColor),
                        singleLine = true,
                        cursorBrush = SolidColor(contentColor),
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester)
                            .clearFocusOnKeyboardDismiss(),
                        decorationBox = {
                            if (textIsBlank) {
                                Text(
                                    text = placeholderText,
                                    style = MaterialTheme.typography.subtitle1
                                )
                            }
                            it()
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus(true) }
                        )
                    )
                }
                // TODO: use shrink, expand animation instead using animation chaining
                AnimatedVisibility(
                    visible = textIsBlank.not(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(
                        onClick = onClearClick,
                        modifier = Modifier
                            .requiredSize(20.dp)
                            .padding(end = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear icon")
                    }
                }
            }
        }
    }
}

@Composable
fun OutlinedLabel(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    text: String,
    color: Color,
    allCapital: Boolean = false,
    @DrawableRes icon: Int?
) {
    Surface(
        modifier = modifier.border(BorderStroke(1.dp, color), shape = shape),
        color = Color.Transparent,
        contentColor = color
    ) {
        if (icon != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .requiredSize(12.dp, 12.dp),
                    painter = painterResource(icon),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(start = 2.dp, end = 4.dp),
                    text = if (allCapital) text.uppercase() else text,
                    style = MaterialTheme.typography.caption
                )
            }
        } else {
            Text(
                text = if (allCapital) text.uppercase() else text,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }
    }
}

@Composable
fun FilledLabel(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    text: String,
    contentColor: Color,
    backgroundColor: Color
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.caption
                .copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

@Composable
fun LabelNewNote(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    text: String = "new",
    color: Color = Color(0xFF80E906),
    allCapital: Boolean = true,
    @DrawableRes icon: Int? = null
) {
    OutlinedLabel(
        modifier = modifier,
        shape = shape,
        text = text,
        color = color,
        allCapital = allCapital,
        icon = icon
    )
}

@Preview(showBackground = true, backgroundColor = 0xff9831f)
@Composable
fun NoteItemPreview() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        JnotesTheme {
            NoteItem(
                Note(
                    title = "Jetpack compose",
                    content = Note.NoteContent("Currently learning jetpack compose in android")
                )
            ) {}
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NoteItemPreviewNightMode() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        JnotesTheme {
            NoteItem(
                Note(
                    title = "Jetpack compose in dark",
                    content = Note.NoteContent("Currently learning jetpack compose in android")
                )
            ) {}
        }
    }
}

@Preview
@Composable
fun LabelNewNotePreview() {
    LabelNewNote(
        modifier = Modifier.padding(30.dp)
    )
}

@Preview
@Composable
fun FilledLabelPreview() {
    FilledLabel(
        modifier = Modifier.padding(30.dp),
        text = "EMAIL",
        contentColor = Color(0xFF424040),
        backgroundColor = Color(0xFFFFFFFF),
    )
}