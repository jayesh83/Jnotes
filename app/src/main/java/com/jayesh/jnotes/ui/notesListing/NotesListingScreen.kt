package com.jayesh.jnotes.ui.notesListing

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.jayesh.jnotes.R
import com.jayesh.jnotes.ui.models.Note
import com.jayesh.jnotes.ui.models.NoteContent
import com.jayesh.jnotes.ui.theme.JnotesTheme

private const val TAG = "NotesListingScreen"

@Composable
fun NotesListingScreen(
    viewmodel: NotesListingViewmodel,
    onAddNewNote: () -> Unit,
    onEditNote: (noteId: String) -> Unit
) {
    JnotesTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(
                    start = true,
                    end = true,
                    bottom = true
                ), // when in landscape mode, apply end edge navigation bar padding
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.app_name))
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(R.string.new_note)) },
                    icon = {
                        Icon(imageVector = Icons.Default.Add, "Icon Add")
                    },
                    onClick = onAddNewNote
                )
            }
        ) {
            val notes by viewmodel.getAllNotes().collectAsState(initial = null)
            notes?.let {
                LazyColumn {
                    items(it) { note ->
                        NoteItem(
                            note = note,
                            onItemClick = { noteId ->
                                Log.e(TAG, "NotesListingScreen: noteId: $noteId clicked")
                                onEditNote(noteId)
                            }
                        )
                    }
                }
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
    Surface(elevation = 2.dp,
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onItemClick(note.id) }
            .height(IntrinsicSize.Min)
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.h6,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 2.dp)
                )
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = note.createdAt.toString(),
                        style = MaterialTheme.typography.caption,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                }
            }
            Text(
                text = note.content.text,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(top = 8.dp),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
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
    JnotesTheme {
        NoteItem(
            Note(
                title = "Jetpack compose",
                content = NoteContent("Currently learning jetpack compose in android")
            )
        ) {}
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun NoteItemPreviewNightMode() {
    JnotesTheme {
        NoteItem(
            Note(
                title = "Jetpack compose",
                content = NoteContent("Currently learning jetpack compose in android")
            )
        ) {}
    }
}