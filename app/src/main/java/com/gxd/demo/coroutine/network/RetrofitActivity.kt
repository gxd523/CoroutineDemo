package com.gxd.demo.coroutine.network

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.gxd.demo.coroutine.R
import com.gxd.demo.coroutine.databinding.ActivityRetrofitBinding
import com.gxd.demo.coroutine.network.data.CheckTokenRequest
import kotlinx.coroutines.launch


class RetrofitActivity : ComponentActivity(), View.OnClickListener {
    private val binding by lazy {
        ActivityRetrofitBinding.inflate(layoutInflater).apply { setContentView(root) }
    }
    private val viewModel: MyViewModel by viewModels()
    private val wanAndroidApi by lazy {
        RetrofitObj.retrofit.create(WanAndroidApi::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit)

        binding.retrofitTextTv.apply { movementMethod = ScrollingMovementMethod.getInstance() }
        binding.retrofitGetBtn.setOnClickListener(this)
        binding.retrofitPostBtn.setOnClickListener(this)
        binding.retrofitJsonBodyPostBtn.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        lifecycleScope.launch {
            try {
                when (view?.id) {
                    R.id.retrofit_get_btn -> wanAndroidApi.requestBannerList("json")
                    R.id.retrofit_post_btn -> wanAndroidApi.login("guoxiaodong", "Wdmzjgxd523e")
                    R.id.retrofit_json_body_post_btn -> wanAndroidApi.checkToken(
                        CheckTokenRequest("11041d5e9430037663edcd81e3722332")
                    )
                    else -> {}
                }
            } catch (e: Exception) {
                e
            }.toString().let(binding.retrofitTextTv::setText)
        }
    }
}