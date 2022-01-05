package cn.chitanda.dynamicstatusbar

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import androidx.core.view.WindowInsetsControllerCompat
import java.lang.ref.WeakReference
import kotlin.math.roundToLong
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

private const val TAG = "DynamicStatusBar"

object DynamicStatusBar {
    private var statusBarBitmap: Bitmap? = null
    private var statusBarCanvas: Canvas? = null
    private var insetsController: WindowInsetsControllerCompat? = null
    private var statusBarHeight: Int = 100
    private var weakDecorView: WeakReference<View>? = null
    private val decorView: View? get() = weakDecorView?.get()
    private var delay = 1000L / 60
    internal fun init(context: Context) {
        getStatusBarHeight(context)
        initStatusBarBitmap()
    }

    private val preDrawListener by lazy {
        ViewTreeObserver.OnPreDrawListener {
            if (initStatusBarBitmap()) {
                decorView?.handler?.let {
                    it.removeCallbacksAndMessages(null)
                    it.postDelayed(::calculateBright, delay)
                }
            }
            true
        }
    }


    private fun calculateBright() {
        statusBarCanvas?.let {
          val time =  measureTimeMillis{
                val backup = statusBarCanvas?.save()
                try {
                    it.scale(1 / 5f, 1 / 5f)
                    decorView?.draw(it)
                } catch (e: Exception) {
                    Log.e(TAG, "OnPreDrawListener: ", e)
                } finally {
                    backup?.let { statusBarCanvas?.restoreToCount(it) }
                    insetsController?.isAppearanceLightStatusBars =
                        statusBarBitmap?.isLightColor() == true
                }
            }
            Log.d(TAG, "calculateBright: time = $time ms")
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
    }

    internal fun onResume(window: Window) {
        weakDecorView = WeakReference(
            if (window.decorView.fitsSystemWindows) window.decorView else window.decorView.findViewById(
                android.R.id.content
            )
        )
        delay =(1000L/ (window.decorView.display?.refreshRate?: 60f).roundToLong() )
        insetsController = WindowInsetsControllerCompat(window, window.decorView)
        decorView?.viewTreeObserver?.addOnPreDrawListener(preDrawListener)
    }

    internal fun onPause() {
        insetsController = null
        decorView?.viewTreeObserver?.removeOnPreDrawListener(preDrawListener)
    }

}