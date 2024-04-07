#ifdef __cplusplus
extern "C" {
#endif

#ifndef CPP_ENCRYPTOR_H
#define CPP_ENCRYPTOR_H

int encryptor_init();
void encryptor_free();
int encryptor_encrypt_file(const char *filename, const char *new_filename, const char *iv);
int encryptor_decrypt_file(const char *filename, const char *new_filename, const char *iv);

#endif //CPP_ENCRYPTOR_H

#ifdef __cplusplus
}
#endif