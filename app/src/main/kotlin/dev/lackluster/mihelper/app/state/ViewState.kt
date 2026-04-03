package dev.lackluster.mihelper.app.state

sealed interface ViewState<out T> {
    object Idle : ViewState<Nothing>               // 初始空闲状态
    object Loading : ViewState<Nothing>            // 正在加载/处理中
    data class Success<T>(val data: T) : ViewState<T> // 成功，并携带业务数据
    data class Error(val message: UiText) : ViewState<Nothing> // 失败，携带错误信息
}