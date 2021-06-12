package com.yumin.todolist.data

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.yumin.todolist.LogUtils

class SharedPreferenceLiveData<T>: LiveData<T> {
    private var mKey: String ? = null
    private var mDefaultValue: T? = null
    private lateinit var mSharedPreferences: SharedPreferences

    constructor( key: String, defaultValue: T, sharedPreferences: SharedPreferences) {
        this.mKey = key
        this.mDefaultValue = defaultValue
        this.mSharedPreferences = sharedPreferences
    }

    private val listener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (mKey == key) {
                val value: T = mSharedPreferences.all[key] as T
                LogUtils.logD(TAG,"[onSharedPreferenceChanged] KEY = $key, VALUE = $value")
                setValue(value ?: mDefaultValue)
            }
        }

    override fun onActive() {
        super.onActive()
        val value: T = mSharedPreferences.all[mKey] as T
        setValue(value ?: mDefaultValue)
        LogUtils.logD(TAG,"[onActive] KEY = $mKey, VALUE = $value")
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    companion object{
        val TAG: String = SharedPreferenceLiveData::class.java.simpleName
    }
}