package com.jayesh.jnotes.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jayesh.jnotes.ui.theme.BlackMuted
import com.jayesh.jnotes.ui.theme.Blue200
import com.jayesh.jnotes.ui.theme.Blue500
import com.jayesh.jnotes.ui.theme.Green200
import com.jayesh.jnotes.ui.theme.Green500
import com.jayesh.jnotes.ui.theme.Grey200
import com.jayesh.jnotes.ui.theme.Grey500
import com.jayesh.jnotes.ui.theme.Parrot200
import com.jayesh.jnotes.ui.theme.Parrot500
import com.jayesh.jnotes.ui.theme.Pink200
import com.jayesh.jnotes.ui.theme.Pink500
import com.jayesh.jnotes.ui.theme.WhiteMutated
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Operation.DELETE
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Operation.EQUAL
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Operation.INSERT
import java.util.*

private const val TAG = "NewOrEditNoteViewmodel"
const val UNDO_REDO_STACK_MAX_SIZE = 10

class NewOrEditNoteViewmodel(savedStateHandle: SavedStateHandle) : ViewModel() {
    var currentlyEditing by mutableStateOf(CurrentlyEditing.Note) // denotes what is being edited currently. It can be title, note or none
        private set
    var titleTextFieldState by mutableStateOf(TextFieldValue(""))
        private set
    var noteTextFieldState by mutableStateOf(TextFieldValue(""))
        private set
    var selectedBackgroundType by mutableStateOf(availableSingleColorBackgrounds().first())
        private set
    var bottomSheetNoteBackgroundChangerVisible by mutableStateOf(false)
        private set

    private val diffMatchPatch = DiffMatchPatch()

    private var stackOfUndo = mutableStateListOf<LinkedList<DiffMatchPatch.Diff>>()
    private var stackOfRedo = mutableStateListOf<LinkedList<DiffMatchPatch.Diff>>()

    val enableUndo get() = stackOfUndo.size > 0
    val enableRedo get() = stackOfRedo.size > 0

    fun setOnTitleChange(textFieldValue: TextFieldValue) {
        titleTextFieldState = textFieldValue
    }

    private fun setNoteText(text: String) {
        noteTextFieldState = noteTextFieldState.copy(text = text)
    }

    fun setOnNoteChange(textFieldValue: TextFieldValue) {
        if (textFieldValue.text == noteTextFieldState.text) {
            noteTextFieldState = textFieldValue.copy() // when only cursor position changes, we do not want a recomposition
            return
        }
        val diffs = diffMatchPatch.diffMain(
            noteTextFieldState.text, textFieldValue.text
        )
        if (stackOfUndo.size == UNDO_REDO_STACK_MAX_SIZE) {
            stackOfUndo.removeFirst()
        }
        stackOfUndo.add(diffs)
        noteTextFieldState = textFieldValue
    }

    fun setCurrentlyEditingState(editingMode: CurrentlyEditing) {
        currentlyEditing = editingMode
        if ((editingMode == CurrentlyEditing.Title || editingMode == CurrentlyEditing.Note)
            && bottomSheetNoteBackgroundChangerVisible
        ) {
            viewModelScope.launch {
                delay(500)
                toggleNoteBackgroundChangerState()
            }
        }
    }

    fun toggleNoteBackgroundChangerState() {
        bottomSheetNoteBackgroundChangerVisible = !bottomSheetNoteBackgroundChangerVisible
    }

    fun undo() {
        if (stackOfUndo.isNotEmpty()) {
            val diffs = stackOfUndo.removeLast()
            val stringBuilder = StringBuilder()
            diffs.forEach { change ->
                if (change.operation == EQUAL || change.operation == DELETE) {
                    stringBuilder.append(change.text)
                }
            }
            if (stackOfRedo.size == UNDO_REDO_STACK_MAX_SIZE) {
                stackOfRedo.removeFirst()
            }
            stackOfRedo.add(diffs)
            setNoteText(stringBuilder.toString())
        }
    }

    fun redo() {
        if (stackOfRedo.isNotEmpty()) {
            val diffs = stackOfRedo.removeLast()
            val stringBuilder = StringBuilder()
            diffs.forEach { change ->
                if (change.operation == EQUAL || change.operation == INSERT) {
                    stringBuilder.append(change.text)
                }
            }
            if (stackOfUndo.size == UNDO_REDO_STACK_MAX_SIZE) {
                stackOfUndo.removeFirst()
            }
            stackOfUndo.add(diffs)
            setNoteText(stringBuilder.toString())
        }
    }

    fun setOnBackgroundChange(background: BackgroundType) {
        if (background is BackgroundType.SingleColor) {
            selectedBackgroundType = background
        }
    }

    fun availableSingleColorBackgrounds(): List<BackgroundType.SingleColor> {
        return listOf(
            BackgroundType.SingleColor(backgroundColor = WhiteMutated, contentColor = BlackMuted),
            BackgroundType.SingleColor(backgroundColor = Blue200, contentColor = Blue500),
            BackgroundType.SingleColor(backgroundColor = Parrot200, contentColor = Parrot500),
            BackgroundType.SingleColor(backgroundColor = Pink200, contentColor = Pink500),
            BackgroundType.SingleColor(backgroundColor = Grey200, contentColor = Grey500),
            BackgroundType.SingleColor(backgroundColor = Green200, contentColor = Green500)
        )
    }
}