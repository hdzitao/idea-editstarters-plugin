package io.github.hdzitao.editstarters.utils;

import com.google.gson.Gson;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * bean结构持久化
 *
 * @version 3.2.0
 */
public abstract class JSONConverter<T> extends Converter<T> {
    private final Gson gson = new Gson();

    @Override
    public @Nullable T fromString(@NotNull String value) {
        Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        return gson.fromJson(value, type);
    }

    @Override
    public @Nullable String toString(@NotNull T value) {
        return gson.toJson(value);
    }
}
