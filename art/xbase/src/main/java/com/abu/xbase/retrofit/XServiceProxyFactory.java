package com.abu.xbase.retrofit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import retrofit2.Call;

/**
 * @author abu
 *         2018/3/22    14:26
 *         ..
 */
@Deprecated
public class XServiceProxyFactory implements InvocationHandler {
    private Object target;

    private XServiceProxyFactory() {
        throw new IllegalArgumentException("请使用私有构造 XServiceProxyFactory(Object target)");
    }

    private XServiceProxyFactory(Object target) {
        this.target = target;
    }

    private static final HashMap<Object, Call> mTasks = new HashMap<>();

//    protected boolean addTask(Object tag, Call call) {
//        return mTasks.put(tag, call);
//    }


    /**
     * 创建代理类
     *
     * @param service 需要代理的实体service
     * @param <T>     需要代理的实体service的类
     * @return 代理
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T service) {
        return (T) Proxy.newProxyInstance(
                service.getClass().getClassLoader(),
                service.getClass().getInterfaces(),
                new XServiceProxyFactory(service));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = method.invoke(target, args);
        if(result instanceof Call)
            ;
        return result;
    }
}
