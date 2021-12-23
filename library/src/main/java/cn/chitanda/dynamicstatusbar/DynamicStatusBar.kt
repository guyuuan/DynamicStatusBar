package cn.chitanda.dynamicstatusbar

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import androidx.core.view.WindowInsetsControllerCompat
import java.lang.ref.WeakReference

private const val TAG = "DynamicStatusBar"

object DynamicStatusBar {
    private val mainHandler by lazy { Handler(Looper.getMainLooper()) }
    private var statusBarBitmap: Bitmap? = null
    private var statusBarCanvas: Canvas? = null
    private var insetsController: WindowInsetsControllerCompat? = null
    private var statusBarHeight: Int = 100
    private var weakDecorView: WeakReference<View>? = null
    private val decorView: View? get() = weakDecorView?.get()

    //    @MainThread
//    fun init(activity: ComponentActivity) {
//        this.activity = WeakReference(activity)
//        weakDecorView = WeakReference(activity.window.decorView)
//        activity.lifecycle.addObserver(this)
//        insetsController = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
//    }
//
//    @MainThread
//    fun init(activity: AppCompatActivity) {
//        this.activity = WeakReference(activity)
//        weakDecorView = WeakReference(activity.window.decorView)
//        activity.lifecycle.addObserver(this)
//        insetsController = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
//    }

    internal fun init(context: Context) {
        getStatusBarHeight(context)
        initStatusBarBitmap()
    }

    private val preDrawListener by lazy {
        ViewTreeObserver.OnPreDrawListener {
            if (initStatusBarBitmap()) {
                mainHandler.apply {
                    removeCallbacksAndMessages(null)
                    postDelayed({
                        val backup = statusBarCanvas?.save()
                        try {
                            statusBarCanvas?.let {
                                it.scale(1 / 5f, 1 / 5f)
                                decorView?.draw(it)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "OnPreDrawListener: ", e)
                        } finally {
                            backup?.let { statusBarCanvas?.restoreToCount(it) }
                        }
                        insetsController?.isAppearanceLightStatusBars =
                            statusBarBitmap?.isLightColor() == true
                    }, 20)
                }
            }
            true
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
        weakDecorView = WeakReference(window.decorView)
        insetsController = WindowInsetsControllerCompat(window, window.decorView)
        decorView?.viewTreeObserver?.addOnPreDrawListener(preDrawListener)
    }

    internal fun onPause() {
        mainHandler.removeCallbacksAndMessages(null)
        insetsController = null
        decorView?.viewTreeObserver?.removeOnPreDrawListener(preDrawListener)
    }
}