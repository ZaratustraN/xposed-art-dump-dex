//
// Created by RoseJames on 2017/12/6.
//



extern "C" {
#include "inline_hook/inlineHook.h"
}

#include "DexDumper.h"
#include <sys/system_properties.h>
#include <unistd.h>
#include <stdlib.h>
#include <android/log.h>
#include <dlfcn.h>
#include <fcntl.h>
#include <time.h>
#include <string>
#include <string.h>

#define LOG_TAG "DexDumper"

using namespace std;

static string mPkgName;

art::DexFile *(*old_openmemory)(const byte *base, size_t size, const std::string &location,
                                uint32_t location_checksum, art::MemMap *mem_map,
                                const art::OatDexFile *oat_dex_file, std::string *error_msg) = NULL;

art::DexFile *new_openmemory(const byte *base, size_t size, const std::string &location,
                             uint32_t location_checksum, art::MemMap *mem_map,
                             const art::OatDexFile *oat_dex_file, std::string *error_msg) {

    DexDumper::LOG("art::DexFile::OpenMemory is called");

    DexDumper::WriteToFile((uint8_t *) base, size);

    // 调用原art::DexFile::OpenMemory函数
    return (*old_openmemory)(base, size, location, location_checksum, mem_map, oat_dex_file,
                             error_msg);
}


bool DexDumper::StartDump(string pkgName) {
    if (IsArt()) {
        mPkgName = pkgName;
        void *handle = dlopen("libart.so", RTLD_GLOBAL | RTLD_LAZY);
        if (handle == NULL) {
            LOG("Error: unable to find libart.so");
            return false;
        }

        void *address = dlsym(handle,
                              "_ZN3art7DexFile10OpenMemoryEPKhjRKNSt3__112basic_stringIcNS3_11char_traitsIcEENS3_9allocatorIcEEEEjPNS_6MemMapEPKNS_10OatDexFileEPS9_");
        if (address == NULL) {
            LOG("Error: unable to find openMemory method");
            return false;
        }

        if (registerInlineHook((uint32_t) address, (uint32_t) new_openmemory,
                               (uint32_t **) &old_openmemory) != ELE7EN_OK) {
            LOG("ERROR: Register inline hook failed");
            return false;
        }

        if (inlineHook((uint32_t) address) != ELE7EN_OK) {
            LOG("Error: inline hook failed");
            return false;
        }

        LOG("Inline hook Success");
        return true;
    } else {
        return false;
    }

}


bool DexDumper::IsArt() {
    char version[10];

    __system_property_get("ro.build.version.sdk", version);

    int sdk = atoi(version);

    LOG("version:%d", sdk);
    if (sdk >= 21) {
        return true;
    } else {
        return false;
    }

}

void DexDumper::LOG(const char *fmt, ...) {
    va_list args;
    va_start(args, fmt);
    __android_log_vprint(ANDROID_LOG_INFO, LOG_TAG, fmt, args);
    va_end(args);
}

void DexDumper::WriteToFile(uint8_t *data, size_t size) {
    string pathName = DexDumper::createFileName(size);

    LOG("Dump dex file name is %s", pathName.c_str());
    LOG("Start dump");
    int dex = open(pathName.c_str(), O_CREAT | O_WRONLY, 0644);

    if (dex < 0) {
        LOG("Open or create file error");
        return;
    }

    int ret = write(dex, data, size);

    if (ret < 0) {
        LOG("Write file error");
    } else {
        LOG("Dump dex file success: %s", pathName.c_str());
    }

    close(dex);
}

string DexDumper::createFileName(size_t size) {
    char result[1024];
    time_t now;
    struct tm *timenow;
    time(&now);
    timenow = localtime(&now);

    memset(result, 0, 1024);
    sprintf(result, "/data/data/%s/dump_size_%u_time_%d_%d_%d_%d_%d_%d.dex", mPkgName.c_str(),
            size,
            timenow->tm_year + 1900,
            timenow->tm_mon + 1,
            timenow->tm_mday,
            timenow->tm_hour,
            timenow->tm_min,
            timenow->tm_sec);

    return string(result);
}
