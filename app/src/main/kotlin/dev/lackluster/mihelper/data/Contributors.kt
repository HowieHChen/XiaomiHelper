package dev.lackluster.mihelper.data

import androidx.annotation.DrawableRes
import dev.lackluster.mihelper.R

object Contributors {
    val developers = listOf(
        Contributor("Howie", "HowieHChen", R.mipmap.developer_howie, "https://github.com/HowieHChen"),
        Contributor("余空", "YuKongA", R.mipmap.developer_yukonga, "https://github.com/YuKongA"),
        Contributor("焕晨HChen", "HChenX", R.mipmap.developer_hchenx, "https://github.com/HChenX"),
    )
    val translators = listOf(
        Contributor("igormiguell", "en | pt-rBR", R.mipmap.translator_igormiguell, "https://github.com/igormiguell"),
        Contributor("WendellOffical", "zh-rHK", R.mipmap.translator_wendelloffical, "https://github.com/WendellOffical"),
        Contributor("reindex-ot", "ja", R.mipmap.translator_reindex_ot, "https://github.com/reindex-ot"),
        Contributor("huntersun", "vi", R.mipmap.translator_huntersun, "https://github.com/huntersun"),
        Contributor("Wuang26", "vi", R.mipmap.translator_wuang26, "https://github.com/Wuang26"),
    )
}

data class Contributor(
    val name: String,
    val bio: String,
    @DrawableRes val avatarResId: Int?,
    val link: String
)