package com.carterhudson.rxoptional;


import com.carterhudson.rxoptional.utils.objects.TestObject;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import io.reactivex.schedulers.Schedulers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class RxOptionalTest {

    @Test
    public void of_with_null_value() {
        try {
            RxOptional.of(null);
        } catch (NullPointerException e) {
            assertTrue(true);
            return;
        }

        fail("Expected NPE");
    }

    @Test
    public void of_with_non_null_value() {
        assertEquals(RxOptional.of("a").get(), "a");
    }

    @Test
    public void empty() {
        assertEquals(RxOptional.empty().isPresent(), false);
    }

    @Test
    public void ofNullable_null_value() {
        assertFalse(RxOptional.ofNullable(null).isPresent());
    }

    @Test
    public void ofNullable_non_null_value() {
        assertTrue(RxOptional.ofNullable("a").isPresent());
    }

    @Test
    public void ifPresent_with_value() {
        Object value = new Object();
        RxOptional.of(value).ifPresent(v -> {
            assertNotNull(v);
            assertEquals(v, value);
        });
    }

    @Test
    public void ifPresent_without_value() {
        RxOptional.ofNullable(null).ifPresent(array -> fail("Shouldn't be called"));
    }

    @Test
    public void filter_fail_predicate() {
        String a = "a";
        RxOptional<String> b = RxOptional.of(a).filter(val -> a.equals("b"));
        assertEquals(b.isPresent(), RxOptional.empty().isPresent());
    }

    @Test
    public void filter_pass_predicate() {
        String a = "a";
        RxOptional<String> b = RxOptional.of(a).filter(val -> a.equals("a"));
        assertEquals(b.get(), a);
    }

    @Test
    public void filter_null_predicate_value_present() {
        try {
            RxOptional.of("a").filter(null);
        } catch (NullPointerException e) {
            assertTrue(true);
            return;
        }

        fail("Expected NPE");
    }

    @Test
    public void get_with_value() {
        Object value = new Object();
        assertEquals(RxOptional.of(value).get(), value);
    }

    @Test
    public void get_without_value() {
        try {
            RxOptional.empty().get();
        } catch (NoSuchElementException e) {
            assertTrue(true);
            return;
        }

        fail("Expected NoSuchElementException");
    }

    @Test
    public void orElse() {
        Object defaultValue = new Object();
        Object value = RxOptional.ofNullable(null).orElse(defaultValue);
        assertEquals(defaultValue, value);
    }

    @Test
    public void orElseGet() {
        String value = RxOptional.<String>ofNullable(null).orElseGet(() -> "a");
        assertEquals(value, "a");
    }

    @Test
    public void map() {
        String b = "b";
        RxOptional<String> B = RxOptional.of(b);
        RxOptional<String> A = RxOptional.of("a").map(a -> "b");
        assertEquals(B, A);

        assertEquals(RxOptional.ofNullable(null).map(d -> d), RxOptional.empty());
    }

    @Test
    public void flatMap() {
        String b = "b";
        RxOptional<String> B = RxOptional.of(b);
        RxOptional<String> A = RxOptional.of("a").flatMap(a -> RxOptional.of("b"));
        assertEquals(B, A);
    }

    @Test
    public void toObservable() {
        RxOptional.of("b").toObservable().subscribeOn(Schedulers.trampoline()).test().assertValue("b");
    }

    @Test
    public void valid_iterable() {
        /* Observing a list-backed optional; performs as expected */
        List<String> stringList = new ArrayList<>(Arrays.asList("a", "b", "c"));
        RxOptional.ofNullable(stringList)
                  .toObservable()
                  .subscribeOn(Schedulers.trampoline())
                  .flatMapIterable(strings -> strings)
                  .flatMap(string -> RxOptional.ofNullable(string).toObservable())
                  .filter(string -> string != null)
                  .toList()
                  .test()
                  .assertValue(list -> list.size() == 3)
                  .assertValue(stringList);
    }

    @Test
    public void empty_iterable() {
        /* Observing empty list and performing flat map propagates empties downstream */
        List<String> stringList = new ArrayList<>();
        RxOptional.ofNullable(stringList)
                  .toObservable()
                  .subscribeOn(Schedulers.trampoline())
                  .flatMapIterable(strings -> strings)
                  .flatMap(string -> RxOptional.ofNullable(string).toObservable())
                  .filter(string -> string != null)
                  .toList()
                  .test()
                  .assertValue(list -> list.size() == 0)
                  .assertValue(stringList);
    }

    @Test
    public void null_iterable() {
        /* Observing null-backed optional propagates downstream as empties*/
        List<String> stringList = null;
        RxOptional.ofNullable(stringList)
                  .toObservable()
                  .subscribeOn(Schedulers.trampoline())
                  .flatMapIterable(strings -> strings)
                  .flatMap(string -> RxOptional.ofNullable(string).toObservable())
                  .filter(string -> string != null)
                  .toList()
                  .test()
                  .assertValue(list -> list.size() == 0)
                  .assertValue(Collections.emptyList());
    }

    @Test
    public void flatOrElse() {
        /* Stay in the optional monad */
        RxOptional.ofNullable(null)
                  .flatOrElse("a")
                  .toObservable()
                  .subscribeOn(Schedulers.trampoline())
                  .test()
                  .assertValue("a");
    }

    @Test
    public void flatOrElse_null() {
        /* Emits no values, but completes */
        RxOptional.ofNullable(null)
                  .flatOrElse(null)
                  .toObservable()
                  .subscribeOn(Schedulers.trampoline())
                  .map(val -> "a")
                  .test()
                  .assertValueCount(0)
                  .assertComplete();
    }

    @Test
    public void flatOrElse_ifPresent() {
        RxOptional.ofNullable(null)
                  .flatOrElse(RxOptional.ofNullable(null).orElse("a"))
                  .map(val -> {
                      assertEquals(val, "a");
                      return "b";
                  })
                  .ifPresent(value -> assertEquals(value, "b"));
    }

    @Test
    public void flatOrElse_ifPresent_all_nulls() {
        RxOptional.ofNullable(null)
                  .flatOrElse(null)
                  .map(val -> null)
                  .ifPresent(value -> assertEquals(value, "a"));
    }

    @Test
    public void flatOrElse_ifPresent_inner_optional() {
        TestObject<TestObject<String>> nullTestObject = new TestObject<>(null);
        TestObject<TestObject<String>> nestedTestObject = new TestObject<>(null);
        RxOptional.ofNullable(nullTestObject.getValue())
                  .flatOrElse(nestedTestObject.getValue())
                  .map(testObj -> RxOptional.ofNullable(testObj.getValue()).orElse("a"))
                  .ifPresent(value -> assertEquals(value, "a"));
    }
}
