#include "encryptor.h"

#include <stdio.h>
#include <errno.h>
#include <time.h>
#include <stdlib.h>
#include <string.h>

#include "aes/aes.h"

#define AES_KEY_LENGTH  (32)
#define AES_BLOCK_SIZE  (AES_BLOCKLEN)

const unsigned char const_key_bytes[] = { 0x96, 0x7a, 0x36, 0xe2, 0x42, 0x63, 0xcf, 0xae,
                                          0x03, 0xc6, 0x19, 0x05, 0x22, 0x6e, 0x86, 0x95,
                                          0x5f, 0xac, 0xa2, 0x26, 0x25, 0x17, 0xac, 0xf5,
                                          0x12, 0xf8, 0x26, 0x82, 0xc5, 0x4b, 0xd0, 0xf2 };

unsigned char *key_bytes;

int encryptor_init() {

    // Выделяем память
    key_bytes = (unsigned char *)malloc(sizeof(unsigned char) * AES_KEY_LENGTH);

#if 0
    // Генерируем случайные байты
    srand(time(NULL));
    for (long i = 0; i < AES_KEY_LENGTH; ++i) {
        key_bytes[i] = rand();
    }
#else
    memcpy(key_bytes, const_key_bytes, AES_KEY_LENGTH * sizeof(unsigned char));
#endif

    return 0;
}

void encryptor_free() {
    if (key_bytes != NULL) {
        free(key_bytes);
    }
}

int encryptor_encrypt_file(const char *filename, const char *new_filename, const char *iv) {

    if (filename == NULL || new_filename == NULL || iv == NULL) {
        return -EINVAL;
    }

    if (key_bytes == NULL) {
        // Библиотека не проинициализирована
        return -ENOENT;
    }

    int res = 0;

    // Открываем файл для шифрования
    FILE *file = fopen(filename, "rb");
    if (file == NULL) {
        return errno;
    }

    // Открываем файл для записи
    FILE *new_file = fopen(new_filename, "wb");
    if (new_file == NULL) {
        return errno;
    }

    // Инициализируем блоки
    unsigned char block[AES_BLOCK_SIZE] = { 0 };
    unsigned char iv_block[AES_BLOCK_SIZE] = { 0 };

    // Дополняем или обрезаем iv до размера блока
    memcpy(iv_block, iv, strlen(iv) > AES_BLOCK_SIZE ? AES_BLOCK_SIZE : strlen(iv));

    // Инициализируем контекст
    struct AES_ctx context;
    AES_init_ctx_iv(&context, key_bytes, iv_block);

    // Шифруем файл
    int num = 0;
    unsigned long read = 0;
    do {

        // Обнуляем блоки
        memset(block, 0, AES_BLOCK_SIZE);

        // Читаем
        read = fread(block, 1, AES_BLOCK_SIZE, file);

        // Шифруем
        AES_CTR_xcrypt_buffer(&context, block, read);

        // Записываем
        fwrite(block, read, 1, new_file);

    } while (read == AES_BLOCK_SIZE);

    // Закрываем файлы
    fclose(new_file);
    fclose(file);

    return res;
}

int encryptor_decrypt_file(const char *filename, const char *new_filename, const char *iv) {

    if (filename == NULL || new_filename == NULL) {
        return -EINVAL;
    }

    if (key_bytes == NULL) {
        // Библиотека не проинициализирована
        return -ENOENT;
    }

    int res = 0;

    // Открываем файл для расшифрования
    FILE *file = fopen(filename, "rb");
    if (file == NULL) {
        return errno;
    }

    // Открываем файл для записи
    FILE *new_file = fopen(new_filename, "wb");
    if (new_file == NULL) {
        return errno;
    }

    // Инициализируем блоки
    unsigned char block[AES_BLOCK_SIZE] = { 0 };
    unsigned char iv_block[AES_BLOCK_SIZE] = { 0 };

    // Дополняем или обрезаем iv до размера блока
    memcpy(iv_block, iv, strlen(iv) > AES_BLOCK_SIZE ? AES_BLOCK_SIZE : strlen(iv));

    // Инициализируем контекст
    struct AES_ctx context;
    AES_init_ctx_iv(&context, key_bytes, iv_block);

    // Расшифровываем файл
    int num = 0;
    unsigned long read = 0;
    do {

        // Обнуляем блоки
        memset(block, 0, AES_BLOCK_SIZE);

        // Читаем
        read = fread(block, 1, AES_BLOCK_SIZE, file);

        // Расшифровываем
        AES_CTR_xcrypt_buffer(&context, block, read);

        // Записываем
        fwrite(block, read, 1, new_file);

    } while (read == AES_BLOCK_SIZE);

    // Закрываем файлы
    fclose(new_file);
    fclose(file);

    return res;
}