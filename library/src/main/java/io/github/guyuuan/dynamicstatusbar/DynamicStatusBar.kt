package io.github.guyuuan.dynamicstatusbar

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
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withScale
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.lang.ref.WeakReference
import kotlin.math.abs
import kotlin.math.roundToLong

private const val TAG = "DynamicStatusBar"

object DynamicStatusBar {
    private var statusBarBitmap: Bitmap? = null
    private var statusBarCanvas: Canvas? = null
    private var navBarBitmap: Bitmap? = null
    private var navBarCanvas: Canvas? = null
    private var insetsController: WindowInsetsControllerCompat? = null
    private var statusBarHeight: Int = 100
    private var navBarHeight: Int = 100
    private var weakWindow: WeakReference<Window>? = null
    private var weakDecorView: WeakReference<View>? = null
    private val decorView: View? get() = weakDecorView?.get()
    private val window: Window? get() = weakWindow?.get()
    private var delay = 1000L / 60
    private val handler = Handler(Looper.getMainLooper())

    @Volatile
    var isLight = true
        private set

    private lateinit var rect: Rect
    private lateinit var navRect: Rect


    private val preDrawListener by lazy {
        ViewTreeObserver.OnPreDrawListener {
            if (initStatusBarBitmap()) {
                calculateBright()
            }
            true
        }
    }

    private fun calculateBright() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window?.let {
                    statusBarBitmap?.let { it1 ->
                        PixelCopy.request(
                            it, rect, it1,
                            { result ->
                                if (result != PixelCopy.SUCCESS) {
                                    Log.e(TAG, "Error while copying pixels, copy result: $result")
                                } else {
                                    val isLight =
                                        (statusBarBitmap?.isLightColor() == true).also { b ->
                                            isLight = b
                                        }
                                    insetsController?.isAppearanceLightStatusBars = isLight
                                    insetsController?.isAppearanceLightNavigationBars = isLight
                                }
                            },
                            handler
                        )
                    }
                    navBarBitmap?.let { it1 ->
                        PixelCopy.request(
                            it, navRect, it1,
                            { result ->
                                if (result != PixelCopy.SUCCESS) {
                                    Log.e(TAG, "Error while copying pixels, copy result: $result")
                                } else {
                                    val isLight = (navBarBitmap?.isLightColor() == true).also { b ->
                                        isLight = b
                                    }
                                    insetsController?.isAppearanceLightNavigationBars = isLight
                                }
                            },
                            handler
                        )
                    }
                }
            } else {
                handler.removeCallbacks(::calculateBrightWithCanvas)
                handler.postDelayed(::calculateBrightWithCanvas, delay)
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "OnPreDrawListener: ", e)
            }
        }
    }

    private fun calculateBrightWithCanvas() {
        statusBarCanvas?.let {
            it.withScale(1 / 5f, 1 / 5f) {
                decorView?.draw(it)
            }
            val isLight = (statusBarBitmap?.isLightColor() == true).also { b ->
                isLight = b
            }
            insetsController?.isAppearanceLightStatusBars = isLight
        }
        navBarCanvas?.let {
            it.withScale(1 / 5f, 1 / 5f) {
                decorView?.draw(it)
            }
            val isLight = (navBarBitmap?.isLightColor() == true).also { b ->
                isLight = b
            }
            insetsController?.isAppearanceLightNavigationBars = isLight
        }
    }

    private fun initStatusBarBitmap(): Boolean {

        if (statusBarBitmap == null || statusBarCanvas == null) {
            window?.decorView?.let {
                val insets = WindowInsetsCompat.toWindowInsetsCompat(it.rootWindowInsets, it)
                val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
                statusBarHeight = abs(statusBar.top - statusBar.bottom)
            }
            rect = Rect(0, 0, Resources.getSystem().displayMetrics.widthPixels, statusBarHeight)
            try {
                val w = Resources.getSystem().displayMetrics.widthPixels / 5
                val h = statusBarHeight / 5
                statusBarBitmap = createBitmap(w, h)
                statusBarCanvas = Canvas(statusBarBitmap ?: return false)

            } catch (e: Exception) {
                Log.e(TAG, "initStatusBarBitmap: ", e)
                return false
            }
            Log.d(TAG, "initStatusBarBitmap: ")
        }
        if (navBarCanvas == null || navBarBitmap == null) {
            window?.decorView?.let {
                val insets = WindowInsetsCompat.toWindowInsetsCompat(it.rootWindowInsets, it)
                val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                navBarHeight = abs(navBar.top - navBar.bottom)
            }
            navRect = Rect(
                0,
                Resources.getSystem().displayMetrics.heightPixels - navBarHeight,
                Resources.getSystem().displayMetrics.widthPixels,
                Resources.getSystem().displayMetrics.heightPixels
            )

            try {
                val w = Resources.getSystem().displayMetrics.widthPixels / 5
                val h = navBarHeight / 5
                navBarBitmap = createBitmap(w, h)
                navBarCanvas = Canvas(statusBarBitmap ?: return false)
            } catch (e: Exception) {
                Log.e(TAG, "initStatusBarBitmap: ", e)
                return false
            }
            Log.d(TAG, "initStatusBarBitmap: ")
        }
        return true
    }

//    private fun getStatusBarHeight(context: Context) {
//        val resourceId =
//            context.resources?.getIdentifier("status_bar_height", "dimen", "android") ?: 0
//        statusBarHeight = if (resourceId > 0) {
//            //根据资源ID获取响应的尺寸值
//            context.resources?.getDimensionPixelSize(resourceId) ?: 100
//        } else {
//            100
//        }
//        rect = Rect(
//            0, 0, Resources.getSystem().displayMetrics.widthPixels,
//            statusBarHeight
//        )
//    }

    internal fun onResume(window: Window) {
        weakWindow?.clear()
        weakWindow = WeakReference(window)
        weakDecorView?.clear()
        weakDecorView = WeakReference(window.decorView)
        delay = (1000L / (window.decorView.display?.refreshRate ?: 60f).roundToLong()) * 3
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