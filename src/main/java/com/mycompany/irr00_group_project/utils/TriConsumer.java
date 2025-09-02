package com.mycompany.irr00_group_project.utils;

/**
 * TriConsumer is a functional interface that represents an 
 * operation that accepts three input arguments and returns no result.
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);
}