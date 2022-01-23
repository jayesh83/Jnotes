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
import com.jayesh.jnotes.ui.Screen.NotesListing
import com.jayesh.jnotes.ui.newOrEditNote.NewOrEditNoteScreen
import com.jayesh.jnotes.ui.notesListing.NotesListingScreen

@Composable
fun JnotesApp() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NotesListing.route
    ) {
        composable(
            route = NotesListing.route
        ) {
            NotesListingScreen(
                viewmodel = hiltViewModel(),
                onAddNewNote = {
                    navController.navigate(NoteCreateNew.route)
                },
                onEditNote = {
                    navController.navigate(NoteEdit.createRoute(it))
                }
            )
        }

        composable(
            route = NoteEdit.route,
            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("noteId")
            NewOrEditNoteScreen(
                noteId = id,
                viewmodel = hiltViewModel(),
                onBack = {
                    navController.navigateUp()
                }
            )
        }

        composable(
            route = NoteCreateNew.route
        ) {
            NewOrEditNoteScreen(
                noteId = null,
                viewmodel = hiltViewModel(),
                onBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object NotesListing : Screen("note_listing")
    object NoteEdit : Screen("notes_view_or_edit/{noteId}") {
        fun createRoute(noteId: String) = "notes_view_or_edit/$noteId"
    }

    object NoteCreateNew : Screen("notes_create_new")
}

