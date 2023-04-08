package com.bcontrol.app.bcontrol

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bcontrol.app.bcontrol.R


class MainActivity : AppCompatActivity() {
    // val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        Log.d("DEBUG","Main activity created")
        setContentView(R.layout.activity_main)
    }
}