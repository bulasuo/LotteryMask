package com.abu.xbase.Task;

/**
 * @author abu
 *         2018/2/8    14:11
 *         bulasuo@foxmail.com
 */

@FunctionalInterface
public interface TaskTUR<T, U, R> {

    R apply(T t, U u);
}
