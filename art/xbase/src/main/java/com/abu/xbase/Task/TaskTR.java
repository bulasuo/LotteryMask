package com.abu.xbase.Task;

/**
 * @author abu
 *         2018/2/8    14:10
 *         bulasuo@foxmail.com
 */

@FunctionalInterface
public interface TaskTR<T, R> {

    R apply(T t);
}
