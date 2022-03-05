package com.jayesh.jnotes.ui.noteDetail

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding
import com.jayesh.jnotes.ui.noteDetail.BackgroundType.SingleColor
import com.jayesh.jnotes.ui.noteDetail.CurrentlyEditing.None
import com.jayesh.jnotes.ui.noteDetail.CurrentlyEditing.Note
import com.jayesh.jnotes.ui.noteDetail.CurrentlyEditing.Title
import com.jayesh.jnotes.ui.theme.BlackMuted
import com.jayesh.jnotes.ui.theme.Blue200
import com.jayesh.jnotes.ui.theme.Blue500
import com.jayesh.jnotes.ui.theme.Green200
import com.jayesh.jnotes.ui.theme.Green500
import com.jayesh.jnotes.ui.theme.Grey200
import com.jayesh.jnotes.ui.theme.Grey500
import com.jayesh.jnotes.ui.theme.JnotesTheme
import com.jayesh.jnotes.ui.theme.Orange500
import com.jayesh.jnotes.ui.theme.Parrot200
import com.jayesh.jnotes.ui.theme.Parrot500
import com.jayesh.jnotes.ui.theme.Pink200
import com.jayesh.jnotes.ui.theme.Pink500
import com.jayesh.jnotes.ui.theme.WhiteMutated

private const val TAG = "NewOrEditNote"

// TODO: 04/01/22 scroll title along with note
// FIXME: 04/01/22 textfield text below keyboard

@Composable
fun NoteDetailScreen(
    viewmodel: NoteDetailViewmodelImpl,
    onBack: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    fun onBackPress() {
        if (viewmodel.noteBackgroundChangerBottomSheetVisible) {
            viewmodel.toggleNoteBackgroundChangerState()
        } else {
            focusManager.clearFocus(true)
            viewmodel.updateNoteIfNeeded()
            onBack()
        }
    }

    BackHandler {
        Log.e(TAG, "NewOrEditNoteScreen: BackHandler called")
        onBackPress()
    }

    JnotesTheme(
        backgroundColor = viewmodel.selectedBackgroundType.backgroundColor,
        contentColor = viewmodel.selectedBackgroundType.contentColor
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(
                    start = true,
                    end = true,
                    bottom = false
                )  // when in landscape mode, apply end edge navigation bar padding) {
        ) {
            Column {
                Crossfade(
                    targetState = viewmodel.currentlyEditing,
                    animationSpec = spring()
                ) { currentlyEditing ->
                    when (currentlyEditing) {
                        Title -> {
                            TopAppBarWhenEditingTitle(
                                onBack = { onBackPress() },
                                onEditingComplete = {
                                    setEditingComplete(viewmodel, focusManager)
                                }
                            )
                        }
                        Note -> {
                            TopAppBarWhenEditingContent(
                                onBack = { onBackPress() },
                                enableUndo = viewmodel.enableUndo,
                                onUndo = viewmodel::undo,
                                enableRedo = viewmodel.enableRedo,
                                onRedo = viewmodel::redo,
                                onEditingComplete = {
                                    setEditingComplete(viewmodel, focusManager)
                                }
                            )
                        }
                        None -> {
                            TopAppBarWhenEditingNone(
                                onBack = { onBackPress() },
                                onShare = {},
                                onChangeNoteBackground = viewmodel::toggleNoteBackgroundChangerState
                            )
                        }
                    }
                }
                TitleTextField(
                    textFieldValue = viewmodel.titleTextFieldState,
                    onTitleChange = viewmodel::setOnTitleChange,
                    isFocused = viewmodel.currentlyEditing == Title,
                    onFocusChanged = {
                        if (it.isFocused) {
                            viewmodel.setCurrentlyEditingState(Title)
                        }
                    },
                    modifier = Modifier
                        .padding(start = 28.dp, end = 28.dp, bottom = 16.dp)
                        .fillMaxWidth()
                )
                NoteTextField(
                    textFieldValue = viewmodel.noteTextFieldState,
                    onNoteChange = viewmodel::setOnNoteChange,
                    isFocused = viewmodel.currentlyEditing == Note,
                    onFocusChanged = {
                        if (it.isFocused) {
                            viewmodel.setCurrentlyEditingState(Note)
                        }
                    },
                    shouldPadToNavigationBars = viewmodel.noteBackgroundChangerBottomSheetVisible,
                    onSoftKeyboardDismissed = {
                        viewmodel.setCurrentlyEditingState(None)
                    },
                    modifier = Modifier
                        .padding(start = 28.dp, top = 8.dp, end = 28.dp)
                        .fillMaxSize()
                        .navigationBarsWithImePadding()
                )
            }
            AnimatedVisibility(
                visible = viewmodel.noteBackgroundChangerBottomSheetVisible,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                BottomSheetNoteBackgroundChanger(
                    selectedBackground = viewmodel.selectedBackgroundType,
                    onBackgroundSelected = viewmodel::setOnBackgroundChange,
                    backgroundList = viewmodel.availableSingleColorBackgrounds()
                )
            }
        }
    }
}

private fun setEditingComplete(
    viewmodel: NoteDetailViewmodelImpl,
    focusManager: FocusManager
) {
    viewmodel.setCurrentlyEditingState(None)
    focusManager.clearFocus(true)
}

@Preview
@Composable
fun ChangeBackgroundItemPreview() {
    val changeBackgroundItemData = ChangeBackgroundItemData(
        isSelected = true,
        strokeColor = Orange500,
        backgroundType = SingleColor(Grey200, Grey500)
    )
    ChangeBackgroundItem(changeBackgroundItemData) {}
}

@Preview
@Composable
fun BottomSheetBackgroundChangerPreview() {
    val availableSingleColorBackgrounds = listOf(
        SingleColor(backgroundColor = WhiteMutated, contentColor = BlackMuted),
        SingleColor(backgroundColor = Blue200, contentColor = Blue500),
        SingleColor(backgroundColor = Parrot200, contentColor = Parrot500),
        SingleColor(backgroundColor = Pink200, contentColor = Pink500),
        SingleColor(backgroundColor = Grey200, contentColor = Grey500),
        SingleColor(backgroundColor = Green200, contentColor = Green500)
    )
    BottomSheetNoteBackgroundChanger(
        Modifier,
        availableSingleColorBackgrounds.random(),
        availableSingleColorBackgrounds
    ) {}
}