#include <jni.h>

JNIEXPORT jlong JNICALL
Java_com_example_user_androidhive_CameraView_1Activity_loadCascade(JNIEnv *env, jclass type,
                                                                   jstring cascadeFileName_) {
    const char *cascadeFileName = (*env)->GetStringUTFChars(env, cascadeFileName_, 0);

    // TODO

    (*env)->ReleaseStringUTFChars(env, cascadeFileName_, cascadeFileName);
}