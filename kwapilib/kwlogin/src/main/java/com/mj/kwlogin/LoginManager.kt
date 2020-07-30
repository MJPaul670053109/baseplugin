package com.mj.kwlogin

import android.content.Context
import android.content.Intent


class LoginManager : ILogin {
    override fun startLogin(context: Context) {
        context.startActivity(Intent(context,LoginActivity::class.java))
    }


}