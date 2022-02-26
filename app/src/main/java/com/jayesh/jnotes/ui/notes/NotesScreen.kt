package com.jayesh.jnotes.ui.notes

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.jayesh.jnotes.R

private const val TAG = "NotesListingScreen"

@Composable
fun NotesScreen(
    viewmodel: NotesViewmodelImpl,
    onAddNewNote: () -> Unit,
    onEditNote: (noteId: String) -> Unit
) {
    val listState = rememberLazyListState()
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
            if (listState.firstVisibleItemIndex > 0) {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.app_name))
                    }
                )
            }
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
        NoteList(
            notes = viewmodel.notes.collectAsState().value,
            contentPadding = PaddingValues(horizontal = 14.dp),
            listState = listState,
            onItemClick = onEditNote,
            scrollToTop = viewmodel.scrollToTop,
            onScrolledToTop = { viewmodel.updateScrollToTop(false) }
        )
    }
}