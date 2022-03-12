package com.jayesh.jnotes.ui.notes

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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

    val appBarTransition = updateTransition(
        targetState = listState.firstVisibleItemIndex > 0,
        label = "App bar"
    )

    val topBarAlpha by appBarTransition.animateFloat(
        transitionSpec = {
            tween(easing = FastOutSlowInEasing)
        },
        label = "Appbar offset"
    ) { firstItemInvisible ->
        if (firstItemInvisible) 1f else 0f
    }

    val topBarHeight by appBarTransition.animateDp(
        transitionSpec = {
            tween(easing = FastOutSlowInEasing)
        },
        label = "appbar height"
    ) { firstItemInvisible ->
        if (firstItemInvisible) appBarHeight else 0.dp
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(
                start = true,
                end = true,
                bottom = false
            ), // when in landscape mode, apply end edge navigation bar padding
    ) {
        TopAppBar(
            title = { Text(text = stringResource(id = R.string.app_name)) },
            modifier = Modifier
                .alpha(topBarAlpha)
                .height(topBarHeight)
                .align(Alignment.TopCenter)
                .zIndex(1f)
        )
        NoteList(
            notes = viewmodel.notes.collectAsState().value,
            contentPadding = PaddingValues(horizontal = 14.dp),
            listState = listState,
            onItemClick = onEditNote,
            scrollToTop = viewmodel.scrollToTop,
            onScrolledToTop = { viewmodel.updateScrollToTop(false) },
            modifier = Modifier.align(Alignment.TopStart),
            onSearchQueryChanged = { query -> viewmodel.searchNotes(query) }
        )
        ExtendedFloatingActionButton(
            text = { Text(text = stringResource(R.string.new_note)) },
            icon = { Icon(imageVector = Icons.Default.Add, "Icon Add") },
            onClick = onAddNewNote,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
                .navigationBarsPadding()
        )
    }
}

private val appBarHeight = 56.dp