package com.excellence.ffmpeg;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.excellence.exec.CommandTask;
import com.excellence.exec.Commander;
import com.excellence.exec.CommanderOptions;
import com.excellence.exec.IListener;
import com.excellence.ffmpeg.CpuChecker.CpuAbi;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 *     author : VeiZhang
 *     blog   : http://tiimor.cn
 *     time   : 2018/8/16
 *     desc   :
 * </pre> 
 */
public class FFprobe {

    private static final String TAG = FFprobe.class.getSimpleName();

    private static final String FFPROBE = "ffprobe";

    private static FFprobe mInstance = null;

    private Context mContext = null;

    public static void init(Context context) {
        init(context, null);
    }

    public static void init(Context context, CommanderOptions options) {
        init(context, true, options);
    }

    public static void init(Context context, boolean initialized, CommanderOptions options) {
        if (mInstance != null) {
            Log.i(TAG, "FFprobe initialized!!!");
            return;
        }
        if (options == null) {
            Commander.init();
        } else {
            Commander.init(options);
        }
        mInstance = new FFprobe(context.getApplicationContext());
        if (initialized) {
            mInstance.initFFprobe();
        }
    }

    private FFprobe(Context context) {
        mContext = context;
    }

    /**
     * 默认初始化，每次启动应用时，删除旧的ffprobe文件
     * @return
     */
    public void initFFprobe() {
        File ffprobeFile = new File(mInstance.mContext.getFilesDir(), FFPROBE);

        /**
         * ffprobe文件有更新，无法判断，因此每次都更新
         */
        if (ffprobeFile.exists()) {
            boolean success = ffprobeFile.delete();
            Log.d(TAG, "checkFFprobe: delete old ffprobe file success:" + success);
        }
        checkFFprobe();
    }

    /**
     * @see CommandTask.Builder#build()
     *
     * @param command
     * @param listener
     * @return
     */
    @Deprecated
    public static CommandTask addTask(@NonNull List<String> command, IListener listener) {
        String ffprobe = checkFFprobe();
        List<String> cmd = new ArrayList<>(command);
        if (!command.contains(ffprobe)) {
            cmd.add(0, ffprobe);
        }
        return Commander.addTask(cmd, listener);
    }

    /**
     * @see CommandTask.Builder#build()
     *
     * @param command
     * @param listener
     * @return
     */
    @Deprecated
    public static CommandTask addTask(@NonNull String[] command, IListener listener) {
        return addTask(Arrays.asList(command), listener);
    }

    /**
     * @see CommandTask.Builder#build()
     * 字符串命令，参数请以空格分隔
     *
     * @param command
     * @param listener
     * @return
     */
    @Deprecated
    public static CommandTask addTask(@NonNull String command, IListener listener) {
        String[] cmd = command.split(" ");
        return addTask(cmd, listener);
    }

    public static void destroy() {
        Commander.destroy();
    }

    /**
     * 初始化检测，返回FFprobe工具路径
     *
     * @return
     */
    public static String checkFFprobe() {
        if (mInstance == null) {
            throw new RuntimeException("FFprobe not initialized!!!");
        }

        try {
            File ffprobeFile = new File(mInstance.mContext.getFilesDir(), FFPROBE);
            if (!ffprobeFile.exists()) {
                String cpuArchNameFromAssets = "";
                CpuAbi cpuAbi = CpuChecker.getCpuAbi();

                Log.i(TAG, String.format("checkFFprobe: Loading FFprobe for %s CPU", cpuAbi.getCpuName()));

                if (cpuAbi == CpuAbi.NONE) {
                    throw new Exception("Device not supported");
                }
                cpuArchNameFromAssets = cpuAbi.getCpuName();

                cpuArchNameFromAssets = cpuArchNameFromAssets + File.separator + FFPROBE;
                boolean success = FileUtils.copyFileFromAssetsToData(mInstance.mContext, cpuArchNameFromAssets, FFPROBE);
                if (!success) {
                    Log.i(TAG, "checkFFprobe: default arm for CPU");
                    CpuAbi defaultCpuAbi = CpuAbi.ARMv7;

                    cpuArchNameFromAssets = defaultCpuAbi.getCpuName() + File.separator + FFPROBE;
                    success = FileUtils.copyFileFromAssetsToData(mInstance.mContext, cpuArchNameFromAssets, FFPROBE);
                    Log.i(TAG, "checkFFprobe: default arm is success : " + success);
                }

                Log.i(TAG, "FFprobe path: " + ffprobeFile.getPath());
            }
            ffprobeFile.setExecutable(true);
            return ffprobeFile.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
