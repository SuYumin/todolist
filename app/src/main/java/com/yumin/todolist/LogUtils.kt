package com.yumin.todolist

import android.util.Log
import androidx.databinding.library.BuildConfig

class LogUtils {
    companion object {
        private var DEBUG: Boolean = BuildConfig.DEBUG;

        @JvmStatic fun logI(tag:String, message:String){
            if (DEBUG)
                Log.i(tag,message)
        }

        @JvmStatic fun logD(tag: String, message: String) {
            if (DEBUG)
                Log.d(tag,message)
        }

        @JvmStatic fun logW(tag: String, message: String) {
            if (DEBUG)
                Log.w(tag,message)
        }

        @JvmStatic fun logE(tag: String, message: String) {
            if (DEBUG)
                Log.e(tag,message)
        }

        @JvmStatic fun logV(tag: String, message: String) {
            if (DEBUG)
                Log.v(tag,message)
        }
    }
}