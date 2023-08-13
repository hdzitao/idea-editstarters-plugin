package io.github.hdzitao.editstarters.cache;

import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * 内存缓存
 *
 * @version 3.2.0
 */
public class MemoryCache<K, V> {
    private final WeakHashMap<K, V> cache = new WeakHashMap<>();
    private final Function<K, V> transFun;


    public MemoryCache(Function<K, V> transFun) {
        this.transFun = transFun;
    }

    public V get(K k) {
        return cache.computeIfAbsent(k, transFun);
    }
}
