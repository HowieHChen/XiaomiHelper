package dev.lackluster.mihelper.utils

object Device {
    val isPad by lazy {
        try {
            Class.forName("miui.os.Build").getDeclaredField("IS_TABLET").get(null) as Boolean
        }
        catch (e: Exception) {
            false
        }
    }
}