package com.example.cameraxproject.activity

import android.app.Application

class MyApplication : Application() {
    companion object {
        var temFilePath:String=""
    }
    override fun onCreate() {
        super.onCreate()
        temFilePath=filesDir.absolutePath
    }
}