package io.github.hdzitao.editstarters.cache

import com.google.gson.Gson
import com.intellij.util.xmlb.Converter
import java.lang.reflect.ParameterizedType

/**
 * bean结构持久化
 *
 * @version 3.2.0
 */
abstract class JSONConverter<T> : Converter<T>() {
    private val gson = Gson()

    override fun fromString(value: String): T {
        val type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]

        return gson.fromJson(value, type)
    }

    override fun toString(value: T & Any): String? {
        return gson.toJson(value)
    }
}
