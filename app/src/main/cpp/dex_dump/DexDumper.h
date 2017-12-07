//
// Created by RoseJames on 2017/12/6.
//

#ifndef XPOSED_ART_DUMP_DEX_DEXDUMPER_H
#define XPOSED_ART_DUMP_DEX_DEXDUMPER_H

#include <string>

using namespace std;

class DexDumper {
public:
    bool StartDump(string pkgName);

    static void LOG(const char *fmt, ...);

    static void WriteToFile(uint8_t *string, size_t size);

private:
    bool IsArt();

    static string createFileName(size_t size);
};


typedef uint8_t byte;

namespace art {

    class OatFile;

    class DexFile;

    class OatDexFile;

    class MemMap;
}


#endif //XPOSED_ART_DUMP_DEX_DEXDUMPER_H
