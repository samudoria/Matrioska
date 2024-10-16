//
// Created by Simeone on 10/29/2021.
//

#include "utils/utils.h"
#include "Log.h"
#include <Substrate/CydiaSubstrate.h>
#include <fake_dlfcn.h>

extern "C" {

static inline bool isWordBreakAfter(uint16_t c) {
    if (c == ' ' || (0x2000 <= c && c <= 0x200A) || c == 0x3000) {
        // spaces
        return true;
    }
    // Break layout context before and after BiDi control character.
    if ((0x2066 <= c && c <= 0x2069) || (0x202A <= c && c <= 0x202E) || c == 0x200E ||
        c == 0x200F) {
        return true;
    }
    // Note: kana is not included, as sophisticated fonts may kern kana
    return false;
}

static inline bool isWordBreakBefore(uint16_t c) {
    // CJK ideographs (and yijing hexagram symbols)
    return isWordBreakAfter(c) || (0x3400 <= c && c <= 0x9FFF);
}

size_t getPrevWordBreakForCache(const uint16_t* chars, size_t offset, size_t len) {
    ALOGE("getPrevWordBreakForCache called");
    if (offset == 0) return 0;
    if (offset > len) offset = len;
    if (isWordBreakBefore(chars[offset - 1])) {
        return offset - 1;
    }
    for (size_t i = offset - 1; i > 0; i--) {
        if (isWordBreakBefore(chars[i]) || isWordBreakAfter(chars[i - 1])) {
            return i;
        }
    }
    return 0;
}

size_t getNextWordBreakForCache(const uint16_t* chars, size_t offset, size_t len) {
    ALOGE("getNextWordBreakForCache called");
    if (offset >= len) return len;
    if (isWordBreakAfter(chars[offset])) {
        return offset + 1;
    }
    for (size_t i = offset + 1; i < len; i++) {
        // No need to check isWordBreakAfter(chars[i - 1]) since it is checked
        // in previous iteration.  Note that isWordBreakBefore returns true
        // whenever isWordBreakAfter returns true.
        if (isWordBreakBefore(chars[i])) {
            return i;
        }
    }
    return len;
}

}

#include <Bypass/bypass_dlfcn.h>
#include <link.h>

#define B_ADDR_MASK 0x3FFFFFF

struct lib_info{
    const char *name;
    void *addr;
};

int iterate_cb(struct dl_phdr_info *info, size_t size, void *data) {
    struct lib_info* linfo = (struct lib_info*) data;
    ALOGI("lib name: %s", info->dlpi_name);
    if(strcmp(info->dlpi_name, linfo->name) == 0) {
        linfo->addr = (void *) info->dlpi_addr;
    }
    return 0;
}

void *find_lib(const char *name) {
    struct lib_info info;
    info.name = name;
    info.addr = NULL;
    dl_iterate_phdr(iterate_cb, &info);
    return info.addr;
}

void startMinikinHook() {
    //void *handle = dlopen("/system/lib64/libminikin.so", RTLD_NOW);
    //void *handle = find_lib("/system/lib64/libc.so");
    void *handle = bp_dlopen("/system/lib64/libminikin.so", RTLD_NOW);
    // void *handle = find_lib("/system/lib64/libminikin.so");
    find_lib("/system/lib64/libminikin.so");
    ALOGE("minikin handle: %x", handle);
    if(handle) {
        void *addr = dlsym(handle, "_ZN7minikin24getPrevWordBreakForCacheEPKtmm");
        if (addr == NULL) {
            ALOGE("Not found symbol : _ZN7minikin24getPrevWordBreakForCacheEPKtmm");
        } else {
            int instr =  *((int *)addr);
            addr = ((char *)addr) + (instr & B_ADDR_MASK);
            ALOGE("getPrevWordBreakForCache: %x [%x]", addr, instr);
            MSHookFunction(addr, (void *) getPrevWordBreakForCache, NULL);
        }
        addr = dlsym(handle, "_ZN7minikin24getNextWordBreakForCacheEPKtmm");
        if (addr == NULL) {
            ALOGE("Not found symbol : _ZN7minikin24getNextWordBreakForCacheEPKtmm");
        } else {
            int instr =  *((int *)addr);
            addr = ((char *)addr) + (instr & B_ADDR_MASK);
            ALOGE("getNextWordBreakForCache: %x [%x]", addr, instr);
            MSHookFunction(addr, (void *) getNextWordBreakForCache, NULL);
        }
    }
}

void startMinikinHookNoDlopen() {

    void *addr = dlsym(RTLD_DEFAULT, "_ZN7minikin24getPrevWordBreakForCacheEPKtmm");
    if (addr == NULL) {
        ALOGE("Not found symbol : _ZN7minikin24getPrevWordBreakForCacheEPKtmm");
    } else {
        ALOGE("getPrevWordBreakForCache: %x", addr);
        MSHookFunction(addr, (void *) getPrevWordBreakForCache, NULL);
    }
    addr = dlsym(RTLD_DEFAULT, "_ZN7minikin24getNextWordBreakForCacheEPKtmm");
    if (addr == NULL) {
        ALOGE("Not found symbol : _ZN7minikin24getNextWordBreakForCacheEPKtmm");
    } else {
        ALOGE("getNextWordBreakForCache: %x", addr);
        MSHookFunction(addr, (void *) getNextWordBreakForCache, NULL);
    }
}