package com.excellence.ffmpeg;

import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * <pre>
 *     author : VeiZhang
 *     blog   : http://tiimor.cn
 *     time   : 2018/8/17
 *     desc   :
 * </pre> 
 */
class CpuChecker {

    private static final String TAG = CpuChecker.class.getSimpleName();

    private static final String CPU_ARM = "armeabi";
    private static final String CPU_ARM_V7 = "armeabi-v7a";
    private static final String CPU_ARM64_V8 = "arm64-v8a";
    private static final String CPU_X86 = "x86";
    private static final String CPU_X86_64 = "x86_64";

    protected static CpuAbi getCpuAbi() {
        CpuAbi cpuAbi = CpuAbi.NONE;

        String CPU_ABI = android.os.Build.CPU_ABI;
        Log.i(TAG, "getCpuArch - Build.CPU_ABI: " + Build.CPU_ABI);

        switch (CPU_ABI) {
            case CPU_ARM:
                return CpuAbi.ARM;

            case CPU_ARM_V7:
            case CPU_ARM64_V8:
                return CpuAbi.ARMv7;

            case CPU_X86:
            case CPU_X86_64:
                return CpuAbi.x86;

            default:
                break;
        }

        try {
            Log.i(TAG, "getCpuAbi: read /proc/cpuinfo");
            FileReader fileReader = new FileReader("/proc/cpuinfo");
            BufferedReader reader = new BufferedReader(fileReader);
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("ARM")) {
                    Log.i(TAG, "/proc/cpuinfo contains \"ARM\"");
                    cpuAbi = CpuAbi.ARM;
                }
            }
            reader.close();
            fileReader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return cpuAbi;
    }

    protected enum CpuAbi {
        x86(CPU_X86),
        ARMv7(CPU_ARM_V7),
        ARM(CPU_ARM),
        NONE(null);

        private String mCpuName = null;

        CpuAbi(String cpuName) {
            mCpuName = cpuName;
        }

        public String getCpuName() {
            return mCpuName;
        }
    }
}
