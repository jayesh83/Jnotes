package com.jayesh.jnotes.ui.notes

import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.jayesh.jnotes.R
import com.jayesh.jnotes.ui.Screen.NoteEditGraph
import com.jayesh.jnotes.ui.models.Note
import com.jayesh.jnotes.ui.detail.NoteDetailScreen
import com.jayesh.jnotes.ui.detail.NoteDetailViewmodelImpl
import com.jayesh.jnotes.util.Constants
import com.jayesh.jnotes.util.LocalWindowSize
import com.jayesh.jnotes.util.WindowSize
import com.jayesh.jnotes.util.collectState
import com.jayesh.jnotes.util.isCompact
import com.sahu.panes.CenteredPane
import com.sahu.panes.PaneConfig
import com.sahu.panes.TwoPane

@Composable
fun HomeScreen(
    navController: NavController,
    notesViewModel: NotesViewmodel,
    noteDetailViewModel: NoteDetailViewmodelImpl
) {
    val state by notesViewModel.collectState()

    val windowSizeClass = LocalWindowSize.current
    val lazyListState = rememberLazyListState()

    fun navigateToNoteEdit(noteId: String) {
        navController.navigate(NoteEditGraph.createRoute(noteId)) {
            launchSingleTop = true
        }
    }

    fun navigateToCreateNewNote() {
        navController.navigate(NoteEditGraph.createRoute(noteId = null))
    }

    when (windowSizeClass) {
        WindowSize.Compact, WindowSize.Medium -> {
            NotesScreen(
                notes = state.notes,
                scrollToTop = state.scrollToTop,
                onScrolledToTop = { notesViewModel.updateScrollToTop(false) },
                searchQueryText = state.searchQueryText,
                onSearchQueryChanged = notesViewModel::searchNotes,
                onAddNewNote = ::navigateToCreateNewNote,
                onEditNote = ::navigateToNoteEdit,
                notesListState = lazyListState
            )
        }
        WindowSize.Expanded -> {
            val selectedNoteId = remember { mutableStateOf("") }
            TwoPane(
                left = {
                    NoteList(
                        notes = state.notes,
                        contentPadding = PaddingValues(horizontal = 14.dp),
                        listState = lazyListState,
                        onItemClick = { noteId ->
                            // we want to unselect on tap of the same selected item
                            if (selectedNoteId.value == noteId) {
                                selectedNoteId.value = ""
                            } else {
                                selectedNoteId.value = noteId
                            }
                        },
                        scrollToTop = state.scrollToTop,
                        onScrolledToTop = { notesViewModel.updateScrollToTop(false) },
                        modifier = Modifier.align(Alignment.TopStart),
                        searchQuery = state.searchQueryText,
                        onSearchQueryChanged = notesViewModel::searchNotes
                    )
                    AddNewNoteFloatingActionButton(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .navigationBarsPadding(start = false, end = false),
                        onClick = ::navigateToCreateNewNote,
                        windowSizeClass = windowSizeClass
                    )
                },
                right = {
                    Crossfade(targetState = selectedNoteId) { noteId ->
                        if (noteId.value.isBlank()) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = "Select a note to view details",
                                    modifier = Modifier.align(Alignment.Center),
                                    style = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.onSurface)
                                )
                            }
                        } else {
                            LaunchedEffect(key1 = noteId.value) {
                                noteDetailViewModel.loadNote(noteId.value)
                            }
                            NoteDetailScreen(
                                navController = navController,
                                viewmodel = noteDetailViewModel,
                                showingInMasterDetailUI = true
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun NotesScreen(
    notes: List<Note>,
    scrollToTop: Boolean,
    onScrolledToTop: () -> Unit,
    searchQueryText: String,
    onSearchQueryChanged: (String) -> Unit,
    onAddNewNote: () -> Unit,
    onEditNote: (noteId: String) -> Unit,
    notesListState: LazyListState
) {
    val appBarTransition = updateTransition(
        targetState = notesListState.firstVisibleItemIndex > 0,
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
        if (firstItemInvisible) Constants.topAppBarHeight else 0.dp
    }

    val currentWindowSizeClass = LocalWindowSize.current

    CenteredPane(
        centerPaneConfig = PaneConfig(integerResource(id = R.integer.center_layout_columns)),
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(
                start = true,
                end = true,
                bottom = false
            )
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
            notes = notes,
            contentPadding = PaddingValues(horizontal = 14.dp),
            listState = notesListState,
            onItemClick = onEditNote,
            scrollToTop = scrollToTop,
            onScrolledToTop = onScrolledToTop,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopStart),
            searchQuery = searchQueryText,
            onSearchQueryChanged = onSearchQueryChanged
        )
        AddNewNoteFloatingActionButton(
            onClick = onAddNewNote,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .navigationBarsPadding(),
            windowSizeClass = currentWindowSizeClass
        )
    }
}

@Composable
fun AddNewNoteFloatingActionButton(
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.new_note),
    icon: ImageVector = Icons.Default.Add,
    windowSizeClass: WindowSize,
    onClick: () -> Unit
) {
    if (windowSizeClass.isCompact()) {
        ExtendedFloatingActionButton(
            text = { Text(text = text) },
            icon = { Icon(imageVector = icon, "Icon Add") },
            onClick = onClick,
            modifier = modifier
        )
    } else {
        FloatingActionButton(onClick = onClick, modifier = modifier) {
            Icon(imageVector = icon, "Icon Add")
        }
    }
}