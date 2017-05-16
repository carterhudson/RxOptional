package com.carterhudson.rxoptional.utils.objects;


import android.support.annotation.Nullable;

public class TestObject<T> {
    T value;

    public TestObject(T value) {
        this.value = value;
    }

    @Nullable
    public T getValue() {
        return value;
    }
}
