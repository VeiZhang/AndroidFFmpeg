package com.excellence.ffmpeg;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.excellence.exec.Command.CommandTask;
import com.excellence.exec.Commander;
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
        if (mInstance != null) {
            Log.i(TAG, "FFmpeg initialized!!!");
            return;
        }
        Commander.init();
        mInstance = new FFmpeg(context.getApplicationContext());
    }

    private FFmpeg(Context context) {
        mContext = context;
    }

    public static CommandTask addTask(@NonNull List<String> command, IListener listener) {
        String ffmpeg = checkFFmpeg();
        List<String> cmd = new ArrayList<>(command);
        if (!command.contains(ffmpeg)) {
            cmd.add(0, ffmpeg);
        }
        return Commander.addTask(cmd, listener);
    }

    public static CommandTask addTask(@NonNull String[] command, IListener listener) {
        return addTask(Arrays.asList(command), listener);
    }

    /**
     * 字符串命令，参数请以空格分隔
     *
     * @param command
     * @param listener
     * @return
     */
    public static CommandTask addTask(@NonNull String command, IListener listener) {
        String[] cmd = command.split(" ");
        return addTask(cmd, listener);
    }

    public static void destory() {
        Commander.destory();
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
                switch (cpuAbi) {
                    case x86:
                        Log.i(TAG, "checkFFmpeg: Loading FFmpeg for x86 CPU");
                        cpuArchNameFromAssets = cpuAbi.getCpuName();
                        break;
                    case ARMv7:
                        Log.i(TAG, "checkFFmpeg: Loading FFmpeg for armv7 CPU");
                        cpuArchNameFromAssets = cpuAbi.getCpuName();
                        break;
                    case NONE:
                    default:
                        throw new Exception("Device not supported");
                }
                cpuArchNameFromAssets = cpuArchNameFromAssets + File.separator + FFMPEG;
                FileUtils.copyFileFromAssetsToData(mInstance.mContext, cpuArchNameFromAssets, FFMPEG);
            }
            ffmpegFile.setExecutable(true);
            return ffmpegFile.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
