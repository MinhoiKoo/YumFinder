package com.minhoi.recipeapp

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_API_KEY)
        var keyHash = Utility.getKeyHash(this)
        Log.d("GlobalApplication", "$keyHash")
    }
}