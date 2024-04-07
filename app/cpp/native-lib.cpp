#include <jni.h>
#include <string>

#include "encryptor/encryptor.h"

extern "C"
{
JNIEXPORT jint JNICALL
Java_com_example_cryptapp_MainActivity_encryptor_1init(JNIEnv *env, jobject thiz) {
    return encryptor_init();
}

JNIEXPORT jint JNICALL
Java_com_example_cryptapp_MainActivity_encryptor_1encryptFile(
        JNIEnv *env, jobject thiz,
        jstring filename,
        jstring new_filename,
        jstring iv) {
    return encryptor_encrypt_file(
            env->GetStringUTFChars(filename, JNI_FALSE),
            env->GetStringUTFChars(new_filename, JNI_FALSE),
            env->GetStringUTFChars(iv, JNI_FALSE));
}

JNIEXPORT jint JNICALL
Java_com_example_cryptapp_MainActivity_encryptor_1decryptFile(
        JNIEnv *env, jobject thiz,
        jstring filename,
        jstring new_filename,
        jstring iv) {
    return encryptor_decrypt_file(
            env->GetStringUTFChars(filename, JNI_FALSE),
            env->GetStringUTFChars(new_filename, JNI_FALSE),
            env->GetStringUTFChars(iv, JNI_FALSE));
}


}