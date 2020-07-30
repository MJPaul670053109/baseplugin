package com.haiziwang.base
import android.app.Application
abstract class ModulePlugin : IPlugin {
    open fun initPlugin(s: Application) {}
}