package dev.lackluster.mihelper

import androidx.appcompat.app.AppCompatDelegate
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication
import com.topjohnwu.superuser.Shell
import dev.lackluster.mihelper.app.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class HelperApplication : ModuleApplication() {

    override fun onCreate() {
        super.onCreate()
        /**
         * 跟随系统夜间模式
         * Follow system night mode
         */
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        // Your code here.

        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR) // 将错误输出重定向到标准输出
                .setFlags(Shell.FLAG_MOUNT_MASTER)    // 可选：如果需要全局挂载命名空间
                .setTimeout(10)                       // 设置请求 root 的超时时间
        )

        startKoin {
            androidLogger()
            androidContext(this@HelperApplication)
            modules(appModule)
        }
    }
}