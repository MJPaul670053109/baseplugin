package com.haiziwang.base

import java.util.concurrent.ConcurrentHashMap

object KwPluginMap {

    private val services = ConcurrentHashMap<Class<*>, Any>()

    @JvmStatic
    fun register(service: Class<*>, serviceImp: Any) {
        services[service] = serviceImp
    }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun <T> visit(service: Class<T>): T {
        if (services.containsKey(service)) {
            return (services[service] as T)
        }
        throw RuntimeException("serviceImp has not register yet.")
    }
}