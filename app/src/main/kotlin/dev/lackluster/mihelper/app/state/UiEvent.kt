package dev.lackluster.mihelper.app.state

import dev.lackluster.mihelper.data.Route

sealed interface UiEvent {
    data class ShowToast(val message: UiText, val long: Boolean = false) : UiEvent
//    data class ShowSnackbar(val message: String) : UiEvent
    object NavigateBack : UiEvent
    data class NavigateTo(val route: Route) : UiEvent
}