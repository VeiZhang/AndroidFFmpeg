# FFmpeg for Android

[![Download][icon_download]][download]

* 完整引用
    ```
    // 完整引用，集成所有CPU架构的可执行文件
    implementation 'com.excellence:ffmpeg:_latestVersion'
    ```

* 部分引用
    ```
    implementation 'com.excellence:ffmpeg-java:_latestVersion'

    // 部分引用，使用想要的CPU架构的可执行文件
    implementation 'com.excellence:ffmpeg-arm64-v8a:_latestVersion'
    implementation 'com.excellence:ffmpeg-armv7a:_latestVersion'
    implementation 'com.excellence:ffmpeg-x86:_latestVersion'
    ```

基于[AndroidExec][AndroidExec]项目，FFmpeg命令执行

## AndroidFFmpeg使用

```
// 初始化，默认：不限制并发线程数；指令超时10s终止
FFmpeg.init(context);

// 自定义初始化参数：超时1s终止
FFmpeg.init(context, new CommanderOptions.Builder().setTimeOut(1000).build())

// 获取FFmpeg工具路径
FFmpeg.checkFFmpeg()

// 创建执行命令
推荐方式：new CommandTask.Builder().command(FFmpeg.checkFFmpeg()).command(cmd).build().deploy(IListener);

丢弃方式：FFmpeg.addTask(cmd, new IListener() {
    @Override
    public void onPre(String command) {
        Log.i(TAG, "onPre: " + command);
    }

    @Override
    public void onProgress(String message) {
        Log.i(TAG, "onProgress: " + message);
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @Override
    public void onSuccess(String message) {
        Log.i(TAG, "onSuccess: " + message);
    }
});

// 终止命令
CommandTask.discard()

// 终止所有命令
FFmpeg.destroy()
```

## FFmpeg命令

[FFMpeg官网][FFMpeg官网]

[Windows工具下载][Windows工具下载]，解压后，把FFmpeg加入到环境变量中，可在Windows上使用FFmpeg

### 命令选项

* 选项参数

    基本语法格式：

    ```
    ffmpeg [options] [[infile options] -i infile]... {[outfile options] outfile}...
    ```

* 常用选项

    ```
    -version:   显示版本信息。

    -formats:   显示所有有效的格式。

    -decoders:  显示所有有效解码器。

    -encoders:  显示所有有效的编码器。

    -bsfs:      显示有效的数据流滤镜。

    -pix_fmts:  显示有效的像素格式。
    ```

* 主要选项

    ```
    -f fmt(input|output):      指定输入或者输出文件格式，可依据扩展名自动指定。

    -i filename(input):        指定输入文件。

    -y (global):               默认自动覆盖输出文件。

    -n (global):               不覆盖输出文件，如果输出文件已存在则立即退出。

    -t duration(input|output): 限制输入／输出的时间。
                               如果用于输入选项，就是限定从输入中读取多少时间的数据；
                               如果用于输出选项，则表示写入多少时间数据后就停止。
                               duration 可以是以秒为单位的数值活着 hh:mm:ss[.xxx] 格式的时间值。
                               -to 和 -t 是互斥的，-t 有更高的优先级。

    -to time_stop(output):     写入 time_stop 时间后就停止。
                               time_stop 可以是以秒为单位的数值或者 hh:mm:ss[.xxx] 格式的时间值。

    -fs limit_size(output):    设置输出文件大小限制，单位是字节（bytes）。

    -ss time_off(input|output): 指定输入文件或输出文件的开始位置。
    ```

* 视频选项

    ```
    -vframes number(output): 设置输出文件的帧数。

    -r rate(input|output):   设置帧率（Hz 值）。

    -s size(input|output):   设置帧的尺寸。数据格式是 WxH，即宽度值x高度值。

    -aspect aspect(output):  指定视频的长宽显示比例。格式为浮点数字符串或者 num:den 格式字符串。
                             如"4:3"，"16:9"，"1.333"等。

    -vcodec codec(output):   设置视频编码器。
    ```

* 音频选项

    ```
    -aframes number(output): 设置输出文件的帧数。

    -ar rate(input|output): 设置音频的采样率，单位为 Hz。

    -aq quality(output): 设置音频品质（编码指定为 VBR）。

    -ac channels(input|output): 设置音频通道数。

    -af filtergraph(output): 对音频使用 filtergraph 滤镜效果。
    ```

### 常用命令

* 获取视频信息

    ```
    ffprobe -v quiet -print_format json -show_format -show_streams inputfile
    ```

* 视频截图

    ```
    ffmpeg -i input_file -y -f mjpeg -ss 1 -t 0.001 -s widthxheight output_file

    i: 源文件
    y: 覆盖输出文件
    f: 截图格式
    ss: 起始位置，单位秒
    t: 截图时间，单位秒
    s: 图片宽x高
    ```

* 每隔 1 秒截一张图

    ```
    ffmpeg -i input.mp4 -f image2 -vf fps=fps=1 out%d.jpg
    ```

* 每隔 20 秒截一张图

    ```
    ffmpeg -i input.mp4 -f image2 -vf fps=fps=1/20 out%d.jpg
    ```

* 将视频的前 30 帧转换成一个 Gif

    ```
    ffmpeg -i input.mp4 -vframes 30 -y -f gif output.gif
    ```

* 从视频中生成 Gif

    ```
    ffmpeg -i input.mp4 -t 10 -pix_fmt rgb24 output.gif
    ```

* 转换视频为图片（每帧一张图)

    ```
    ffmpeg -i input.mp4 out%d.jpg
    ```

* 图片转换为视频

    ```
    ffmpeg -f image2 -i out%d.jpg -r 25 video.mp4
    ```

* 提取视频的关键帧

    ```
    ffmpeg -i input.mp4 -vf select='eq(pict_type\,I)' -vsync 2 -s 160x90 -f image2 out-%02d.jpeg
    ```

* 分解视频音频流

    ```
    // 分离视频流
    ffmpeg -i input_file -vcodec copy -an output_file_video

    // 分离音频流
    ffmpeg -i input_file -vcodec copy -vn output_file_audio
    ```

* 视频转码

    ```
    // 转码为码流原始文件
    ffmpeg -i input.mp4 -vcodec h264 -an -f m4v test.264
    ```

* 视频封装

    ```
    ffmpeg -i video_file -i audio_file -vcodec copy -acodec copy output_file
    ```

* 视频录制

    ```
    // 录制视频流
    ffmpeg -i rtsp://hostname/stream -vcodec copy output.avi

    // 通过电脑摄像头录制
    ffmpeg -f avfoundation -framerate 30 -i "0" -f mpeg1video -b 500k -r 20 -vf scale=640:360 output.avi
    ```


## 版本更新

| 版本 | 描述 |
| --- | ---- |
| [1.2.3][FFmpeg1.2.3] | 增加CPU架构 **2021-5-30** |
| [1.2.2][FFmpeg1.2.2] | 修复任务偶尔不执行 **2019-8-15** |
| [1.2.1][FFmpeg1.2.1] | 设置armeabi为默认的CPU架构 **2019-5-29** |
| [1.2.0][FFmpeg1.2.0] | 新增armeabi架构，分离ffmpeg可执行文件 **2019-5-21** |
| [1.1.1][FFmpeg1.1.1] | 更新ffmpeg可执行文件，修改低版本机型中任务销毁时导致的阻塞 **2019-4-29** |
| [1.1.0][FFmpeg1.1.0] | 使用Builder模式创建命令任务，修复崩溃异常 **2018-9-3** |
| [1.0.0][FFmpeg1.0.0] | 集成FFmpeg命令行执行 **2018-8-17** |


## 感谢

> - [WritingMinds][ffmpeg-android-java], [WritingMinds][ffmpeg-android]
> - [hiliving][hiliving]
> - [c060604][c060604]


<!-- 网站链接 -->

[download]:https://bintray.com/veizhang/maven/ffmpeg/_latestVersion "Latest version"
[AndroidExec]:https://github.com/VeiZhang/AndroidExec
[FFMpeg官网]:http://ffmpeg.org/
[Windows工具下载]:https://ffmpeg.zeranoe.com/builds/
[ffmpeg-android-java]:https://github.com/WritingMinds/ffmpeg-android-java "FFmpeg在Android中示例"
[ffmpeg-android]:https://github.com/WritingMinds/ffmpeg-android "编译FFmpeg可执行文件"
[hiliving]:https://github.com/hiliving/VideoEdit "FFmpeg命令示例"
[c060604]:https://github.com/c060604/ffmpeg-usage "FFmpeg命令讲解"

<!-- 图片链接 -->

[icon_download]:https://api.bintray.com/packages/veizhang/maven/ffmpeg/images/download.svg

<!-- 版本 -->

[FFmpeg1.2.2]:https://bintray.com/veizhang/maven/ffmpeg/1.2.3
[FFmpeg1.2.2]:https://bintray.com/veizhang/maven/ffmpeg/1.2.2
[FFmpeg1.2.1]:https://bintray.com/veizhang/maven/ffmpeg/1.2.1
[FFmpeg1.2.0]:https://bintray.com/veizhang/maven/ffmpeg/1.2.0
[FFmpeg1.1.1]:https://bintray.com/veizhang/maven/ffmpeg/1.1.1
[FFmpeg1.1.0]:https://bintray.com/veizhang/maven/ffmpeg/1.1.0
[FFmpeg1.0.0]:https://bintray.com/veizhang/maven/ffmpeg/1.0.0
