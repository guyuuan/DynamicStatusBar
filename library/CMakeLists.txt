cmake_minimum_required(VERSION 3.22.1)

set(libs ${CMAKE_SOURCE_DIR}/src/main/jniLibs)
#导入libopencv_java4库文件
#add_library(libopencv SHARED IMPORTED)
#set_target_properties(libopencv PROPERTIES
#        IMPORTED_LOCATION "${libs}/${ANDROID_ABI}/libopencv_java4.so")
#导入头文件
#include_directories(${CMAKE_SOURCE_DIR}/src/main/opencv/include)


ADD_LIBRARY(
        imganalyst
        SHARED
        src/main/jni/imganalyst.cpp
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
        imganalyst
        ${log-lib}
        ${jnigraphics-lib}
)

# 去除未使用函数与变量
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -ffunction-sections -fdata-sections")
set(CMAKE_CXX_FLAGS "${CMAKE_C_FLAGS}")
# 设置去除未使用代码的链接flag
SET_TARGET_PROPERTIES(imganalyst PROPERTIES LINK_FLAGS "-Wl,--gc-sections")
#去除冗余代码
#SET_TARGET_PROPERTIES(imganalyst PROPERTIES LINK_FLAGS "-Wl,--gc-sections,--icf=safe")

#设置编译器的优化flag
#编译器有个优化flag可以设置，分别是-Os（体积最小），-O3(性能最优)等。这里将编译器的优化flag设置为-Os，以便减少体积。
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Os")
set(CMAKE_CXX_FLAGS "${CMAKE_C_FLAGS}")