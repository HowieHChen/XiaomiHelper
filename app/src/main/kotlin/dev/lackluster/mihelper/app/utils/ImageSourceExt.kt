package dev.lackluster.mihelper.app.utils

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import dev.lackluster.hyperx.ui.component.ImageSource

fun @receiver:DrawableRes Int.toImageSource(): ImageSource {
    return ImageSource.Res(this)
}

fun ImageVector.toImageSource(): ImageSource {
    return ImageSource.Vector(this)
}

fun ImageBitmap.toImageSource(): ImageSource {
    return ImageSource.Bitmap(this)
}