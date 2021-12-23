package cn.chitanda.mylibrary.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import cn.chitanda.dynamicstatusbar.DynamicStatusBar
import cn.chitanda.mylibrary.compose.ui.theme.MyLibraryTheme

private const val TAG = "ComposeActivity"

class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        DynamicStatusBar.init(this)
        setContent {
            MyLibraryTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(300) { i ->
                            val color = when {
                                i % 2 == 0 -> Color.White
                                else -> Color.Black
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .background(color = color)
                            )
                        }
                    }
                }
            }
        }
    }
}