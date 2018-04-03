package com.abu.xbase.Task;

/**
 * @author abu
 *         2018/2/8    14:10
 *         ..
 */

@FunctionalInterface
public interface TaskTR<T, R> {

    R apply(T t);
}
