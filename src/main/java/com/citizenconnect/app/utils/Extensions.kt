package com.citizenconnect.app.utils
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.citizenconnect.app.R
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat; import java.util.*

fun View.showSnackbar(msg: String, d: Int = Snackbar.LENGTH_SHORT) = Snackbar.make(this, msg, d).show()
fun View.visible()   { visibility = View.VISIBLE }
fun View.gone()      { visibility = View.GONE }

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Long.toFormattedDate(): String = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(this))
fun Long.toFormattedDateTime(): String = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(this))

fun getGreeting(context: Context): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour in 4..11 -> context.getString(R.string.good_morning)
        hour in 12..16 -> context.getString(R.string.good_afternoon)
        hour in 17..20 -> context.getString(R.string.good_evening)
        else -> context.getString(R.string.good_night)
    }
}
