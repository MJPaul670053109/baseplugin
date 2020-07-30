package com.mj.kwlogin

import android.app.Application
import com.haiziwang.base.KwPluginMap
import com.haiziwang.base.ModulePlugin

class LoginPlugin : ModulePlugin() {


    override fun initPlugin(s: Application) {
//        Log.e("sss", application.toString())
        KwPluginMap.register(ILogin::class.java, LoginManager())
    }
}