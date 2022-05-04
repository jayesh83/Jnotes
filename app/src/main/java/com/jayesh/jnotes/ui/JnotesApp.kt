package com.jayesh.jnotes.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.jayesh.jnotes.ui.Screen.NoteEdit
import com.jayesh.jnotes.ui.Screen.NoteEditGraph
import com.jayesh.jnotes.ui.Screen.Notes
import com.jayesh.jnotes.ui.noteDetail.NoteDetailScreen
import com.jayesh.jnotes.ui.notes.HomeScreen
import com.jayesh.jnotes.ui.sharenote.ShareNoteScreen

@Composable
fun JnotesApp() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Notes.route
    ) {
        composable(route = Notes.route) {
            HomeScreen(
                navController = navController,
                notesViewModel = hiltViewModel(),
                noteDetailViewModel = hiltViewModel()
            )
        }

        navigation(startDestination = NoteEdit.route, route = NoteEditGraph.route) {
            composable(
                route = NoteEdit.route,
                arguments = listOf(
                    navArgument("noteId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val parent = remember(backStackEntry) {
                    navController.getBackStackEntry(route = NoteEditGraph.route)
                }
                NoteDetailScreen(
                    navController = navController,
                    viewmodel = hiltViewModel(viewModelStoreOwner = parent)
                )
            }

            composable(route = Screen.NoteShare.route) { backStackEntry ->
                val parent = remember(backStackEntry) {
                    navController.getBackStackEntry(route = NoteEditGraph.route)
                }
                ShareNoteScreen(
                    navController = navController,
                    noteDetailViewModel = hiltViewModel(viewModelStoreOwner = parent)
                )
            }
        }
    }
}

sealed class Screen(val route: String) {

    object Notes : Screen("notes")

    object NoteEditGraph : Screen("notes/view_or_edit_graph?noteId={noteId}") {
        // Note: null noteId denotes a new note to be created
        fun createRoute(noteId: String?): String {
            return if (noteId != null) "notes/view_or_edit_graph?noteId=$noteId" else "notes/view_or_edit_graph"
        }
    }

    object NoteEdit : Screen("notes/view_or_edit?noteId={noteId}")

    object NoteShare : Screen("notes/view_or_edit/share")
}

