package com.carterhudson.rxoptional;


import com.carterhudson.rxoptional.support.Supplier;

import java.util.NoSuchElementException;
import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static java.util.Objects.requireNonNull;

public class RxOptional<T> {
    private final T value;

    private static final RxOptional<?> EMPTY = new RxOptional();

    private RxOptional() {
        this.value = null;
    }

    private RxOptional(T value) {
        this.value = value;
    }

    public static <T> RxOptional<T> of(T value) {
        return new RxOptional<>(requireNonNull(value));
    }

    public static <T> RxOptional<T> empty() {
        //noinspection unchecked
        return (RxOptional<T>) EMPTY;
    }

    public static <T> RxOptional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    public RxOptional<T> filter(Predicate<? super T> predicate) {
        if (value == null) {
            return this;
        }

        requireNonNull(predicate);
        return Single.just(value)
                     .filter(predicate)
                     .map(RxOptional::of)
                     .defaultIfEmpty(empty())
                     .blockingGet();
    }

    public boolean isPresent() {
        return value != null;
    }

    public T get() {
        if (!isPresent()) {
            throw new NoSuchElementException();
        }

        return value;
    }

    public void ifPresent(Consumer<? super T> consumer) {
        if (value != null) {
            requireNonNull(consumer);
            Single.just(value).subscribe(consumer);
        }
    }

    public <U> RxOptional<U> map(Function<? super T, ? extends U> mapper) {
        if (value != null) {
            requireNonNull(mapper);
        } else {
            return empty();
        }

        return RxOptional.of(Single.just(value).map(mapper).blockingGet());
    }

    public <U> RxOptional<U> flatMap(Function<? super T, ? extends RxOptional<U>> mapper) {
        if (value != null) {
            requireNonNull(mapper);
        } else {
            return empty();
        }

        return Single.just(value).map(mapper).blockingGet();
    }

    public T orElse(T other) {
        return isPresent() ? value : other;
    }

    public T orElseGet(Supplier<T> other) {
        if (!isPresent()) {
            requireNonNull(other);
        }

        return orElse(other.get());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RxOptional)) {
            return false;
        }

        RxOptional<?> that = (RxOptional<?>) obj;

        return (!this.isPresent() && !that.isPresent()) || this.get().equals(that.get());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value == null ? "Optional[empty]" : String.format("Optional[%s]", value.toString());
    }
}
