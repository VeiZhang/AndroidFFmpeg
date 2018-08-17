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
public class CpuChecker {

    private static final String TAG = CpuChecker.class.getSimpleName();

    public static final String CPU_ARM_V7 = "armeabi-v7a";
    public static final String CPU_ARM64_V8A = "arm64-v8a";
    public static final String CPU_x86 = "x86";
    public static final String CPU_x86_64 = "x86_64";

    public static CpuAbi getCpuAbi() {
        CpuAbi cpuAbi = CpuAbi.NONE;

        String CPU_ABI = android.os.Build.CPU_ABI;
        Log.i(TAG, "getCpuArch - Build.CPU_ABI: " + Build.CPU_ABI);

        switch (CPU_ABI) {
            case CPU_ARM_V7:
            case CPU_ARM64_V8A:
                return CpuAbi.ARMv7;

            case CPU_x86:
            case CPU_x86_64:
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
                if (line.contains("ARMv7")) {
                    Log.i(TAG, "/proc/cpuinfo contains \"ARMv7\"");
                    cpuAbi = CpuAbi.ARMv7;
                }
            }
            reader.close();
            fileReader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return cpuAbi;
    }

    public enum CpuAbi {
        x86(CPU_x86),
        ARMv7(CPU_ARM_V7),
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
