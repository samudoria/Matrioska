//
// Created by zhangsong on 17-11-14.
//

#include <stdio.h>
#include <time.h>
#include <safekey/safekey_jni.h>

#include <stdlib.h>

#include "BypassFile.h"
#include "originalInterface.h"
//#include "encryptInfoMgr.h"
#include "utils/mylog.h"
using namespace xdja;

/********************************************************************/

char * keyGenerator::generate(size_t keylen) {
   return NULL;
}

/********************************************************************/
/********************************************************************/
bool EncryptFile::isEncryptFile(int fd) {
    return true;
}

EncryptFile::EncryptFile(const char * path) {
    this->path = new char[strlen(path) + 1];
    memset(this->path, 0, strlen(path) + 1);

    strcpy(this->path, path);
}

EncryptFile::EncryptFile(EncryptFile &ef) {
    this->path = new char[strlen(ef.path) + 1];
    memset(this->path, 0, strlen(ef.path) + 1);
    strcpy(path, ef.path);
}

EncryptFile::~EncryptFile() {

    if(path) {
        delete []path;
        path = 0;
    }
}

/*

#include <string>
void dump(char * addr, int len)
{
    std::string output = "";
    char tmp[10] = {0};
    for(int i = 0; i < len; i++)
    {
        memset(tmp, 0, 10);
        sprintf(tmp, "%02hhX ", addr[i]);
        output += tmp;

        if((i + 1) % 10 == 0) {
            output += "\n";
            log("%s", (char *)output.c_str());
            output = "";
        }
    }
}
*/

int EncryptFile::getHeaderLen() {
    return 0;
}

bool EncryptFile::writeHeader(int fd) {
    return true;
}

bool EncryptFile::readHeader(int fd) {
    return true;
}

bool EncryptFile::create(int fd, ef_mode mode) {

    lseek(fd, 0, SEEK_SET);     /*将文件游标设置为0 ———— 透明游标*/

    return true;
}

ssize_t EncryptFile::read(int fd, char *buf, size_t len) {
    //log("EncryptFile::read [fd %d] [len %zu]", fd, len);

    return originalInterface::original_read(fd, buf, len);
}

ssize_t EncryptFile::write(int fd, char *buf, size_t len) {
    //log("EncryptFile::write [fd %d] [len %zu]", fd, len);

    return originalInterface::original_write(fd, buf, len);
}

ssize_t EncryptFile::pread64(int fd, void *buf, size_t len, off64_t offset) {
    //log("EncryptFile::pread64 [fd %d] [len %zu] [offset %lld]", fd, len, offset);

    return originalInterface::original_pread64(fd, buf, len, offset);
}

ssize_t EncryptFile::pwrite64(int fd, void *buf, size_t len, off64_t offset) {
    //log("EncryptFile::pwrite64 [fd %d] [len %zu] [offset %lld]", fd, len, offset);

    return originalInterface::original_pwrite64(fd, buf, len, offset);
}

off_t EncryptFile::lseek(int fd, off_t pos, int whence) {
    //log("EncryptFile::lseek [fd %d] [offset %d] [whence %d]", fd, pos, whence);

    return originalInterface::original_lseek(fd, pos, whence);
}

int EncryptFile::llseek(int fd, unsigned long offset_high, unsigned long offset_low, loff_t *result,
                       unsigned int whence) {

    off64_t rel_offset = 0; rel_offset |= offset_high; rel_offset <<= 32; rel_offset |= offset_low;
    //log("EncryptFile::llseek [fd %d] [offset %lld] [whence %d]", fd, rel_offset, whence);

    return originalInterface::original_llseek(fd, offset_high, offset_low, result, whence);
}

int EncryptFile::fstat(int fd, struct stat *buf) {
    //log("EncryptFile::fstat [fd %d]", fd);

    return originalInterface::original_fstat(fd, buf);
}

int EncryptFile::ftruncate(int fd, off_t length) {
    //log("EncryptFile::ftruncate [fd %d] [length %ld", fd, length);

    return originalInterface::original_ftruncate(fd, length);
}

int EncryptFile::ftruncate64(int fd, off64_t length) {
    //log("EncryptFile::ftruncate64 [fd %d] [length %lld", fd, length);

    return originalInterface::original_ftruncate64(fd, length);
}
