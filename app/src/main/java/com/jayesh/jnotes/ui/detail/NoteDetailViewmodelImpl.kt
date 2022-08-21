package com.jayesh.jnotes.ui.detail

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jayesh.jnotes.data.repository.NotesRepo
import com.jayesh.jnotes.data.repository.fileUtility.FileHelper
import com.jayesh.jnotes.ui.detail.NoteDetailViewmodelImpl.Action.CREATE
import com.jayesh.jnotes.ui.detail.NoteDetailViewmodelImpl.Action.NOTHING
import com.jayesh.jnotes.ui.models.Note
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
import kotlinx.coroutines.withContext
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Operation.DELETE
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Operation.EQUAL
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Operation.INSERT
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.util.LinkedList
import java.util.UUID
import javax.inject.Inject

const val UNDO_REDO_STACK_MAX_SIZE = 50

// TODO: inject dispatcher

@HiltViewModel
class NoteDetailViewmodelImpl @Inject constructor(
    private val repo: NotesRepo,
    private val fileHelper: FileHelper,
    savedStateHandle: SavedStateHandle
) : NoteDetailViewmodel() {

    private var noteId: String? = null
    private var oldNote: Note? = null
    val isNewNote get() = oldNote == null

    var currentlyEditing by mutableStateOf(CurrentlyEditing.None) // denotes what is being edited currently. It can be title, note or none
        private set
    var titleTextFieldState by mutableStateOf(TextFieldValue(""))
        private set
    var noteTextFieldState by mutableStateOf(TextFieldValue(""))
        private set
    var title by mutableStateOf("")
        private set
    var note by mutableStateOf("")
        private set

    private var lastCachedNote: String = ""

    var historyText: String = ""
        private set
    var noteSetupComplete: Boolean by mutableStateOf(false)
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
    var undoOrRedoClicked by mutableStateOf(false)

    val enableUndo get() = stackOfUndo.size > 0
    val enableRedo get() = stackOfRedo.size > 0

    init {
        val noteId = savedStateHandle.get<String>("noteId")
        loadNote(noteId)
        Timber.e("Init detail viewmodel")
    }

    override fun getNoteId(): String? = noteId

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
            setCurrentlyEditingState(CurrentlyEditing.Note)
            noteSetupComplete = true
        }
    }

    private fun setupNote(note: Note) {
        this.oldNote = note
        this.title = note.title
        this.note = note.content.text
        this.lastCachedNote = note.content.text
        selectedBackgroundType = BackgroundType.SingleColor(
            backgroundColor = note.config.backgroundColor,
            contentColor = note.config.contentColor
        )
        noteSetupComplete = true
    }

    private fun resetStateToDefault() {
        setCurrentlyEditingState(CurrentlyEditing.None)
        noteBackgroundChangerBottomSheetVisible = false
        forceClearCurrentFocus = true
        stackOfRedo.clear()
        stackOfUndo.clear()
    }

    fun setOnTitleChange(title: String) {
        this.title = title
    }

    fun setOnNoteChange(newNote: String) {
        note = newNote
    }

    fun cacheNoteChange(newNote: String) {
        val diffs = diffMatchPatch.diffMain(lastCachedNote, newNote)
        if (stackOfUndo.size == UNDO_REDO_STACK_MAX_SIZE) {
            stackOfUndo.removeFirst()
        }
        stackOfUndo.add(diffs)
        lastCachedNote = newNote
        Timber.d("cached: $newNote")
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
        undoOrRedoClicked = true
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
            historyText = stringBuilder.toString()
        }
    }

    fun redo() {
        undoOrRedoClicked = true
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
            historyText = stringBuilder.toString()
        }
    }

    fun setOnBackgroundChange(background: BackgroundType, shouldUpdateInDb: Boolean = false) {
        if (background is BackgroundType.SingleColor) {
            selectedBackgroundType = background
            if (shouldUpdateInDb) {
                noteId?.let { updateNote(it, getUpdatedNote()) }
            }
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
        Timber.e("updateNoteIfNeeded: decision: ${decision.name}")
    }

    private val contentEmpty
        get() = note.isBlank()

    private val titleEmpty
        get() = title.isBlank()

    private val oldNotesColorChanged
        get() = !isNewNote && (
                oldNote?.config?.backgroundColor != selectedBackgroundType.backgroundColor ||
                        oldNote?.config?.contentColor != selectedBackgroundType.contentColor)

    private val oldNotesTitleChanged
        get() = !isNewNote && oldNote?.title != title

    private val oldNotesContentChanged
        get() = !isNewNote && oldNote?.content?.text != note

    private fun getUpdatedNote() = Note(
        title = title,
        content = Note.NoteContent(note),
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

    suspend fun getBitmapFileUri(name: String, bitmap: ImageBitmap): Uri? {
        return runCatching {
            withContext(Dispatchers.IO) {
                deleteAllSavedScreenshotsPreviously()
                saveBitmap(name, bitmap)
            }
        }.onFailure {
            Timber.d(it, "error while saving bitmap")
        }.getOrNull()
    }

    private suspend fun deleteAllSavedScreenshotsPreviously() {
        withContext(Dispatchers.IO) {
            fileHelper.getNoteScreenshotDirectory().deleteRecursively()
        }
    }

    private fun saveBitmap(name: String, bitmap: ImageBitmap): Uri? {
        val file = File(fileHelper.getNoteScreenshotDirectory(), name)
        val stream = FileOutputStream(file)
        bitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
        return fileHelper.getFileUri(file)
    }

    private fun randomId() = UUID.randomUUID()?.toString() ?: System.currentTimeMillis().toString()

    enum class Action {
        CREATE, UPDATE, DELETE, NOTHING
    }

    override fun onCleared() {
        super.onCleared()
        Timber.e("onCleared detail viewmodel")
    }
}