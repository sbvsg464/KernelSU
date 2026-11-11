package me.weishu.kernelsu.ui.component

import androidx.compose.runtime.Composable
import me.weishu.kernelsu.Natives

@Composable
fun KsuIsValid(
    content: @Composable () -> Unit
) {
    // UI preview mode: keep navigation and settings visible without a live backend.
    content()
}
