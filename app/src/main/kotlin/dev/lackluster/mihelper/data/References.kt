package dev.lackluster.mihelper.data

object References {
    val list = listOf(
        Reference("HowieHChen/hyperx-compose", "Apache-2.0","https://github.com/HowieHChen/hyperx-compose"),
        Reference("miuix-kotlin-multiplatform/miuix", "Apache-2.0","https://github.com/miuix-kotlin-multiplatform/miuix"),
        Reference("YifePlayte/MaxFreeForm", "GPL-3.0", "https://github.com/YifePlayte/MaxFreeForm"),
        Reference("YifePlayte/MaxMiPad", "GPL-3.0", "https://github.com/YifePlayte/MaxMiPad"),
        Reference("zerorooot/HorizontalContentextension", "GPL-3.0", "https://github.com/zerorooot/HorizontalContentextension"),
        Reference("LuckyPray/DexKit", "GPL-3.0", "https://github.com/LuckyPray/DexKit"),
        Reference("Simplicity-Team/WooBoxForMIUI", "GPL-3.0", "https://github.com/Simplicity-Team/WooBoxForMIUI"),
        Reference("HowieHChen/WooBoxForMIUI", "GPL-3.0", "https://github.com/HowieHChen/WooBoxForMIUI"),
        Reference("hosizoraru/WooBoxForMIUI", "GPL-3.0", "https://github.com/hosizoraru/WooBoxForMIUI"),
        Reference("hosizoraru/StarVoyager", "GPL-3.0", "https://github.com/hosizoraru/StarVoyager"),
        Reference("ReChronoRain/HyperCeiler", "AGPL-3.0", "https://github.com/ReChronoRain/HyperCeiler"),
        Reference("HighCapable/YukiHookAPI", "Apache-2.0", "https://github.com/HighCapable/YukiHookAPI"),
        Reference("hellokaton/TinyPinyin", "Apache-2.0", "https://github.com/hellokaton/TinyPinyin"),
        Reference("kooritea/fcmfix", "Unknown", "https://github.com/kooritea/fcmfix")
    )
}

data class Reference(
    val name: String,
    val license: String,
    val link: String
)