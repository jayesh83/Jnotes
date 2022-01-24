package com.jayesh.jnotes.util

import android.text.format.DateUtils
import java.util.Calendar

fun Long.timeAgo() = DateUtils.getRelativeTimeSpanString(
    this,
    Calendar.getInstance().timeInMillis,
    DateUtils.DAY_IN_MILLIS
).toString()