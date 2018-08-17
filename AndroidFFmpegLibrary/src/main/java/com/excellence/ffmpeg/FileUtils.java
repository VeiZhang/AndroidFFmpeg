package com.excellence.ffmpeg;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * <pre>
 *     author : VeiZhang
 *     blog   : http://tiimor.cn
 *     time   : 2018/8/16
 *     desc   :
 * </pre> 
 */
public class FileUtils {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /**
     *
     * @param context
     * @param fileNameFromAssets
     * @param targetFileName /data/data/package name/files/targetFileName
     * @return
     */
    public static boolean copyFileFromAssetsToData(Context context, String fileNameFromAssets, String targetFileName) {
        // create files directory under /data/data/package name
        File filesDirectory = context.getFilesDir();

        InputStream is;
        try {
            is = context.getAssets().open(fileNameFromAssets);
            // copy ffmpeg file from assets to files dir
            final FileOutputStream os = new FileOutputStream(new File(filesDirectory, targetFileName));

            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int len;
            while (-1 != (len = is.read(buffer))) {
                os.write(buffer, 0, len);
            }

            os.close();
            is.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static StringBuilder getInputStream(InputStream inputStream) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader stdin = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = stdin.readLine()) != null) {
                System.out.println(line);
                result.append(line);
            }
            stdin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
