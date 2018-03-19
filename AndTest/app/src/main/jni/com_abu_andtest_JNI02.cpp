//
// Created by abu on 2018/3/16.
//
#include "com_abu_andtest_JNI02.h"

JNIEXPORT jstring JNICALL Java_com_abu_andtest_JNI02_getWorldxxxx
(JNIEnv *env, jobject obj)
{

    jmethodID mid; // 方法标识id
    jclass cls = env->GetObjectClass(obj); // 类的对象实例


    mid = env->GetMethodID(cls, "javaMethod1", "()V");
    env->CallVoidMethod(obj, mid);


    jstring input = env->NewStringUTF("jniCallStaticMethod->>javaStaticMethod");
    jmethodID mid2 = env->GetMethodID(cls, "javaMethod2", "(Ljava/lang/String;)V");
    env->CallVoidMethod(obj, mid2, input);//CallObjectMethod java方法返回非void

    return env->NewStringUTF((char *)"Hello JNI xxxxxx!");
}

