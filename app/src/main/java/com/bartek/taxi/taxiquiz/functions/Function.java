package com.bartek.taxi.taxiquiz.functions;

import java.io.IOException;

public interface Function<T, R> {
    R apply(T t) throws IOException;
}
