package com.demo.coroutine

import android.os.Bundle
import com.demo.coroutine.databinding.ActivityMainBinding
import com.gxd.viewbindingwrapper.ViewBindingActivity

class MainActivity : ViewBindingActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.mainTitleTv.text = "Hello Coroutine!"
    }
}