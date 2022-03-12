package com.jayesh.jnotes.util

import android.text.format.DateUtils
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import com.jayesh.jnotes.BuildConfig
import java.util.Calendar
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun Long.timeAgo() = DateUtils.getRelativeTimeSpanString(
    this,
    Calendar.getInstance().timeInMillis,
    DateUtils.DAY_IN_MILLIS
).toString()

class Ref(var value: Int)

const val EnableDebugCompositionLogs = true

@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun LogCompositions(tag: String) {
    if (EnableDebugCompositionLogs && BuildConfig.DEBUG) {
        val ref = remember { Ref(0) }
        SideEffect { ref.value++ }
        Log.e(tag, "Compositions: ${ref.value}")
    }
}

val blank: String get() = ""

/**
 * Returns `this` value if it satisfies the given [predicate] or `null`, if it doesn't.
 *
 * For detailed usage information see the documentation for [scope functions](https://kotlinlang.org/docs/reference/scope-functions.html#takeif-and-takeunless).
 */
@ExperimentalContracts
inline fun String.takeStringOrBlank(predicate: (String) -> Boolean): String {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return if (predicate(this)) this else ""
}