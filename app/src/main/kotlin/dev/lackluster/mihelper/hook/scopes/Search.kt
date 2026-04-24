package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.search.MoreSearchEngine

object Search : StaticHooker() {
    override val requireDexKit: Boolean = true

    override fun onInit() {
        attach(MoreSearchEngine)
    }
}