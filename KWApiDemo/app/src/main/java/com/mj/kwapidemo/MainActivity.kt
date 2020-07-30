package com.mj.kwapidemo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.haiziwang.base.KwPluginMap
import com.mj.kwaccount.IAccount
import com.mj.kwlogin.ILogin
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.util.concurrent.locks.ReentrantLock

class MainActivity : AppCompatActivity() {

    private var accountManager: IAccount? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        accountManager = KwPluginMap.visit(IAccount::class.java)


        val loginManager = KwPluginMap.visit(ILogin::class.java)
        tv_show?.setOnClickListener {
            accountManager?.saveUserName(edt_desc?.text?.trim().toString())
            Toast.makeText(this, accountManager?.userName ?: "", Toast.LENGTH_SHORT).show()
            loginManager.startLogin(this)
        }

        val test = Test2()

        Thread(Runnable {
            Test2().test()
        }).start()

        Thread(Runnable {
            Test2().test()
        }).start()
    }


    class Test {

        fun test() {
            synchronized(Test::class.java) {
                (0..5).forEach {
                    Log.e("ssss", Thread.currentThread().name.toString() + it)
                }
            }
        }
    }


    class Test2 {
        private val lock = ReentrantLock()
        fun test() {
            lock.lock()
            try {
                (0..5).forEach {
                    Log.e("ssss", Thread.currentThread().name.toString() + it)
                }
            } catch (e: Exception) {

            } finally {
                lock.unlock()
            }
        }
    }


}
