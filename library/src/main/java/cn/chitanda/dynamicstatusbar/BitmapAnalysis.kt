package cn.chitanda.dynamicstatusbar

import android.graphics.Bitmap
import android.util.Log
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

private val nativeAnalyst =NativeAnalyst()
fun Bitmap.isLightColor(): Boolean {
    return nativeAnalyst.getBright(this) > 128
}