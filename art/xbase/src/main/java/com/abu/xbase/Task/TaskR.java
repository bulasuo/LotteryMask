package com.abu.xbase.Task;

/**
 * @author abu
 *         2018/2/8    14:23
 *         bulasuo@foxmail.com
 */

@FunctionalInterface
public interface TaskR<R> {

    R apply();
}
