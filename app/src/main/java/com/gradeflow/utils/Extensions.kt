package com.gradeflow.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Double.formatGpa(decimals: Int = 2): String = String.format(Locale.getDefault(), "%.${decimals}f", this)
fun Long.toFormattedDate(): String = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(this))
fun Long.toShortDate(): String = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(this))
