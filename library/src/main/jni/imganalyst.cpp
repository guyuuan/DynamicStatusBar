//
// Created by Chen on 2021/7/2.
//

#include <jni.h>
#include <string>
#include "mlog.h"
#include "android/bitmap.h"

//(A & 0xff) << 24 | (B & 0xff) << 16 | (G & 0xff) << 8 | (R & 0xff);
#define BGR_8888_A(p) ((p & (0xff<<24))   >> 24 )
#define BGR_8888_B(p) ((p & (0xff << 16)) >> 16 )
#define BGR_8888_G(p) ((p & (0xff << 8))  >> 8 )
#define BGR_8888_R(p) (p & (0xff) )

extern "C" {

jint jni_getBright(JNIEnv *env,jobject , jobject bitmap) {
    AndroidBitmapInfo bitmapInfo;
    int result = AndroidBitmap_getInfo(env, bitmap, &bitmapInfo);
    if (ANDROID_BITMAP_RESULT_SUCCESS != result) {
        LOG_E("get bitmap info error :%d", result);
    }
    //获取Bitmap像素缓存指针,通过遍历获取BGRA数据
    void *p = nullptr;
    result = AndroidBitmap_lockPixels(env, bitmap, &p);
    if (ANDROID_BITMAP_RESULT_SUCCESS != result) {
        LOG_E("lock bitmap pixels error :%d", result);
    }

    //获取图片宽高
    uint32_t w = bitmapInfo.width;
    uint32_t h = bitmapInfo.height;

    auto pixels = (uint32_t *) p;
    int  r, g, b;
    uint32_t bright = 0;
    for (int x = 0; x < w; ++x) {
        for (int y = 0; y < h; ++y) {
            void *pixel;
            pixel = pixels + y * w + x;
            uint32_t v = *((uint32_t *) pixel);
            r = BGR_8888_R(v);
            g = BGR_8888_G(v);
            b = BGR_8888_B(v);
            bright += (uint32_t) (0.299f * (double) r + 0.587f * (double) g + 0.114f * (double) b);
        }
    }
    bright = (bright / (w * h));
    return static_cast<jint>(bright);
}
static std::string className = "io/github/guyuuan/dynamicstatusbar/NativeAnalyst";
static JNINativeMethod methods[] = {
        {"getBright", "(Landroid/graphics/Bitmap;)I", (jint *) jni_getBright}

};

JNIEXPORT jint JNI_OnLoad(JavaVM *javaVm, void *) {
    JNIEnv *env = nullptr;
    int r = javaVm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6);
    if (r != JNI_OK) {
        LOG_E("can't get JNIEnv");
        return JNI_ERR;
    }
    jclass clazz;
    clazz = env->FindClass(className.c_str());
    if (clazz == nullptr) {
        LOG_E("can't get %s", className.c_str());
        return JNI_ERR;
    }
    r = env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(JNINativeMethod));
    if (r != JNI_OK) {
        LOG_E("register natives failed");
        return JNI_ERR;
    }
    return JNI_VERSION_1_6;
}
}