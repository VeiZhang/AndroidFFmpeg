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
public class FFmpeg {

    private static final String TAG = FFmpeg.class.getSimpleName();

    private static final String FFMPEG = "ffmpeg";

    private static FFmpeg mInstance = null;

    private Context mContext = null;

    public static void init(Context context) {
        init(context, null);
    }

    public static void init(Context context, CommanderOptions options) {
        if (mInstance != null) {
            Log.i(TAG, "FFmpeg initialized!!!");
            return;
        }
        if (options == null) {
            Commander.init();
        } else {
            Commander.init(options);
        }
        mInstance = new FFmpeg(context.getApplicationContext());
    }

    private FFmpeg(Context context) {
        mContext = context;
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
        String ffmpeg = checkFFmpeg();
        List<String> cmd = new ArrayList<>(command);
        if (!command.contains(ffmpeg)) {
            cmd.add(0, ffmpeg);
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
     * 初始化检测，返回FFmpeg工具路径
     *
     * @return
     */
    public static String checkFFmpeg() {
        if (mInstance == null) {
            throw new RuntimeException("FFmpeg not initialized!!!");
        }

        try {
            File ffmpegFile = new File(mInstance.mContext.getFilesDir(), FFMPEG);
            if (!ffmpegFile.exists()) {
                String cpuArchNameFromAssets = "";
                CpuAbi cpuAbi = CpuChecker.getCpuAbi();

                Log.i(TAG, String.format("checkFFmpeg: Loading FFmpeg for %s CPU", cpuAbi.getCpuName()));

                if (cpuAbi == CpuAbi.NONE) {
                    throw new Exception("Device not supported");
                }
                cpuArchNameFromAssets = cpuAbi.getCpuName();

                cpuArchNameFromAssets = cpuArchNameFromAssets + File.separator + FFMPEG;
                boolean success = FileUtils.copyFileFromAssetsToData(mInstance.mContext, cpuArchNameFromAssets, FFMPEG);
                if (!success) {
                    Log.i(TAG, "checkFFmpeg: default arm for CPU");
                    CpuAbi defaultCpuAbi = CpuAbi.ARMv7;

                    cpuArchNameFromAssets = defaultCpuAbi.getCpuName() + File.separator + FFMPEG;
                    success = FileUtils.copyFileFromAssetsToData(mInstance.mContext, cpuArchNameFromAssets, FFMPEG);
                    Log.i(TAG, "checkFFmpeg: default arm is success : " + success);
                }
            }
            ffmpegFile.setExecutable(true);
            return ffmpegFile.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
