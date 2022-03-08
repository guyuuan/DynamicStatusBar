package cn.chitanda.dynamicstatusbar

import android.graphics.Bitmap

/**
 *@author: Chen
 *@createTime: 2021/7/2 13:51
 *@description:
 **/
class NativeAnalyst {
    companion object {
        init {
            System.loadLibrary("imganalyst")
        }
    }

    external fun getBright(bitmap: Bitmap): Int
}