package io.github.guyuuan.dynamicstatusbar.init

import android.app.Activity
import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import io.github.guyuuan.dynamicstatusbar.DynamicStatusBar

/**
 * @author: Chen
 * @createTime: 2021/12/23 10:46 上午
 * @description:
 **/
class InitContentProvider : ContentProvider(), Application.ActivityLifecycleCallbacks {
    override fun onCreate(): Boolean {
        val app = (context?.applicationContext as? Application) ?: return false
        app.registerActivityLifecycleCallbacks(this)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null


    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        DynamicStatusBar.onResume(activity.window)
    }

    override fun onActivityPaused(activity: Activity) {
        DynamicStatusBar.onPause()
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}