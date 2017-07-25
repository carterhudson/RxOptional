package com.carterhudson.rxoptional;


public class Outer {
    private String id;
    private Inner inner;

    public Outer id(String id) {
        this.id = id;
        return this;
    }

    public RxOptional<String> id() {
        return RxOptional.maybe(this.id);
    }

    public Outer inner(Inner inner) {
        this.inner = inner;
        return this;
    }

    public RxOptional<Inner> inner() {
        return RxOptional.maybe(inner);
    }

    public static class Inner {
        private String id;

        public Inner id(String id) {
            this.id = id;
            return this;
        }

        public RxOptional<String> id() {
            return RxOptional.maybe(this.id);
        }
    }
}