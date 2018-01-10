package com.carterhudson.rxoptional;

import com.carterhudson.rxoptional.support.Supplier;

import java.util.NoSuchElementException;

import javax.annotation.Nonnull;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static java.util.Objects.requireNonNull;

public class RxOptional<T> extends Maybe<T> {
    private static RxOptional<?> EMPTY = new RxOptional<>();

    private T value;
    private Maybe<T> delegate;

    private RxOptional() {
        value = null;
    }

    private RxOptional(T value) {
        this.value = value;
        delegate = Maybe.fromCallable(() -> value);
    }

    public static <T> RxOptional<T> maybe(T value) {
        return new RxOptional<>(value);
    }

    @SuppressWarnings("unchecked")
    public static <T> RxOptional<T> empty() {
        return (RxOptional<T>) EMPTY;
    }

    public RxOptional<T> or(T anotherValue) {
        if (value == null) {
            return new RxOptional<>(anotherValue);
        }

        return this;
    }

    public <U> Observable<U> flatten(Function<T, Iterable<? extends U>> mapper) {
        return delegate.flattenAsObservable(mapper);
    }

    public T get() {
        if (value == null) {
            throw new NoSuchElementException(
                "get() called on RxOptional containing null value with no fallback specified");
        }

        return delegate.doOnError(Throwable::printStackTrace).blockingGet();
    }

    @Nonnull
    public RxOptional<T> ifPresent(@Nonnull Consumer<T> consumer) {
        if (value != null) {
            requireNonNull(consumer);
            try {
                consumer.accept(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return this;
    }

    @Nonnull
    public RxOptional<T> ifNotPresent(@Nonnull Action action) {
        if (value == null) {
            requireNonNull(action);
            try {
                action.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return this;
    }

    @Nonnull
    public RxOptional<T> mapIfNotPresent(@Nonnull Supplier<T> supplier) {
        if (value == null) {
            requireNonNull(supplier);
            try {
                return maybe(supplier.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return maybe(value);
    }

    @Nonnull
    public <R> RxOptional<R> mapIfPresent(@Nonnull Function<T, R> function) {
        if (value != null) {
            requireNonNull(function);
            try {
                return maybe(function.apply(value));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return empty();
    }

    @Nonnull
    public RxOptional<T> flatMapIfNotPresent(@Nonnull Supplier<RxOptional<T>> supplier) {
        if (value == null) {
            requireNonNull(supplier);
            try {
                return supplier.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return maybe(value);
    }

    @Nonnull
    public <R> RxOptional<R> flatMapIfPresent(@Nonnull Function<T, RxOptional<R>> function) {
        if (value != null) {
            requireNonNull(function);
            try {
                return function.apply(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return empty();
    }

    @Override
    protected void subscribeActual(MaybeObserver<? super T> observer) {
        delegate.subscribe(observer);
    }
}