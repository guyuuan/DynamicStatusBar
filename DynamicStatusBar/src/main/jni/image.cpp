//
// Created by Chen on 2021/7/2.
//

#include <jni.h>
#include <string>
#include "mlog.h"
#include "android/bitmap.h"
#include "opencv2/opencv.hpp"
#include "opencv2/core.hpp"

#define ASSERT(status, ret)     if (!(status)) { return ret; }
#define ASSERT_FALSE(status)    ASSERT(status, false)

extern "C" {
jstring jni_getString(JNIEnv *env, jobject obj) {
    std::string str = "native  has been initialized ";
    return env->NewStringUTF(str.c_str());
}
jint jni_getBright(JNIEnv *env, jobject jobj, jobject bitmap) {
    void *bitmapPixels;                                            // Save picture pixel data
    AndroidBitmapInfo bitmapInfo;                                   // Save picture parameters
    cv::Mat mat;
    cv::Mat gray;
    ASSERT_FALSE(AndroidBitmap_getInfo(env, bitmap, &bitmapInfo) >= 0);        // Get picture parameters
    ASSERT_FALSE(bitmapInfo.format == ANDROID_BITMAP_FORMAT_RGBA_8888|| bitmapInfo.format ==ANDROID_BITMAP_FORMAT_RGB_565);          // Only ARGB? 8888 and RGB? 565 are supported
    ASSERT_FALSE(AndroidBitmap_lockPixels(env, bitmap, &bitmapPixels) >=0);  // Get picture pixels (lock memory block)
    ASSERT_FALSE(bitmapPixels);

    if (bitmapInfo.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
        cv::Mat tmp(bitmapInfo.height, bitmapInfo.width, CV_8UC4,bitmapPixels);    // Establish temporary mat
        tmp.copyTo(
                mat);                                                         // Copy to target matrix
    } else {
        cv::Mat tmp(bitmapInfo.height, bitmapInfo.width, CV_8UC2, bitmapPixels);
        cv::cvtColor(tmp, mat, cv::COLOR_BGR5652RGB);
    }
    cv::cvtColor(mat, gray, cv::COLOR_BGR2GRAY, 0);
    cv::Scalar s = cv::mean(gray);
    int bright = (int)s.val[0];
    LOGD("jni bright %lf",s.val[0]);
    return (jint) bright;
}
static char *className = "cn/chitanda/dynamicstatusbar/NativeAnalyst";
static JNINativeMethod methods[] = {
        {"init",      "()Ljava/lang/String;",         (jstring *) jni_getString},
        {"getBright", "(Landroid/graphics/Bitmap;)I", (jint *) jni_getBright}

};

JNIEXPORT jint JNI_OnLoad(JavaVM *javaVm, void *) {
    JNIEnv *env = nullptr;
    int r = javaVm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6);
    LOGD("start get JNIEnv");
    if (r != JNI_OK) {
        LOGE("can't get JNIEnv");
        return JNI_ERR;
    }
    jclass clazz = nullptr;
    LOGD("start get %s", className);
    clazz = env->FindClass(className);
    if (clazz == nullptr) {
        LOGE("can't get %s", className);
        return JNI_ERR;
    }
    LOGD("start register natives");
    r = env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(JNINativeMethod));
    if (r != JNI_OK) {
        LOGE("register natives failed");
        return JNI_ERR;
    }
    return JNI_VERSION_1_6;
}
}