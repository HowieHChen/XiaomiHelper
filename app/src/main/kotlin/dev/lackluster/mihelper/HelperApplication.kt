package dev.lackluster.mihelper

import androidx.appcompat.app.AppCompatDelegate
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication

class HelperApplication : ModuleApplication() {

    override fun onCreate() {
        super.onCreate()
        /**
         * 跟随系统夜间模式
         * Follow system night mode
         */
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        // Your code here.
    }
}