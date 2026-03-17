package dev.lackluster.mihelper.data

import dev.lackluster.mihelper.R

object Contributors {
    val list = listOf(
        Contributor("余空", "YuKongA", R.mipmap.contributor_yukonga, "https://github.com/YuKongA"),
        Contributor("焕晨HChen", "HChenX", R.mipmap.contributor_hchenx, "https://github.com/HChenX"),
        Contributor("igor", "igormiguell", R.mipmap.contributor_igormiguell, "https://github.com/igormiguell"),
        Contributor("Re*Index. (ot_inc)", "reindex-ot", R.mipmap.contributor_reindex_ot, "https://github.com/reindex-ot"),
    )
}

data class Contributor(
    val name: String,
    val bio: String,
    val avatarResId: Int,
    val link: String
)