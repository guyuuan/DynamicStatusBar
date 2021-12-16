cmake_minimum_required(VERSION 3.10.2)

set(libs ${CMAKE_SOURCE_DIR}/src/main/jniLibs)
#导入libopencv_java4库文件
#add_library(libopencv SHARED IMPORTED)
#set_target_properties(libopencv PROPERTIES
#        IMPORTED_LOCATION "${libs}/${ANDROID_ABI}/libopencv_java4.so")
#导入头文件
#include_directories(${CMAKE_SOURCE_DIR}/src/main/opencv/include)


ADD_LIBRARY(
        image-lib
        SHARED
        src/main/jni/image.cpp
        src/main/jni/mlog.h
)

find_library(
        log-lib
        log
)
find_library(
        jnigraphics-lib
        jnigraphics
)

target_link_libraries(
        image-lib
        ${log-lib}
        ${jnigraphics-lib}
)