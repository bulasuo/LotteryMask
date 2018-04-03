package com.abu.xbase.Task;

/**
 * @author abu
 *         2018/2/8    14:06
 *         ..
 */

@FunctionalInterface
public interface TaskT<T> {
    void apply(T t);
}
