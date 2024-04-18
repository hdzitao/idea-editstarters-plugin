package io.github.hdzitao.editstarters.cache

import java.util.*
import java.util.function.Function

/**
 * 内存缓存
 *
 * @version 3.2.0
 */
class MemoryCache<K, V>(private val transFun: Function<K, V>) {
    private val cache = WeakHashMap<K, V>()

    fun get(k: K): V = cache.computeIfAbsent(k, transFun)
}
