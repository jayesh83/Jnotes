package com.jayesh.jnotes.ui.noteDetail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jayesh.jnotes.data.repository.NotesRepo
import com.jayesh.jnotes.ui.models.Note
import com.jayesh.jnotes.ui.noteDetail.NoteDetailViewmodelImpl.Action.CREATE
import com.jayesh.jnotes.ui.noteDetail.NoteDetailViewmodelImpl.Action.NOTHING
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Operation.DELETE
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Operation.EQUAL
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Operation.INSERT
import java.util.LinkedList
import java.util.UUID
import javax.inject.Inject

private const val TAG = "NewOrEditNoteViewmodel"
const val UNDO_REDO_STACK_MAX_SIZE = 10

@HiltViewModel
class NoteDetailViewmodelImpl @Inject constructor(
    private val repo: NotesRepo,
    savedStateHandle: SavedStateHandle
) : NoteDetailViewmodel() {

    private var noteId: String? = null
    private var oldNote: Note? = null
    private val isNewNote get() = oldNote == null

    var currentlyEditing by mutableStateOf(CurrentlyEditing.None) // denotes what is being edited currently. It can be title, note or none
        private set
    var titleTextFieldState by mutableStateOf(TextFieldValue(""))
        private set
    var noteTextFieldState by mutableStateOf(TextFieldValue(""))
        private set
    var selectedBackgroundType by mutableStateOf(availableSingleColorBackgrounds().first())
        private set
    var noteBackgroundChangerBottomSheetVisible by mutableStateOf(false)
        private set
    var forceClearCurrentFocus by mutableStateOf(false)
        private set

    private val diffMatchPatch = DiffMatchPatch()

    private var stackOfUndo = mutableStateListOf<LinkedList<DiffMatchPatch.Diff>>()
    private var stackOfRedo = mutableStateListOf<LinkedList<DiffMatchPatch.Diff>>()

    val enableUndo get() = stackOfUndo.size > 0
    val enableRedo get() = stackOfRedo.size > 0

    init {
        val noteId = savedStateHandle.get<String>("noteId")
        loadNote(noteId)
        Log.e(TAG, "Init detail viewmodel")
    }

    override fun loadNote(noteId: String?) {
        this.noteId = noteId
        if (noteId != null) {
            viewModelScope.launch {
                repo.getNote(noteId)?.also { note ->
                    setupNote(note)
                    resetStateToDefault()
                }
            }
        } else {
            currentlyEditing = CurrentlyEditing.Note
        }
    }

    private fun resetStateToDefault() {
        setCurrentlyEditingState(CurrentlyEditing.None)
        noteBackgroundChangerBottomSheetVisible = false
        forceClearCurrentFocus = true
        stackOfRedo.clear()
        stackOfUndo.clear()
    }

    private fun setupNote(note: Note) {
        this.oldNote = note
        titleTextFieldState = titleTextFieldState.copy(text = note.title)
        noteTextFieldState = noteTextFieldState.copy(
            text = note.content.text
        )
        selectedBackgroundType = BackgroundType.SingleColor(
            backgroundColor = note.config.backgroundColor,
            contentColor = note.config.contentColor
        )
    }

    fun setOnTitleChange(textFieldValue: TextFieldValue) {
        titleTextFieldState = textFieldValue
    }

    private fun setNoteText(text: String) {
        noteTextFieldState = noteTextFieldState.copy(text = text)
    }

    fun setOnNoteChange(textFieldValue: TextFieldValue) {
        if (textFieldValue.text == noteTextFieldState.text) {
            noteTextFieldState =
                textFieldValue.copy() // when only cursor position changes, we do not want a recomposition
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
            && noteBackgroundChangerBottomSheetVisible
        ) {
            toggleNoteBackgroundChangerState()
        }
    }

    fun toggleNoteBackgroundChangerState() {
        noteBackgroundChangerBottomSheetVisible = !noteBackgroundChangerBottomSheetVisible
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

    fun updateNoteIfNeeded() {
        val decision = whatToDoWithThisNote()
        if (decision in arrayOf(CREATE, Action.UPDATE, Action.DELETE)) {
            if (decision == CREATE)
                saveNote(getUpdatedNote())
            if (decision == Action.UPDATE)
                noteId?.let { noteId -> updateNote(noteId, getUpdatedNote()) }
            if (decision == Action.DELETE)
                noteId?.let { noteId -> deleteNote(noteId) }
        }
        Log.e(TAG, "updateNoteIfNeeded: decision: ${decision.name}")
    }

    private val contentEmpty
        get() = noteTextFieldState.text.isBlank()

    private val titleEmpty
        get() = titleTextFieldState.text.isBlank()

    private val oldNotesColorChanged
        get() = !isNewNote && (
                oldNote?.config?.backgroundColor != selectedBackgroundType.backgroundColor ||
                        oldNote?.config?.contentColor != selectedBackgroundType.contentColor)

    private val oldNotesTitleChanged
        get() = !isNewNote && oldNote?.title != titleTextFieldState.text

    private val oldNotesContentChanged
        get() = !isNewNote && oldNote?.content?.text != noteTextFieldState.text


    private fun getUpdatedNote() = Note(
        title = titleTextFieldState.text,
        content = Note.NoteContent(noteTextFieldState.text),
        config = Note.NoteConfig(
            backgroundColor = selectedBackgroundType.backgroundColor,
            contentColor = selectedBackgroundType.contentColor,
            syncStatus = Note.SyncStatus.NOT_SYNCED
        ),
        id = noteId ?: randomId()
    )

    private fun whatToDoWithThisNote(): Action {
        return when {
            (isNewNote && !contentEmpty) || (isNewNote && !titleEmpty) -> CREATE
            ((oldNotesContentChanged && !contentEmpty) || (oldNotesContentChanged && contentEmpty && !titleEmpty)) ||
                    ((oldNotesTitleChanged && !titleEmpty) || (oldNotesTitleChanged && titleEmpty && !contentEmpty)) ||
                    oldNotesColorChanged -> Action.UPDATE
            !isNewNote && contentEmpty && titleEmpty -> Action.DELETE
            else -> NOTHING
        }
    }


    override fun saveNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.saveNote(note)
        }
    }

    override fun updateNote(id: String, note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.editNote(id, note)
        }
    }

    override fun deleteNote(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteNote(id)
        }
    }

    private fun randomId() = UUID.randomUUID()?.toString() ?: System.currentTimeMillis().toString()

    enum class Action {
        CREATE, UPDATE, DELETE, NOTHING
    }

    override fun onCleared() {
        super.onCleared()
        Log.e(TAG, "onCleared detail viewmodel")
    }
}