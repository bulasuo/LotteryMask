package com.abu.xbase.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author   abu
 * 2017/11/7    19:03
 * bulasuo@foxmail.com
 */

public class XProxyUtil {

    /**
     * @author abu   2017/2/14   16:11
     */
    public static String getStrValueByField(Object o, String fieldName) {
        final Object o1 = getObjByField(o, fieldName);
        return o1 == null ? null : String.valueOf(o1);
    }

    /**
     * @param o         载体对象
     * @param fieldName 成员名 that 要反射的
     * @return
     */
    public static Object getObjByField(Object o, String fieldName) {
        Class<?> c;
        Field f;
        Object o1;
        Field[] fields;
        try {
            if (o != null) {
                for (c = o.getClass(); c != Object.class; c = c.getSuperclass()) {
                    fields = c.getDeclaredFields();
                    //允许访问私有
                    Field.setAccessible(fields, true);
                    if (fields != null && fieldName != null) {
                        for (int i = 0; i < fields.length; i++) {
                            if (fieldName.equals((f = fields[i]).getName())) {
                                o1 = f.get(o);
                                if (o1 != null){
                                    return o1;
                                }
                            }
                        }
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 利用递归找一个类的指定方法，如果找不到，去父亲里面找直到最上层Object对象为止。
     *
     * @param clazz      目标类
     * @param methodName 方法名
     * @param classes    方法参数类型数组
     * @return 方法对象
     * @throws Exception
     */
    public static Method getMethod(Class clazz, String methodName,
                                   final Class[] classes) throws Exception {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            try {
                method = clazz.getMethod(methodName, classes);
            } catch (NoSuchMethodException ex) {
                if (clazz.getSuperclass() == null) {
                    return method;
                } else {
                    method = getMethod(clazz.getSuperclass(), methodName,
                            classes);
                }
            }
        }
        return method;
    }

    /**
     * @param obj        调整方法的对象
     * @param methodName 方法名
     * @param classes    参数类型数组
     * @param objects    参数数组
     * @return 方法的返回值
     */
    public static Object invoke(final Object obj, final String methodName,
                                final Class[] classes, final Object[] objects) {
        try {
            Method method = getMethod(obj.getClass(), methodName, classes);
            // 调用private方法的关键一句话
            method.setAccessible(true);
            return method.invoke(obj, objects);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invoke(final Object obj, final String methodName,
                                final Class[] classes) {
        return invoke(obj, methodName, classes, new Object[]{});
    }

    public static Object invoke(final Object obj, final String methodName) {
        return invoke(obj, methodName, new Class[]{}, new Object[]{});
    }
}
