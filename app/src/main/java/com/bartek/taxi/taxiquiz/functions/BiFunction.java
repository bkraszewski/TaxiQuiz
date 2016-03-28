package com.bartek.taxi.taxiquiz.functions;

public interface BiFunction<T, U, R> {
    R apply(T t, U u);
}