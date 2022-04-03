package com.jayesh.jnotes.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jayesh.jnotes.ui.Screen.NoteCreateNew
import com.jayesh.jnotes.ui.Screen.NoteEdit
import com.jayesh.jnotes.ui.Screen.Notes
import com.jayesh.jnotes.ui.noteDetail.NoteDetailScreen
import com.jayesh.jnotes.ui.notes.HomeScreen

@Composable
fun JnotesApp() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Notes.route
    ) {
        composable(route = Notes.route) {
            HomeScreen(
                notesViewModel = hiltViewModel(),
                noteDetailViewModel = hiltViewModel(),
                onAddNewNote = {
                    navController.navigate(NoteCreateNew.route)
                },
                onEditNote = { noteId ->
                    navController.navigate(NoteEdit.createRoute(noteId))
                }
            )
            // TODO: remove this code
/*            NotesScreen(
                viewmodel = hiltViewModel(),
                onAddNewNote = {
                    navController.navigate(NoteCreateNew.route)
                },
                onEditNote = { noteId ->
                    navController.navigate(NoteEdit.createRoute(noteId))
                }
            )*/
        }

        composable(
            route = NoteEdit.route,
            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
        ) {
            NoteDetailScreen(
                viewmodel = hiltViewModel(),
                onBack = { navController.navigateUp() }
            )
        }

        composable(
            route = NoteCreateNew.route
        ) {
            NoteDetailScreen(
                viewmodel = hiltViewModel(),
                onBack = { navController.navigateUp() }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Notes : Screen("notes")
    object NoteEdit : Screen("notes/view_or_edit/{noteId}") {
        fun createRoute(noteId: String) = "notes/view_or_edit/$noteId"
    }

    object NoteCreateNew : Screen("notes/create_new")
}

