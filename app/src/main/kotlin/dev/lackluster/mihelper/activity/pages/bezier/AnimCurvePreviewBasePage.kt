package dev.lackluster.mihelper.activity.pages.bezier

import android.graphics.PointF
import android.view.View
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.data.CategoryData
import cn.fkj233.ui.activity.data.DescData
import cn.fkj233.ui.activity.data.DialogData
import cn.fkj233.ui.activity.data.DropDownData
import cn.fkj233.ui.activity.data.EditTextData
import dev.lackluster.hyperx.preference.CategoryTitle
import dev.lackluster.hyperx.preference.EditTextPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.activity.view.AnimCurvePreviewView
import dev.lackluster.mihelper.activity.view.CurvePreviewView
import dev.lackluster.mihelper.data.Pref

abstract class AnimCurvePreviewBasePage: BasePage() {
    private val nonlinearTypeEntries by lazy {
        arrayOf(
            DropDownData.SpinnerItemData(activity.getString(R.string.home_refactor_nonlinear_type_non), 0),
            DropDownData.SpinnerItemData(activity.getString(R.string.home_refactor_nonlinear_type_dece), 1),
            DropDownData.SpinnerItemData(activity.getString(R.string.home_refactor_nonlinear_type_bezier), 2)
        )
    }

    companion object {
        private val paramsHashMap = mapOf(
            "apps_nonlinear_type" to Pair(Pref.Key.MiuiHome.Refactor.APPS_NONLINEAR_TYPE, Pref.DefValue.HomeRefactor.APPS_NONLINEAR_TYPE),
            "apps_nonlinear_factor" to Pair(Pref.Key.MiuiHome.Refactor.APPS_NONLINEAR_DECE_FACTOR, Pref.DefValue.HomeRefactor.APPS_NONLINEAR_DECE_FACTOR),
            "apps_nonlinear_x1" to Pair(Pref.Key.MiuiHome.Refactor.APPS_NONLINEAR_PATH_X1, Pref.DefValue.HomeRefactor.APPS_NONLINEAR_PATH_X1),
            "apps_nonlinear_y1" to Pair(Pref.Key.MiuiHome.Refactor.APPS_NONLINEAR_PATH_Y1, Pref.DefValue.HomeRefactor.APPS_NONLINEAR_PATH_Y1),
            "apps_nonlinear_x2" to Pair(Pref.Key.MiuiHome.Refactor.APPS_NONLINEAR_PATH_X2, Pref.DefValue.HomeRefactor.APPS_NONLINEAR_PATH_X2),
            "apps_nonlinear_y2" to Pair(Pref.Key.MiuiHome.Refactor.APPS_NONLINEAR_PATH_Y2, Pref.DefValue.HomeRefactor.APPS_NONLINEAR_PATH_Y2),
            "folder_nonlinear_type" to Pair(Pref.Key.MiuiHome.Refactor.FOLDER_NONLINEAR_TYPE, Pref.DefValue.HomeRefactor.FOLDER_NONLINEAR_TYPE),
            "folder_nonlinear_factor" to Pair(Pref.Key.MiuiHome.Refactor.FOLDER_NONLINEAR_DECE_FACTOR, Pref.DefValue.HomeRefactor.FOLDER_NONLINEAR_DECE_FACTOR),
            "folder_nonlinear_x1" to Pair(Pref.Key.MiuiHome.Refactor.FOLDER_NONLINEAR_PATH_X1, Pref.DefValue.HomeRefactor.FOLDER_NONLINEAR_PATH_X1),
            "folder_nonlinear_y1" to Pair(Pref.Key.MiuiHome.Refactor.FOLDER_NONLINEAR_PATH_Y1, Pref.DefValue.HomeRefactor.FOLDER_NONLINEAR_PATH_Y1),
            "folder_nonlinear_x2" to Pair(Pref.Key.MiuiHome.Refactor.FOLDER_NONLINEAR_PATH_X2, Pref.DefValue.HomeRefactor.FOLDER_NONLINEAR_PATH_X2),
            "folder_nonlinear_y2" to Pair(Pref.Key.MiuiHome.Refactor.FOLDER_NONLINEAR_PATH_Y2, Pref.DefValue.HomeRefactor.FOLDER_NONLINEAR_PATH_Y2),
            "wallpaper_nonlinear_type" to Pair(Pref.Key.MiuiHome.Refactor.WALLPAPER_NONLINEAR_TYPE, Pref.DefValue.HomeRefactor.WALLPAPER_NONLINEAR_TYPE),
            "wallpaper_nonlinear_factor" to Pair(Pref.Key.MiuiHome.Refactor.WALLPAPER_NONLINEAR_DECE_FACTOR, Pref.DefValue.HomeRefactor.WALLPAPER_NONLINEAR_DECE_FACTOR),
            "wallpaper_nonlinear_x1" to Pair(Pref.Key.MiuiHome.Refactor.WALLPAPER_NONLINEAR_PATH_X1, Pref.DefValue.HomeRefactor.WALLPAPER_NONLINEAR_PATH_X1),
            "wallpaper_nonlinear_y1" to Pair(Pref.Key.MiuiHome.Refactor.WALLPAPER_NONLINEAR_PATH_Y1, Pref.DefValue.HomeRefactor.WALLPAPER_NONLINEAR_PATH_Y1),
            "wallpaper_nonlinear_x2" to Pair(Pref.Key.MiuiHome.Refactor.WALLPAPER_NONLINEAR_PATH_X2, Pref.DefValue.HomeRefactor.WALLPAPER_NONLINEAR_PATH_X2),
            "wallpaper_nonlinear_y2" to Pair(Pref.Key.MiuiHome.Refactor.WALLPAPER_NONLINEAR_PATH_Y2, Pref.DefValue.HomeRefactor.WALLPAPER_NONLINEAR_PATH_Y2)
        )
        private fun String.toParamKey(): String {
            return paramsHashMap[this]?.first ?: throw NoSuchElementException("key=${this}")
        }

        private fun String.toParamValue(): Any {
            return paramsHashMap[this]?.second ?: throw NoSuchElementException("key=${this}")
        }
    }
    abstract fun getKeyPrefix(): String
    abstract fun getPageTitleId(): Int

    override fun getTitle(): String {
        return activity.getString(getPageTitleId())
    }
    override fun onCreate() {
        val refreshPreviewBinding = GetDataBinding({
            1.0f
        }) { view, flag, data ->
            val curveType = MIUIActivity.safeSP.getInt("${getKeyPrefix()}_nonlinear_type".toParamKey(), "${getKeyPrefix()}_nonlinear_type".toParamValue() as Int? ?: 0)
            when (flag) {
                0 -> { // CurvePreviewBaseView
                    if (view is CurvePreviewView) {
                        val bezierCurveView: CurvePreviewView = view
                        // Refresh curve preview
                        when (curveType) {
                            1 -> {
                                val factor = MIUIActivity.safeSP.getFloat("${getKeyPrefix()}_nonlinear_factor".toParamKey(), "${getKeyPrefix()}_nonlinear_factor".toParamValue() as Float? ?: 1.0f)
                                bezierCurveView.setDecelerateFactor(factor)
                            }
                            2 -> {
                                val p1x = MIUIActivity.safeSP.getFloat("${getKeyPrefix()}_nonlinear_x1".toParamKey(), "${getKeyPrefix()}_nonlinear_x1".toParamValue() as Float? ?: 0.0f)
                                val p1y = MIUIActivity.safeSP.getFloat("${getKeyPrefix()}_nonlinear_y1".toParamKey(), "${getKeyPrefix()}_nonlinear_y1".toParamValue() as Float? ?: 0.0f)
                                val p2x = MIUIActivity.safeSP.getFloat("${getKeyPrefix()}_nonlinear_x2".toParamKey(), "${getKeyPrefix()}_nonlinear_x2".toParamValue() as Float? ?: 1.0f)
                                val p2y = MIUIActivity.safeSP.getFloat("${getKeyPrefix()}_nonlinear_y2".toParamKey(), "${getKeyPrefix()}_nonlinear_y2".toParamValue() as Float? ?: 1.0f)
                                bezierCurveView.setControlPointData(
                                    point1 = PointF(p1x, p1y),
                                    point2 = PointF(p2x, p2y)
                                )
                            }
                        }
                        bezierCurveView.setCurveType(curveType)
                    }
                }
                1 -> { // EditTextPreference of decelerate factor
                    if (view is EditTextPreference) {
                        val editTextPreference: EditTextPreference = view
                        editTextPreference.visibility = if (curveType == 1) View.VISIBLE else View.GONE
                    }
                }
                3 -> { // CategoryTitle
                    if (view is CategoryTitle) {
                        val categoryTitle: CategoryTitle = view
                        categoryTitle.visibility = if (curveType != 0) View.VISIBLE else View.GONE
                    }
                }
                in 20..23 -> { // EditTextPreference of bezier control points
                    if (view is EditTextPreference) {
                        val editTextPreference: EditTextPreference = view
                        editTextPreference.visibility = if (curveType == 2) View.VISIBLE else View.GONE
                        if (data is Pair<*, *>) {
                            val which = data.first
                            val point = data.second
                            if (which !is Int || point !is PointF) {
                                return@GetDataBinding
                            }
                            when (flag) {
                                20 -> {
                                    if (which == 1) {
                                        view.setValue(point.x)
                                    }
                                }
                                21 -> {
                                    if (which == 1) {
                                        view.setValue(point.y)
                                    }
                                }
                                22 -> {
                                    if (which == 2) {
                                        view.setValue(point.x)
                                    }
                                }
                                23 -> {
                                    if (which == 2) {
                                        view.setValue(point.y)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        itemList.add(AnimCurvePreviewView(
            AnimCurvePreviewView.CurveData(
                "${getKeyPrefix()}_nonlinear_type".toParamKey(),
                "${getKeyPrefix()}_nonlinear_factor".toParamKey(),
                "${getKeyPrefix()}_nonlinear_x1".toParamKey(),
                "${getKeyPrefix()}_nonlinear_y1".toParamKey(),
                "${getKeyPrefix()}_nonlinear_x2".toParamKey(),
                "${getKeyPrefix()}_nonlinear_y2".toParamKey(),
                "${getKeyPrefix()}_nonlinear_type".toParamValue() as Int? ?: 0,
                "${getKeyPrefix()}_nonlinear_factor".toParamValue() as Float? ?: 1.0f,
                "${getKeyPrefix()}_nonlinear_x1".toParamValue() as Float? ?: 0.0f,
                "${getKeyPrefix()}_nonlinear_y1".toParamValue() as Float? ?: 0.0f,
                "${getKeyPrefix()}_nonlinear_x2".toParamValue() as Float? ?: 1.0f,
                "${getKeyPrefix()}_nonlinear_y2".toParamValue() as Float? ?: 1.0f,
            ),
            dataBindingSend = refreshPreviewBinding.bindingSend,
            dataBindingRecv = refreshPreviewBinding.binding.getRecv(0)
        ))
        PreferenceCategory(
            DescData(titleId = R.string.home_refactor_nonlinear_type),
            CategoryData(hideTitle = true, hideLine = true)
        ) {
            DropDownPreference(
                DescData(titleId = R.string.home_refactor_nonlinear_type),
                DropDownData(
                    key = "${getKeyPrefix()}_nonlinear_type".toParamKey(),
                    defValue = "${getKeyPrefix()}_nonlinear_type".toParamValue() as Int? ?: 0,
                    entries = nonlinearTypeEntries,
                    dataBindingSend = refreshPreviewBinding.bindingSend
                )
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.home_refactor_nonlinear_params),
            CategoryData(),
            dataBindingRecv = refreshPreviewBinding.binding.getRecv(3)
        ) {
            EditTextPreference(
                DescData(
                    titleId = R.string.home_refactor_nonlinear_dece_factor,
                    summaryId = R.string.home_refactor_nonlinear_dece_factor_tips
                ),
                EditTextData(
                    key = "${getKeyPrefix()}_nonlinear_factor".toParamKey(),
                    valueType = EditTextData.ValueType.FLOAT,
                    defValue = "${getKeyPrefix()}_nonlinear_factor".toParamValue() as Float? ?: 1.0f,
                    hintText = ("${getKeyPrefix()}_nonlinear_factor".toParamValue() as Float? ?: 1.0f).toString(),
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.home_refactor_nonlinear_dece_factor,
                            summary = "${activity.getString(R.string.common_range)}: 0.1-10.0"
                        )
                    ),
                    isValueValid = { value ->
                        return@EditTextData (value as Float? ?: 0.0f) in 0.1..10.0
                    },
                    dataBindingSend = refreshPreviewBinding.bindingSend
                ),
                dataBindingRecv = refreshPreviewBinding.binding.getRecv(1)
            )
            EditTextPreference(
                DescData(
                    titleId = R.string.home_refactor_nonlinear_bezier_x1
                ),
                EditTextData(
                    key = "${getKeyPrefix()}_nonlinear_x1".toParamKey(),
                    valueType = EditTextData.ValueType.FLOAT,
                    defValue = "${getKeyPrefix()}_nonlinear_x1".toParamValue() as Float? ?: 1.0f,
                    hintText = ("${getKeyPrefix()}_nonlinear_x1".toParamValue() as Float? ?: 1.0f).toString(),
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.home_refactor_nonlinear_bezier_x1,
                            summary = "${activity.getString(R.string.common_range)}: 0.0-1.0"
                        )
                    ),
                    isValueValid = { value ->
                        return@EditTextData (value as Float? ?: 2.0f) in 0.0..1.0
                    },
                    dataBindingSend = refreshPreviewBinding.bindingSend
                ),
                dataBindingRecv = refreshPreviewBinding.binding.getRecv(20)
            )
            EditTextPreference(
                DescData(
                    titleId = R.string.home_refactor_nonlinear_bezier_y1
                ),
                EditTextData(
                    key = "${getKeyPrefix()}_nonlinear_y1".toParamKey(),
                    valueType = EditTextData.ValueType.FLOAT,
                    defValue = "${getKeyPrefix()}_nonlinear_y1".toParamValue() as Float? ?: 1.0f,
                    hintText = ("${getKeyPrefix()}_nonlinear_y1".toParamValue() as Float? ?: 1.0f).toString(),
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.home_refactor_nonlinear_bezier_y1,
                            summary = "${activity.getString(R.string.common_range)}: 0.0-1.0"
                        )
                    ),
                    isValueValid = { value ->
                        return@EditTextData (value as Float? ?: 2.0f) in 0.0..1.0
                    },
                    dataBindingSend = refreshPreviewBinding.bindingSend
                ),
                dataBindingRecv = refreshPreviewBinding.binding.getRecv(21)
            )
            EditTextPreference(
                DescData(
                    titleId = R.string.home_refactor_nonlinear_bezier_x2
                ),
                EditTextData(
                    key = "${getKeyPrefix()}_nonlinear_x2".toParamKey(),
                    valueType = EditTextData.ValueType.FLOAT,
                    defValue = "${getKeyPrefix()}_nonlinear_x2".toParamValue() as Float? ?: 1.0f,
                    hintText = ("${getKeyPrefix()}_nonlinear_x2".toParamValue() as Float? ?: 1.0f).toString(),
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.home_refactor_nonlinear_bezier_x2,
                            summary = "${activity.getString(R.string.common_range)}: 0.0-1.0"
                        )
                    ),
                    isValueValid = { value ->
                        return@EditTextData (value as Float? ?: 2.0f) in 0.0..1.0
                    },
                    dataBindingSend = refreshPreviewBinding.bindingSend
                ),
                dataBindingRecv = refreshPreviewBinding.binding.getRecv(22)
            )
            EditTextPreference(
                DescData(
                    titleId = R.string.home_refactor_nonlinear_bezier_y2
                ),
                EditTextData(
                    key = "${getKeyPrefix()}_nonlinear_y2".toParamKey(),
                    valueType = EditTextData.ValueType.FLOAT,
                    defValue = "${getKeyPrefix()}_nonlinear_y2".toParamValue() as Float? ?: 1.0f,
                    hintText = ("${getKeyPrefix()}_nonlinear_y2".toParamValue() as Float? ?: 1.0f).toString(),
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.home_refactor_nonlinear_bezier_y2,
                            summary = "${activity.getString(R.string.common_range)}: 0.0-1.0"
                        )
                    ),
                    isValueValid = { value ->
                        return@EditTextData (value as Float? ?: 2.0f) in 0.0..1.0
                    },
                    dataBindingSend = refreshPreviewBinding.bindingSend
                ),
                dataBindingRecv = refreshPreviewBinding.binding.getRecv(23)
            )
        }
    }
}