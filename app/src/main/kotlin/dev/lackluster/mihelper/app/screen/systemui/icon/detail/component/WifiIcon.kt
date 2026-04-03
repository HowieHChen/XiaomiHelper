package dev.lackluster.mihelper.app.screen.systemui.icon.detail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Visibility
import androidx.constraintlayout.compose.layoutId
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.WlanState

@Composable
fun WifiIcon(
    state: WlanState,
) {
    val constraints = ConstraintSet {
        val parent = createRefFor("parent")
        val wifi = createRefFor("wifi")
        val wifiInout = createRefFor("wifi_inout")
        val wifiStandard = createRefFor("wifi_standard")
        constrain(wifi) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        }
        constrain(wifiInout) {
            top.linkTo(wifi.top)
            bottom.linkTo(wifi.bottom)
            if (state.hideWifiActivity) {
                visibility = Visibility.Gone
            } else if (state.hideWifiStandard && state.rightWifiActivity) {
                end.linkTo(wifi.end)
            } else {
                start.linkTo(wifi.start)
            }
        }
        constrain(wifiStandard) {
            top.linkTo(wifi.top)
            bottom.linkTo(wifi.bottom)
            end.linkTo(wifi.end)
            if (state.hideWifiStandard) {
                visibility = Visibility.Gone
            }
        }
    }
    ConstraintLayout(
        constraintSet = constraints,
        modifier = Modifier.size(20.dp, 24.dp)
    ) {
        Image(
            modifier = Modifier
                .layoutId("wifi")
                .size(20.dp),
            painter = painterResource(R.drawable.stat_sys_wifi_signal),
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .layoutId("wifi_inout")
                .size(6.dp, 20.dp),
            painter = painterResource(R.drawable.stat_sys_wifi_inout),
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .layoutId("wifi_standard")
                .size(6.dp, 20.dp),
            painter = painterResource(R.drawable.stat_sys_wifi_standard),
            contentDescription = null
        )
    }
}