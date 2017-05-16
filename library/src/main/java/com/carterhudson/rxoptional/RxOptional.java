package com.carterhudson.rxoptional;


import com.carterhudson.rxoptional.support.Supplier;

import java.util.NoSuchElementException;
import java.util.Objects;

import io.reactivex.Observable;
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

    /**
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html#empty--">Oracle docs</a>
     */
    public static <T> RxOptional<T> empty() {
        //noinspection unchecked
        return (RxOptional<T>) EMPTY;
    }

    /**
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html#of-T-">Oracle docs</a>
     */
    public static <T> RxOptional<T> of(T value) {
        return new RxOptional<>(requireNonNull(value));
    }

    /**
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html#ofNullable-T-">Oracle docs</a>
     */
    public static <T> RxOptional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    /**
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html#filter-java.util.function.Predicate-">Oracle docs</a>
     */
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

    /**
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html#isPresent--">Oracle docs</a>
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html#get--">Oracle docs</a>
     */
    public T get() {
        if (!isPresent()) {
            throw new NoSuchElementException();
        }

        return value;
    }

    /**
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html#ifPresent-java.util.function.Consumer-">Oracle docs</a>
     */
    public RxOptional<T> ifPresent(Consumer<? super T> consumer) {
        if (value != null) {
            requireNonNull(consumer);
            Single.just(value).subscribe(consumer);
        }

        return this;
    }

    /**
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html#map-java.util.function.Function-">Oracle docs</a>
     */
    public <U> RxOptional<U> map(Function<? super T, ? extends U> mapper) {
        if (value != null) {
            requireNonNull(mapper);
        } else {
            return empty();
        }

        return RxOptional.of(Single.just(value).map(mapper).blockingGet());
    }

    /**
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html#flatMap-java.util.function.Function-">Oracle docs</a>
     */
    public <U> RxOptional<U> flatMap(Function<? super T, ? extends RxOptional<U>> mapper) {
        if (value != null) {
            requireNonNull(mapper);
        } else {
            return empty();
        }

        return Single.just(value).map(mapper).blockingGet();
    }

    /**
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html#orElse-T-">Original docs</a>
     */
    public T orElse(T other) {
        return isPresent() ? value : other;
    }

    /**
     * A flat implementation of {@link #orElse(Object)} that returns an RxOptional describing a wrapped value.
     * This is useful for instances where you'd like to provide an else-value, but chain fluently from it with
     * other RxOptional supported operations.
     *
     * @param other - the value to be described by the returned RxOptional
     * @return - an RxOptional describing the else-value
     */
    public RxOptional<T> flatOrElse(T other) {
        return RxOptional.of(orElse(other));
    }

    /**
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html#orElseGet-java.util.function.Supplier-">Oracle docs</a>
     */
    public T orElseGet(Supplier<T> other) {
        if (!isPresent()) {
            requireNonNull(other);
        }

        return orElse(other.get());
    }

    /**
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html#equals-java.lang.Object-">Oracle docs</a>
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RxOptional)) {
            return false;
        }

        RxOptional<?> that = (RxOptional<?>) obj;

        return (!this.isPresent() && !that.isPresent()) || this.get().equals(that.get());
    }

    /**
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html#hashCode--">Oracle docs</a>
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html#toString--">Oracle docs</a>
     */
    @Override
    public String toString() {
        return value == null ? "Optional[empty]" : String.format("Optional[%s]", value.toString());
    }

    /**
     * Enters the RxJava 2 monad for more robust reactive extension operations outside the scope of Java 8's implementation
     *
     * @return - An observable instance of the value described by the optional
     */
    public Observable<T> toObservable() {
        return value != null ? Observable.just(value) : Observable.empty();
    }

    // TODO: 5/16/17 single-method support for common combinations of reactive extension combinations
}
