package com.jayesh.jnotes.ui.detail

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.TextViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavController
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.jayesh.jnotes.R
import com.jayesh.jnotes.ui.Screen
import com.jayesh.jnotes.ui.detail.BackgroundType.SingleColor
import com.jayesh.jnotes.ui.detail.CurrentlyEditing.None
import com.jayesh.jnotes.ui.detail.CurrentlyEditing.Note
import com.jayesh.jnotes.ui.detail.CurrentlyEditing.Title
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
import com.jayesh.jnotes.util.PlatformUtils.afterTextChangedDelayed
import timber.log.Timber
import kotlin.math.roundToInt


private const val TAG = "NewOrEditNote"

// TODO: 04/01/22 scroll title along with note
// FIXME: 04/01/22 textfield text below keyboard
// FIXME: entire screen is being recomposed on each text typing

@Composable
fun NoteDetailScreen(
    navController: NavController,
    viewmodel: NoteDetailViewmodelImpl,
    showingInMasterDetailUI: Boolean = false
) {
    val density = LocalDensity.current

    fun goBack() = navController.navigateUp()

    fun navigateToNoteShare(noteId: String? = viewmodel.getNoteId()) {
        // note: if its old note, commit any changes as we'll fetch fresh data from db in note share screen
        // and do not commit if its new note we'll use the viewModel itself, will not fetch from db
        if (!showingInMasterDetailUI && noteId != null) {
            viewmodel.updateNoteIfNeeded()
        }
        navController.navigate(Screen.NoteShare.createRoute(noteId))
    }

    fun navigateToFullScreenNoteDetail(noteId: String? = viewmodel.getNoteId()) {
        navController.navigate(Screen.NoteEditGraph.createRoute(noteId)) {
            launchSingleTop = true
        }
    }

    val focusManager = LocalFocusManager.current
    if (viewmodel.forceClearCurrentFocus)
        focusManager.clearFocus(true)

    fun onBackPress() {
        if (viewmodel.noteBackgroundChangerBottomSheetVisible) {
            viewmodel.toggleNoteBackgroundChangerState()
        } else {
            focusManager.clearFocus(true)
            viewmodel.updateNoteIfNeeded()
            goBack()
        }
    }

    BackHandler(enabled = showingInMasterDetailUI.not()) {
        Timber.e("NewOrEditNoteScreen: BackHandler called")
        onBackPress()
    }

    val horizontalStartSpacing = remember { if (showingInMasterDetailUI) 20.dp else 28.dp }
    val horizontalEndSpacing = remember { if (showingInMasterDetailUI) 14.dp else 28.dp }

    val showNavigationIcon by derivedStateOf { showingInMasterDetailUI.not() }
    val textFieldEditable by derivedStateOf { showingInMasterDetailUI.not() }

    JnotesTheme(
        backgroundColor = viewmodel.selectedBackgroundType.backgroundColor,
        contentColor = viewmodel.selectedBackgroundType.contentColor
    ) {
        var containerModifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
            .statusBarsPadding()

        if (showingInMasterDetailUI.not()) {
            containerModifier = containerModifier.then(
                Modifier.navigationBarsPadding(
                    start = true,
                    end = true,
                    bottom = false
                )
            )
        } else {
            containerModifier = containerModifier.then(
                Modifier.clickable(
                    onClick = ::navigateToFullScreenNoteDetail,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                )
            )
        }

        Box(modifier = containerModifier) {
            Column {
                Crossfade(
                    targetState = viewmodel.currentlyEditing,
                    animationSpec = spring()
                ) { currentlyEditing ->
                    when (currentlyEditing) {
                        Title -> {
                            TopAppBarWhenEditingTitle(
                                showNavigationIcon = showNavigationIcon,
                                onBack = ::onBackPress,
                                onEditingComplete = {
                                    setEditingComplete(viewmodel, focusManager)
                                }
                            )
                        }
                        Note -> {
                            TopAppBarWhenEditingContent(
                                showNavigationIcon = showNavigationIcon,
                                onBack = ::onBackPress,
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
                                showNavigationIcon = showNavigationIcon,
                                onBack = ::onBackPress,
                                onShare = ::navigateToNoteShare,
                                onChangeNoteBackground = viewmodel::toggleNoteBackgroundChangerState
                            )
                        }
                    }
                }
/*
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
                        .padding(
                            start = horizontalStartSpacing,
                            end = horizontalEndSpacing,
                            bottom = 16.dp
                        )
                        .fillMaxWidth(),
                    enabled = textFieldEditable,
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
                        .padding(
                            start = horizontalStartSpacing,
                            end = horizontalEndSpacing,
                            top = 8.dp
                        )
                        .fillMaxSize(),
                    editable = textFieldEditable,
                )
*/

                val titlePlaceholder = stringResource(R.string.title)
                val notePlaceholder = stringResource(R.string.start_typing)

                val context = LocalContext.current
                val titleNoteView = remember {
                    val titleField = buildTitleEditText(
                        context = context,
                        text = viewmodel.title,
                        onTitleChange = viewmodel::setOnTitleChange,
                        placeholder = titlePlaceholder,
                        focusChangeListener = { _, hasFocus ->
                            if (hasFocus) {
                                viewmodel.setCurrentlyEditingState(Title)
                            }
                        },
                        paddingValues = with(density) {
                            PaddingValues(
                                left = horizontalStartSpacing.roundToPx(),
                                right = horizontalEndSpacing.roundToPx(),
                                bottom = 16.dp.roundToPx()
                            )
                        },
                    )

                    val noteField = buildNoteEditText(
                        context = context,
                        text = viewmodel.note,
                        onNoteChange = viewmodel::setOnNoteChange,
                        afterTextChangedDelayed = viewmodel::cacheNoteChange,
                        placeholder = notePlaceholder,
                        focusChangeListener = { _, hasFocus ->
                            if (hasFocus) {
                                viewmodel.setCurrentlyEditingState(Note)
                            }
                        },
                        paddingValues = with(density) {
                            PaddingValues(
                                left = horizontalStartSpacing.roundToPx(),
                                top = with(density) { 8.dp.roundToPx() },
                                right = horizontalEndSpacing.roundToPx(),
                                bottom = 64.dp.roundToPx()
                            )
                        }
                    )

                    val linearLayout = LinearLayout(context).apply {
                        orientation = LinearLayout.VERTICAL
                        addView(titleField)
                        addView(noteField)
                    }

                    ScrollView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        isFillViewport = true
                        scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
                        overScrollMode = View.OVER_SCROLL_NEVER
                        addView(linearLayout)
                    }
                }

                AndroidView(
                    factory = { titleNoteView },
                    /*update = {
                        Timber.e("scrollView update update")
                        val noteEditText = it.rootView.findViewWithTag<EditText>("note")
                        if (viewmodel.lastHistoryText.isNotEmpty()) {
                            noteEditText.clearFocus()
                        }
                        noteEditText.setText(viewmodel.lastHistoryText)
                        noteEditText.setSelection(viewmodel.lastHistoryText.length)
                        *//*val color = Color(0xFFE06F5B).toArgb()
                        noteEditText.setTextColor(color)
                        noteEditText.highlightColor = Color(0x22E06F5B).toArgb()*//*
                        //noteEditText.setBackgroundColor(color)
                    }*/
                )

                with(viewmodel) {
                    LaunchedEffect(key1 = Unit) {
                        if (!noteSetupComplete) return@LaunchedEffect
                        val noteEditText = titleNoteView.rootView.findViewWithTag<EditText>("note")
                        val titleEditText = titleNoteView.rootView.findViewWithTag<EditText>("title")
                        if (isNewNote) {
                            noteEditText.requestFocus()
                            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)?.showSoftInput(
                                noteEditText,
                                InputMethodManager.SHOW_FORCED
                            )
                        } else {
                            noteEditText.setText(note)
                            titleEditText.setText(title)
                        }
                    }
                    LaunchedEffect(key1 = undoOrRedoClicked) {
                        if (!undoOrRedoClicked) return@LaunchedEffect
                        val noteEditText = titleNoteView.rootView.findViewWithTag<EditText>("note")
                        noteEditText.clearFocus()
                        noteEditText.setText(historyText)
                        noteEditText.setSelection(historyText.length)
                        undoOrRedoClicked = false
                    }
                }
            }

            AnimatedVisibility(
                visible = viewmodel.noteBackgroundChangerBottomSheetVisible,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                BottomSheetNoteBackgroundChanger(
                    selectedBackground = viewmodel.selectedBackgroundType,
                    onBackgroundSelected = { backgroundType ->
                        viewmodel.setOnBackgroundChange(
                            background = backgroundType,
                            shouldUpdateInDb = showingInMasterDetailUI
                        )
                    },
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

private fun buildTitleEditText(
    context: Context,
    text: String = "",
    onTitleChange: (String) -> Unit,
    afterTextChangedDelayed: ((String) -> Unit)? = null,
    placeholder: String,
    focusChangeListener: View.OnFocusChangeListener,
    paddingValues: Paddings,
    layoutParams: ViewGroup.LayoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
): EditText {
    return EditText(context).apply {
        tag = "title"
        this.layoutParams = layoutParams
        setPadding(paddingValues.left, paddingValues.top, paddingValues.right, paddingValues.bottom)
        typeface = Typeface.DEFAULT
        textSize = 24f
        letterSpacing = 0f
        gravity = Gravity.START
        background = null
        inputType = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
        imeOptions = EditorInfo.IME_FLAG_NO_FULLSCREEN
        hint = placeholder
        setText(text)
        onFocusChangeListener = focusChangeListener
        addTextChangedListener { editableText ->
            if (editableText != null) {
                onTitleChange(editableText.toString())
            }
        }
/*
        if (afterTextChangedDelayed != null) {
            afterTextChangedDelayed(afterTextChangedDelayed)
        }
*/
    }
}

private fun buildNoteEditText(
    context: Context,
    text: String = "",
    onNoteChange: (String) -> Unit,
    afterTextChangedDelayed: ((String, Int) -> Unit)? = null,
    placeholder: String,
    focusChangeListener: View.OnFocusChangeListener,
    paddingValues: Paddings,
    layoutParams: ViewGroup.LayoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
): EditText {
    return EditText(context).apply {
        val noteLineHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 28F, context.resources.displayMetrics).roundToInt()
        tag = "note"
        this.layoutParams = layoutParams
        setPadding(paddingValues.left, paddingValues.top, paddingValues.right, paddingValues.bottom)
        typeface = Typeface.DEFAULT
        textSize = 18f
        TextViewCompat.setLineHeight(this, noteLineHeight)
        gravity = Gravity.START
        background = null
        inputType = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
        imeOptions = EditorInfo.IME_FLAG_NO_FULLSCREEN
        hint = placeholder
        setText(text)
        onFocusChangeListener = focusChangeListener
        addTextChangedListener { editableText ->
            /*if (editableText != null && hasFocus()) {
                onNoteChange(editableText.toString())
            }*/
            if (editableText != null) {
                onNoteChange(editableText.toString())
            }
        }
        if (afterTextChangedDelayed != null) {
            afterTextChangedDelayed(afterTextChangedDelayed)
        }
    }
}

data class Paddings(val left: Int = 0, val top: Int = 0, val right: Int = 0, val bottom: Int = 0)

fun PaddingValues(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) = Paddings(left, top, right, bottom)

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