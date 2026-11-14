package dev.lackluster.mihelper.hook.rules.taplus

import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.hook.base.ContextAwareHooker
import dev.lackluster.mihelper.hook.base.ContextScope

object ResourcesUtils : ContextAwareHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true

    override val targetPackage: String
        get() = Scope.TAPLUS

    var icon_text = 0
    var icon_text_recognition_search_layout = 0
    var recognition_card_2 = 0
    var recommend_group = 0
    var recognition_translate = 0
    var recognition_shopping = 0
    var shrink_card_width = 0
    var shrink_card_height = 0
    var shrink_card_elevation = 0
    var expanded_card_margin = 0
    var card_recommend_recognition_margin = 0
    var item_card_margin_right = 0
    var size_50_for_j18 = 0
    var size_60 = 0

    override fun ContextScope.onReady() {
        icon_text = "icon_text".toDrawableId()
        icon_text_recognition_search_layout = "icon_text_recognition_search_layout".toDrawableId()
        recognition_card_2 = "recognition_card_2".toId()
        recommend_group = "recommend_group".toId()
        recognition_translate = "recognition_translate".toId()
        recognition_shopping = "recognition_shopping".toId()
        shrink_card_width = "shrink_card_width".toDimenId()
        shrink_card_height = "shrink_card_height".toDimenId()
        shrink_card_elevation = "shrink_card_elevation".toDimenId()
        expanded_card_margin = "expanded_card_margin".toDimenId()
        card_recommend_recognition_margin = "card_recommend_recognition_margin".toDimenId()
        item_card_margin_right = "item_card_margin_right".toDimenId()
        size_50_for_j18 = "size_50_for_j18".toDimenId()
        size_60 = "size_60".toDimenId()
    }
}
