package cn.chitanda.mylibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.WindowCompat
import cn.chitanda.dynamicstatusbar.DynamicStatusBar

class NextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window,false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next)
    }
}