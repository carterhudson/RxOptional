package com.carterhudson.rxoptional;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RxOptionalTest {

    public static final String SHOULD_NOT_EXECUTE = "should not execute";

    @Test
    public void maybe() {
        assertEquals("1", RxOptional.maybe("1").get());
    }

    @Test
    public void or() {
        assertEquals("1", RxOptional.maybe(null).or("1").get());
    }

    @Test
    public void flatten() {
        ArrayList<String> strings = new ArrayList<>(Arrays.asList("1", "2", "3"));
        List<String> actual = RxOptional
            .maybe(strings)
            .flatten(a -> a)
            .filter(s -> !s.equals("2"))
            .toList()
            .blockingGet();
        assertThat(actual, is(not(strings)));
        assertThat(actual.size(), is(2));
        assertThat(actual.get(0), is("1"));
        assertThat(actual.get(1), is("3"));
    }

    @Test
    public void ifPresent_ifNotPresent() {
        ArrayList<Object> holder = new ArrayList<>();
        assertThat(holder.size(), is(0));
        RxOptional.maybe("1").ifPresent(val -> {
            assertThat(val, is("1"));
            holder.add(new Object());
        });
        assertThat(holder.size(), is(1));
        RxOptional
            .maybe(null)
            .ifPresent(val -> fail(SHOULD_NOT_EXECUTE))
            .ifNotPresent(() -> holder.add(new Object()));
        assertThat(holder.size(), is(2));
    }

    @Test
    public void mapIfPresent_mapIfNotPresent() {
        String actual = RxOptional.maybe("1").mapIfPresent(val -> {
            assertThat(val, is("1"));
            return "2";
        }).mapIfNotPresent(() -> {
            fail(SHOULD_NOT_EXECUTE);
            return "0";
        }).get();
        assertThat(actual, is("2"));

        ArrayList<Object> holder = new ArrayList<>();
        String actual2 = RxOptional.maybe(null).mapIfPresent(val -> {
            holder.add(new Object());
            fail(SHOULD_NOT_EXECUTE);
            return "0";
        }).mapIfNotPresent(() -> {
            assertThat(holder.size(), is(0));
            holder.add(new Object());
            return "1";
        }).get();
        assertThat(holder.size(), is(1));
        assertThat(actual2, is("1"));
    }

    @Test
    public void nested_optional_test() {
        Outer outer = new Outer();
        String actual = outer
            .inner()
            .or(new Outer.Inner())
            .get()
            .id()
            .or("")
            .get();

        assertThat(actual, is(""));
    }

    @Test
    public void flatMapIfPresent_flatMapIfNotPresent() {
        Outer outer = new Outer().id("a");
        ArrayList<Object> holder = new ArrayList<>();
        assertThat(holder.size(), is(0));
        RxOptional
            .maybe(outer)
            .flatMapIfPresent(Outer::id)
            .ifNotPresent(() -> fail(SHOULD_NOT_EXECUTE))
            .ifPresent(holder::add);
        assertThat(holder.size(), is(1));
        assertThat(holder.get(0), is("a"));

        RxOptional
            .maybe(null)
            .flatMapIfNotPresent(() -> RxOptional.maybe(new Outer()))
            .ifNotPresent(() -> fail(SHOULD_NOT_EXECUTE))
            .ifPresent(newLock -> holder.clear());
        assertThat(holder.size(), is(0));

        RxOptional
            .maybe(new Outer())
            .flatMapIfPresent(Outer::id)
            .ifNotPresent(() -> holder.add("b"))
            .ifPresent(id -> fail(SHOULD_NOT_EXECUTE));
        assertThat(holder.size(), is(1));
        assertThat(holder.get(0), is("b"));
    }
}
