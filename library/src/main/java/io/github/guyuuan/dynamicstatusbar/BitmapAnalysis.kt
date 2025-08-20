package io.github.guyuuan.dynamicstatusbar

import android.graphics.Bitmap

private val nativeAnalyst = NativeAnalyst()
fun Bitmap.isLightColor(): Boolean {
    return nativeAnalyst.getBright(this) > 128
}