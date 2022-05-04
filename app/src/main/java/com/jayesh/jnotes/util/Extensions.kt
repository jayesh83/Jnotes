package com.jayesh.jnotes.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.text.format.DateUtils
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.jayesh.jnotes.BuildConfig
import timber.log.Timber
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
        Timber.e("Compositions: ${ref.value}")
    }
}

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/**
 * Convenience for modifiers that should only apply if [condition] is true.
 * [elseFn] useful for conditional else-logic.
 */
inline fun Modifier.thenIf(
    condition: Boolean,
    modifierFn: Modifier.() -> Modifier,
) = this.let {
    if (condition) {
        it.modifierFn()
    } else {
        it
    }
}


/**
 * Convenience for modifiers that should only apply if [condition] is true.
 * [elseFn] useful for conditional else-logic.
 */
inline fun Modifier.thenIf(
    condition: Boolean,
    modifierFn: Modifier.() -> Modifier,
    elseFn: Modifier.() -> Modifier,
) = this.let {
    if (condition) {
        it.modifierFn()
    } else {
        it.elseFn()
    }
}

object StringUtils {
    val blankString: String get() = ""

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

    fun overflowText(text: String, maxLen: Int): String {
        return buildString {
            if (text.length <= maxLen)
                append(text)
            else {
                append(text.take(maxLen))
                append("...")
            }
        }
    }
}