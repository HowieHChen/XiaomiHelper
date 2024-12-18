package dev.lackluster.mihelper.data

import dev.lackluster.mihelper.R

object Contributors {
    val list = listOf(
        Contributor("igor", "igormiguell", R.mipmap.contributor_igormiguell, "https://github.com/igormiguell"),
        Contributor("Re*Index. (ot_inc)", "reindex-ot", R.mipmap.contributor_reindex_ot, "https://github.com/reindex-ot"),
        Contributor("余空", "YuKongA", R.mipmap.contributor_yukonga, "https://github.com/YuKongA"),
    )
}

data class Contributor(
    val name: String,
    val bio: String,
    val avatarResId: Int,
    val link: String
)