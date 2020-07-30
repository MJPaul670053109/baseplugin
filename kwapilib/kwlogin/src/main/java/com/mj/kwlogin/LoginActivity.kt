package com.mj.kwlogin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.haiziwang.base.KwPluginMap
import com.mj.kwaccount.IAccount
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        val accountManager = KwPluginMap.visit(IAccount::class.java)
        tv_show?.setOnClickListener {
            accountManager?.saveUserName(edt_desc?.text?.trim().toString())
            Toast.makeText(this, accountManager?.userName ?: "", Toast.LENGTH_SHORT).show()
        }
    }
}