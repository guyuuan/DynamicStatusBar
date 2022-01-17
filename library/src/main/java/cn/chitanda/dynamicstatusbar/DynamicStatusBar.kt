package cn.chitanda.dynamicstatusbar

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.PixelCopy
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import androidx.core.view.WindowInsetsControllerCompat
import java.lang.ref.WeakReference
import kotlin.math.roundToLong

private const val TAG = "DynamicStatusBar"

object DynamicStatusBar {
    private var statusBarBitmap: Bitmap? = null
    private var statusBarCanvas: Canvas? = null
    private var insetsController: WindowInsetsControllerCompat? = null
    private var statusBarHeight: Int = 100
    private var weakWindow: WeakReference<Window>? = null
    private var weakDecorView: WeakReference<View>? = null
    private val decorView: View? get() = weakDecorView?.get()
    private val window: Window? get() = weakWindow?.get()
    private var delay = 1000L / 60
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var rect: Rect
    internal fun init(context: Context) {
        getStatusBarHeight(context)
        initStatusBarBitmap()
    }

    private val preDrawListener by lazy {
        ViewTreeObserver.OnPreDrawListener {
            if (initStatusBarBitmap()) {
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(::calculateBright, delay)
            }
            true
        }
    }


    private fun calculateBright() {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    window?.let {
                        PixelCopy.request(
                            it, rect, statusBarBitmap ?: return@let,
                            { result ->
                                if (result != PixelCopy.SUCCESS) {
                                    Log.e(TAG, "Error while copying pixels, copy result: $result")
                                } else {
                                    insetsController?.isAppearanceLightStatusBars =
                                        statusBarBitmap?.isLightColor() == true
                                }
                            },
                            handler
                        )
                    }
                } else {
                    statusBarCanvas?.let {
                        it.setBitmap(statusBarBitmap)
                        val backup = statusBarCanvas?.save()
                        it.scale(1 / 5f, 1 / 5f)
                        decorView?.draw(it)
                        backup?.let { i -> statusBarCanvas?.restoreToCount(i) }
                        it.setBitmap(null)
                        insetsController?.isAppearanceLightStatusBars =
                            statusBarBitmap?.isLightColor() == true
                    }
                }
            } catch (e: Exception) {
                BuildConfig.DEBUG.takeIf { b -> b }?.let {
                    Log.e(TAG, "OnPreDrawListener: ", e)
                }
            }
    }

    private fun initStatusBarBitmap(): Boolean {
        if (statusBarBitmap == null || statusBarCanvas == null) {
            try {
                val w = Resources.getSystem().displayMetrics.widthPixels / 5
                val h = statusBarHeight / 5
                statusBarBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                statusBarCanvas = Canvas(statusBarBitmap ?: return false)

            } catch (e: Exception) {
                Log.e(TAG, "initStatusBarBitmap: ", e)
                return false
            }
            Log.d(TAG, "initStatusBarBitmap: ")
        }
        return true
    }

    private fun getStatusBarHeight(context: Context) {
        val resourceId =
            context.resources?.getIdentifier("status_bar_height", "dimen", "android") ?: 0
        statusBarHeight = if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            context.resources?.getDimensionPixelSize(resourceId) ?: 100
        } else {
            100
        }
        rect = Rect(
            0, 0, Resources.getSystem().displayMetrics.widthPixels,
            statusBarHeight
        )
    }

    internal fun onResume(window: Window) {
        weakWindow?.clear()
        weakWindow = WeakReference(window)
        weakDecorView?.clear()
        weakDecorView = WeakReference(window.decorView)
        delay = (1000L / (window.decorView.display?.refreshRate ?: 60f).roundToLong())*3
        insetsController = WindowInsetsControllerCompat(window, window.decorView)
        decorView?.viewTreeObserver?.addOnPreDrawListener(preDrawListener)
    }

    internal fun onPause() {
        weakWindow?.clear()
        weakDecorView?.clear()
        insetsController = null
        decorView?.viewTreeObserver?.removeOnPreDrawListener(preDrawListener)
    }

}