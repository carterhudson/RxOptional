package com.carterhudson.rxoptional;


import org.junit.Test;

import java.util.NoSuchElementException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class RxOptionalTest {

    @Test
    public void of_with_null_value() {
        try {
            RxOptional.of(null);
        } catch (NullPointerException e) {
            assertTrue(true);
            return;
        }

        assertFalse("Expected NPE", false);
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
        RxOptional.ofNullable(null).ifPresent(array -> {
            assertFalse("Shouldn't be called", false);
        });

        assertTrue(true);
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

        assertFalse("Expected NPE", false);
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

        assertFalse("Expected NoSuchElementException", false);
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
}
