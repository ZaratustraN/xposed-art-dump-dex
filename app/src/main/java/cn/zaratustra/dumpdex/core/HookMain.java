package cn.zaratustra.dumpdex.core;

import android.content.Context;
import android.os.Process;

import java.lang.reflect.Method;

import cn.zaratustra.dumpdex.utils.FileUtils;
import dalvik.system.PathClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by zaratustra on 2017/12/5.
 */

public class HookMain implements IXposedHookLoadPackage {

    public static String packageName = "cn.zaratustra.dumpdex";
    public static String[] paths = new String[]{
            String.format("/data/app/%s-%s.apk", packageName, 1),
            String.format("/data/app/%s-%s.apk", packageName, 2),
            String.format("/data/app/%s-%s/base.apk", packageName, 1),
            String.format("/data/app/%s-%s/base.apk", packageName, 2)
    };


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        String filePath = "";
        for (int i = 0; i < paths.length; i++) {
            filePath = paths[i];
            if (FileUtils.isExitsAndNotDir(filePath)) {
                break;
            }
        }

        try {
            PathClassLoader pathClassLoader = new PathClassLoader(filePath, ClassLoader.getSystemClassLoader());
            Class aClass = Class.forName(HookMain.class.getCanonicalName(), true, pathClassLoader);
            Method aClassMethod = aClass.getMethod("hook", XC_LoadPackage.LoadPackageParam.class);
            aClassMethod.setAccessible(true);
            aClassMethod.invoke(aClass.newInstance(), loadPackageParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hook(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (AppInfoCenter.INSTANCE.getSelectedApp().contains(loadPackageParam.packageName)) {
            XposedBridge.log("选择了APP，开始拦截:" + loadPackageParam.packageName);
            DumpDexNative.loadSO();
            XposedHelpers.findAndHookMethod("android.app.Application", loadPackageParam.classLoader, "attach",
                    Context.class, new XC_MethodHook() {

                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);

                            XposedBridge.log(String.format("[%d]Hook %s Application attach method",
                                    Process.myPid(), loadPackageParam.packageName));

                            DumpDexNative.dumpDexNative(loadPackageParam.packageName);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                        }
                    });
        } else {
            XposedBridge.log("未在列表中找到该APP:" + loadPackageParam.packageName);
        }
    }
}

