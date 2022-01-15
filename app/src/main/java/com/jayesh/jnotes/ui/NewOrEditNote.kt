package com.jayesh.jnotes.ui

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.jayesh.jnotes.R
import com.jayesh.jnotes.ui.BackgroundType.SingleColor
import com.jayesh.jnotes.ui.CurrentlyEditing.None
import com.jayesh.jnotes.ui.CurrentlyEditing.Note
import com.jayesh.jnotes.ui.CurrentlyEditing.Title
import com.jayesh.jnotes.ui.theme.BlackMuted
import com.jayesh.jnotes.ui.theme.Blue200
import com.jayesh.jnotes.ui.theme.Blue500
import com.jayesh.jnotes.ui.theme.Green200
import com.jayesh.jnotes.ui.theme.Green500
import com.jayesh.jnotes.ui.theme.Grey200
import com.jayesh.jnotes.ui.theme.Grey500
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
fun NewOrEditNote(viewmodel: NewOrEditNoteViewmodel = viewModel()) {
    val focusManager = LocalFocusManager.current
    val dispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

    Column(
        modifier = Modifier
            .background(viewmodel.selectedBackgroundType.backgroundColor)
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(
                start = true,
                end = true,
                bottom = false
            ) // when in landscape mode, apply end edge navigation bar padding
    ) {
        when (viewmodel.currentlyEditing) {
            Title -> {
                TopAppBarWhenEditingTitle(
                    onBack = { dispatcher.onBackPressed() },
                    onEditingComplete = {
                        setEditingComplete(viewmodel, focusManager)
                    },
                    contentColor = viewmodel.selectedBackgroundType.contentColor
                )
            }
            Note -> {
                TopAppBarWhenEditingContent(
                    onBack = { dispatcher.onBackPressed() },
                    enableUndo = viewmodel.enableUndo,
                    onUndo = viewmodel::undo,
                    enableRedo = viewmodel.enableRedo,
                    onRedo = viewmodel::redo,
                    onEditingComplete = {
                        setEditingComplete(viewmodel, focusManager)
                    },
                    contentColor = viewmodel.selectedBackgroundType.contentColor
                )
            }
            None -> {
                TopAppBarWhenEditingNone(
                    onBack = { dispatcher.onBackPressed() },
                    onShare = {},
                    onChangeNoteBackground = viewmodel::toggleNoteBackgroundChangerState,
                    contentColor = viewmodel.selectedBackgroundType.contentColor
                )
            }
        }
        Box {
            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                TitleTextField(
                    textFieldValue = viewmodel.titleTextFieldState,
                    onTitleChange = viewmodel::setOnTitleChange,
                    isFocused = viewmodel.currentlyEditing == Title,
                    onFocusChanged = {
                        if (it.isFocused) {
                            viewmodel.setCurrentlyEditingState(Title)
                        }
                    },
                    contentColor = viewmodel.selectedBackgroundType.contentColor
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
                    contentColor = viewmodel.selectedBackgroundType.contentColor,
                    shouldPadToNavigationBars = viewmodel.bottomSheetNoteBackgroundChangerVisible,
                )
            }
            if (viewmodel.bottomSheetNoteBackgroundChangerVisible) {
                BottomSheetNoteBackgroundChanger(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    selectedBackground = viewmodel.selectedBackgroundType,
                    onBackgroundSelected = viewmodel::setOnBackgroundChange,
                    backgroundList = viewmodel.availableSingleColorBackgrounds()
                )
            }
        }
    }
}

private fun setEditingComplete(
    viewmodel: NewOrEditNoteViewmodel,
    focusManager: FocusManager
) {
    viewmodel.setCurrentlyEditingState(None)
    focusManager.clearFocus(true)
}

@Composable
fun BasicTopAppBar(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    backgroundColor: Color,
    contentColor: Color,
    rightSideContentSlot: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Row(
            modifier = modifier
                .background(backgroundColor)
                .padding(start = 11.dp, end = 11.dp, top = 2.dp)
                .height(56.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack, modifier = Modifier.fillMaxHeight()) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_back_24),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            rightSideContentSlot()
        }
    }
}

@Composable
fun TopAppBarWhenEditingNone(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colors.onBackground,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onChangeNoteBackground: () -> Unit
) {
    BasicTopAppBar(
        modifier = modifier,
        onBack = onBack,
        backgroundColor = backgroundColor,
        contentColor = contentColor
    ) {
        Row {
            IconButton(onClick = onShare) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_ios_share_black_24dp),
                    contentDescription = null
                )
            }
            IconButton(onClick = onChangeNoteBackground) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_outline_color_lens_24),
                    contentDescription = null
                )
            }
        }

    }
}

@Composable
fun TopAppBarWhenEditingTitle(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colors.onBackground,
    onBack: () -> Unit,
    onEditingComplete: () -> Unit
) {
    BasicTopAppBar(
        modifier = modifier,
        onBack = onBack,
        backgroundColor = backgroundColor,
        contentColor = contentColor
    ) {
        Row {
            IconButton(onClick = onEditingComplete) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_done_24),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun TopAppBarWhenEditingContent(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colors.onBackground,
    onBack: () -> Unit,
    enableUndo: Boolean,
    onUndo: () -> Unit,
    enableRedo: Boolean,
    onRedo: () -> Unit,
    onEditingComplete: () -> Unit
) {
    BasicTopAppBar(
        modifier = modifier,
        onBack = onBack,
        backgroundColor = backgroundColor,
        contentColor = contentColor
    ) {
        Row {
            IconButton(onClick = onUndo, enabled = enableUndo) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_undo_24),
                    contentDescription = null
                )
            }
            IconButton(onClick = onRedo, enabled = enableRedo) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_redo_24),
                    contentDescription = null
                )
            }
            IconButton(onClick = onEditingComplete) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_done_24),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun TitleTextField(
    textFieldValue: TextFieldValue,
    onTitleChange: (TextFieldValue) -> Unit,
    isFocused: Boolean,
    onFocusChanged: (FocusState) -> Unit,
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colors.onBackground
) {
    val focusRequester = remember { FocusRequester() }
    val currentFocusChanged by rememberUpdatedState(newValue = onFocusChanged)
    val currentIsFocused by rememberUpdatedState(newValue = isFocused)

    val commonTextStyle = MaterialTheme.typography.h5.copy(color = contentColor)
    val customTextSelectionColors = remember(key1 = contentColor) {
        TextSelectionColors(
            handleColor = contentColor,
            backgroundColor = contentColor.copy(alpha = 0.2f)
        )
    }

    if (currentIsFocused) {
        LaunchedEffect(key1 = currentIsFocused) {
            focusRequester.requestFocus()
        }
    }

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = onTitleChange,
            modifier = Modifier
                .background(backgroundColor)
                .padding(start = 28.dp, end = 28.dp, bottom = 16.dp)
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { currentFocusChanged(it) },
            textStyle = commonTextStyle,
            cursorBrush = SolidColor(MaterialTheme.colors.onSurface),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
            ),
            decorationBox = {
                if (textFieldValue.text.isBlank()) {
                    Text(
                        text = stringResource(R.string.title),
                        style = commonTextStyle,
                        color = contentColor.copy(0.6f)
                    )
                }
                it()
            },
        )
    }
}

@Composable
fun NoteTextField(
    textFieldValue: TextFieldValue,
    onNoteChange: (TextFieldValue) -> Unit,
    isFocused: Boolean,
    onFocusChanged: (FocusState) -> Unit,
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colors.onBackground,
    shouldPadToNavigationBars: Boolean = false,
) {
    val focusRequester = remember { FocusRequester() }

    val currentFocusChanged by rememberUpdatedState(newValue = onFocusChanged)
    val currentIsFocused by rememberUpdatedState(newValue = isFocused)
    val currentShouldPadToNavigationBars by rememberUpdatedState(newValue = shouldPadToNavigationBars)

    val commonTextStyle = MaterialTheme.typography.body1.copy(lineHeight = 28.sp, fontSize = 18.sp, color = contentColor)
    val customTextSelectionColors = remember(key1 = contentColor) {
        TextSelectionColors(
            handleColor = contentColor,
            backgroundColor = contentColor.copy(alpha = 0.2f)
        )
    }

    if (currentIsFocused) {
        LaunchedEffect(key1 = currentIsFocused) {
            focusRequester.requestFocus()
        }
    }
    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = onNoteChange,
            modifier = Modifier
                .background(backgroundColor)
                .padding(start = 28.dp, top = 8.dp, end = 28.dp)
                .navigationBarsPadding(bottom = currentShouldPadToNavigationBars)
                .fillMaxSize()
                .focusRequester(focusRequester)
                .onFocusChanged { currentFocusChanged(it) },
            textStyle = commonTextStyle,
            cursorBrush = SolidColor(MaterialTheme.colors.onSurface),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
            ),
            decorationBox = {
                if (textFieldValue.text.isBlank()) {
                    Text(
                        text = stringResource(R.string.start_typing),
                        style = commonTextStyle,
                        color = contentColor.copy(0.6f)
                    )
                }
                it()
            },
        )
    }
}

@Composable
fun BottomSheetNoteBackgroundChanger(
    modifier: Modifier = Modifier,
    selectedBackground: BackgroundType,
    backgroundList: List<BackgroundType>,
    onBackgroundSelected: (BackgroundType) -> Unit
) {
    val rowStartEndOffset = 20.dp
    val surfaceBackgroundColor = if (selectedBackground is SingleColor) selectedBackground.backgroundColor
    else MaterialTheme.colors.onSurface

    Column(modifier = modifier) {
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = (0.2f).dp
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clipToBounds(),
            color = surfaceBackgroundColor
        ) {
            LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
                item {
                    Spacer(modifier = Modifier.width(rowStartEndOffset))
                }
                items(backgroundList) { singleColorBackground ->
                    val isSelected = singleColorBackground == selectedBackground
                    val changeBackgroundItemData = ChangeBackgroundItemData(
                        isSelected = isSelected,
                        backgroundType = singleColorBackground
                    )
                    ChangeBackgroundItem(changeBackgroundItemData, onBackgroundSelected)
                }
                item {
                    Spacer(modifier = Modifier.width(rowStartEndOffset))
                }
            }
        }
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = (0.2f).dp,
            color = Color.Gray.copy(alpha = 0.7f)
        )
        Spacer(Modifier.navigationBarsHeight())
    }
}

@Composable
fun ChangeBackgroundItem(
    itemData: ChangeBackgroundItemData,
    onBackgroundSelected: (BackgroundType) -> Unit
) {
    val cardShape = RoundedCornerShape(13.dp)
    val cardHeight = 160.dp
    val cardWidth = 114.dp

    val cardBasicModifier = Modifier
        .height(cardHeight)
        .width(cardWidth)
        .padding(8.dp)
        .clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            onBackgroundSelected(itemData.backgroundType)
        }

    if (itemData.isSelected) {
        Surface(
            modifier = Modifier
                .size(cardWidth, cardHeight)
                .border(3.dp, itemData.strokeColor, cardShape),
            color = Color.Transparent
        ) {
            BackgroundCardItem(
                modifier = cardBasicModifier,
                shape = cardShape,
                backgroundType = itemData.backgroundType,
                contentColor = itemData.derivedContentColor
            )
        }
    } else {
        BackgroundCardItem(
            modifier = cardBasicModifier,
            shape = cardShape,
            backgroundType = itemData.backgroundType,
            contentColor = itemData.derivedContentColor
        )
    }
}

@Composable
private fun BackgroundCardItem(
    modifier: Modifier,
    shape: Shape,
    backgroundType: BackgroundType,
    contentColor: Color?,
    elevation: Dp = (0f).dp
) {
    Card(
        modifier = modifier,
        shape = shape,
        border = BorderStroke(
            width = (0.1f).dp,
            color = if (backgroundType is SingleColor)
                backgroundType.contentColor.copy(.25f) else Color.Gray.copy(.25f)
        ),
        elevation = elevation,
        backgroundColor = if (backgroundType is SingleColor)
            backgroundType.backgroundColor else Color.Transparent
    ) {
        if (backgroundType is SingleColor) {
            Icon(
                painter = painterResource(id = R.drawable.ic_left_alignment),
                tint = contentColor ?: MaterialTheme.colors.onSurface,
                contentDescription = "Change background",
                modifier = Modifier.wrapContentSize()
            )
        } else {
            Image(painter = painterResource(id = R.drawable.ic_launcher_background), contentDescription = null)
        }
    }
}

data class ChangeBackgroundItemData(
    val isSelected: Boolean,
    val backgroundType: BackgroundType,
    val strokeColor: Color = Orange500
) {
    val derivedContentColor: Color?
        get() {
            return if (backgroundType is SingleColor) {
                if (isSelected) {
                    backgroundType.contentColor
                } else {
                    backgroundType.contentColor.copy(0.5f)
                }
            } else {
                return null
            }
        }
}

sealed class BackgroundType {
    data class SingleColor(val backgroundColor: Color, val contentColor: Color) : BackgroundType()
    data class Image(val image: String) : BackgroundType()
}

enum class CurrentlyEditing {
    Title,
    Note,
    None;
}

@Preview(showBackground = true)
@Composable
fun NewOrEditNotePreview() {
    NewOrEditNote()
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
    BottomSheetNoteBackgroundChanger(Modifier, availableSingleColorBackgrounds.random(), availableSingleColorBackgrounds) {}
}