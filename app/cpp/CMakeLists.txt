cmake_minimum_required(VERSION 3.4.1)

set(MODULE_NAME encryptor-native-lib)

set(${MODULE_NAME}_SRC
        encryptor/aes/aes.h
        encryptor/aes/aes.c
        encryptor/encryptor.h
        encryptor/encryptor.c
        native-lib.cpp)

add_library(${MODULE_NAME} SHARED ${${MODULE_NAME}_SRC})

find_library(log-lib log)

target_link_libraries(${MODULE_NAME} ${log-lib})