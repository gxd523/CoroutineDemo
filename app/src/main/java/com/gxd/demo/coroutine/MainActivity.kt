package com.gxd.demo.coroutine

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.gxd.demo.coroutine.databinding.ActivityMainBinding
import com.gxd.demo.coroutine.network.RetrofitActivity

class MainActivity : Activity(), View.OnClickListener {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater).apply { setContentView(root) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.mainNetworkBtn.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.main_network_btn -> RetrofitActivity::class.java
            else -> null
        }?.let { Intent(this, it) }?.let(::startActivity)
    }
}