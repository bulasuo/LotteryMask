//
// Created by abu on 2018/3/16.
//
#include "com_abu_andtest_JNIUtil.h"

JNIEXPORT jstring JNICALL Java_com_abu_andtest_JNIUtil_getWorld
  (JNIEnv *env, jobject jobj)
  {
    return env->NewStringUTF((char *)"Hello JNI !");
  }

