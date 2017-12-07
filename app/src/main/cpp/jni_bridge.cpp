#include <jni.h>
#include <string>
#include <string.h>
#include "dex_dump/DexDumper.h"

using namespace std;

extern "C"
JNIEXPORT void JNICALL
Java_cn_zaratustra_dumpdex_core_DumpDexNative_dumpDexNative(JNIEnv *env, jclass type,
                                                            jstring pkgName) {
    const char *c_str = env->GetStringUTFChars(pkgName, JNI_FALSE);
    string result = "";
    if (c_str != NULL) {
        result = string(c_str, strlen(c_str));
    }

    DexDumper *dexDumper = new DexDumper();
    dexDumper->StartDump(result);

    env->DeleteLocalRef(pkgName);
    delete dexDumper;
}
