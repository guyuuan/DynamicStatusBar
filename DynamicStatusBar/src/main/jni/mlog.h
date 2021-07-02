//
// Created by Chen on 2021/7/2.
//
#include "android/log.h"

#ifndef JNI_LOG_H
#define JNI_LOG_H
#define  LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,"Bitmap",__VA_ARGS__)
#define  LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"Bitmap",__VA_ARGS__)
#endif //JNI_LOG_H
