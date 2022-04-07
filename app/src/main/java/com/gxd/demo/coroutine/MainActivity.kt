package com.gxd.demo.coroutine

import android.app.Activity
import android.os.Bundle
import com.gxd.demo.coroutine.databinding.ActivityMainBinding

class MainActivity : Activity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.mainTitleTv.text = "Hello Coroutine!"
    }
}