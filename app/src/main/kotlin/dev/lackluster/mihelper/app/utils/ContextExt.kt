package dev.lackluster.mihelper.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.net.toUri
import dev.lackluster.mihelper.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Context.showToast(
    message: String,
    long: Boolean = false
) {
    Toast.makeText(this, message, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}

suspend fun Context.showToastAsync(
    message: String,
    long: Boolean = false
) = withContext(Dispatchers.Main) {
    this@showToastAsync.showToast(message, long)
}

fun Context.openUrl(@StringRes urlResId: Int) {
    openUrl(getString(urlResId))
}

fun Context.openUrl(url: String) {
    try {
        val uri = url.toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    } catch (_: Exception) {
        showToast(getString(R.string.about_jump_error_toast), true)
    }
}

fun Context.jumpToAppDetailsSettings(pkg: String) {
    val cleanPkg = pkg.trim()
    if (cleanPkg.isEmpty()) {
        showToast("Illegal parameter, package name is empty", true)
        return
    }
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", cleanPkg, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}

fun Context.getFileNameFromUri(uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) result = cursor.getString(index)
            }
        }
    }
    return result ?: uri.path?.substringAfterLast('/') ?: "Unknown file"
}