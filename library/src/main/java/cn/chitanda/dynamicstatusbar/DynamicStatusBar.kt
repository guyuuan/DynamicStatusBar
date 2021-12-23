package cn.chitanda.dynamicstatusbar

import android.app.Activity
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.ref.WeakReference

private const val TAG = "DynamicStatusBar"

object DynamicStatusBar : LifecycleEventObserver {
    private lateinit var activity: WeakReference<Activity>
    private var switcher = true
    private val mainHandler by lazy { Handler(Looper.getMainLooper()) }
    private var statusBarBitmap: Bitmap? = null
    private var statusBarCanvas: Canvas? = null
    private var insetsController: WindowInsetsControllerCompat? = null
    private lateinit var weakDecorView: WeakReference<View>
    private val decorView: View?
        get() = weakDecorView.get()

    @MainThread
    fun init(activity: ComponentActivity) {
        this.activity = WeakReference(activity)
        weakDecorView = WeakReference(activity.window.decorView)
        activity.lifecycle.addObserver(this)
        insetsController = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
    }

    @MainThread
    fun init(activity: AppCompatActivity) {
        this.activity = WeakReference(activity)
        weakDecorView = WeakReference(activity.window.decorView)
        activity.lifecycle.addObserver(this)
        insetsController = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
    }

    /*
    *  @Description: Turn off the dynamic effect temporarily, you can turn it on again by using open()
    * */
    @MainThread
    fun close() {
        if (!switcher) return
        switcher = false
        onPause()
    }

    /*
    *  @Description: It is used when it needs to be re-opened after it is closed again.
    *                Generally, there is no need to actively call to open it.
    * */
    @MainThread
    fun open() {
        if (switcher) return
        switcher = true
        onResume()
    }

    private val preDrawListener by lazy {
        ViewTreeObserver.OnPreDrawListener {
            if (decorView != null && initStatusBarBitmap()) {
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
                val h = getStatusBarHeight() / 5
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

    private fun getStatusBarHeight(): Int {
        val resourceId =
            activity.get()?.resources?.getIdentifier("status_bar_height", "dimen", "android") ?: 0
        return if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            activity.get()?.resources?.getDimensionPixelSize(resourceId) ?: 100
        } else {
            100
        }
    }

    private fun onResume() {
        decorView?.viewTreeObserver?.addOnPreDrawListener(preDrawListener)
    }

    private fun onPause() {
        decorView?.viewTreeObserver?.removeOnPreDrawListener(preDrawListener)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE -> onPause()
            Lifecycle.Event.ON_RESUME -> onResume()
            else -> {}
        }
    }
}