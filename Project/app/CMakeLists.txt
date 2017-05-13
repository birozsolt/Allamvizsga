# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)
set(EXECUTABLE_OUTPUT_PATH      "${CMAKE_CURRENT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}")

# CMAKE settings
set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

 add_library( opencv_java SHARED IMPORTED )
 add_library( nonfree SHARED IMPORTED )
 add_library( gnustl_shared SHARED IMPORTED )
 add_library( # Sets the name of the library.
              sift-test
              # Sets the library as a shared library.
              SHARED
              # Provides a relative path to your source file(s).
              src/main/cpp/sift-test.cpp )
 include_directories(D:/Egyetem/Allamvizsga/OpenCV-2.4.10-android-sdk/sdk/native/jni/include)
 set_target_properties( # Specifies the target library.
                        nonfree
                        # Specifies the parameter you want to define.
                        PROPERTIES IMPORTED_LOCATION
                        # Provides the path to the library you want to import.
                        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libnonfree.so )
 set_target_properties(opencv_java
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_java.so)
 set_target_properties(gnustl_shared
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libgnustl_shared.so)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       sift-test
                       # prebuilt library libgnustl_shared.
                       opencv_java
                       # prebuilt library libnonfree.
                       nonfree
                       # prebuilt library libgnustl_shared.
                       gnustl_shared

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )