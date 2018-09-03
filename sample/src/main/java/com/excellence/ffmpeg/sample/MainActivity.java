package com.excellence.ffmpeg.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.excellence.exec.CommandTask;
import com.excellence.exec.IListener;
import com.excellence.ffmpeg.FFmpeg;

public class MainActivity extends AppCompatActivity implements IListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private Button mButton = null;
    private TextView mTextView = null;
    private CommandTask mTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.button);
        mTextView = (TextView) findViewById(R.id.text);

        FFmpeg.init(this);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.setText("");
                if (mTask != null) {
                    mTask.discard();
                    mTask = null;
                    return;
                }
                String cmd = "-version";
                // mTask = FFmpeg.addTask(cmd, MainActivity.this);
                mTask = new CommandTask.Builder().command(FFmpeg.checkFFmpeg()).command(cmd).build();
                mTask.deploy(MainActivity.this);
            }
        });
    }

    @Override
    public void onPre(String command) {
        Log.i(TAG, "onPre: " + command);
        mTextView.append(command + "\n");
    }

    @Override
    public void onProgress(String message) {
        Log.i(TAG, "onProgress: " + message);
        mTextView.append(message + "\n");
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
        mTextView.setText("Error:" + t.getMessage());
    }

    @Override
    public void onSuccess(String message) {
        Log.i(TAG, "onSuccess: " + message);
        mTextView.append(message + "\n");
    }
}
