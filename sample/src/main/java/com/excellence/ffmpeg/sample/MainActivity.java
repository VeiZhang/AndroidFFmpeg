package com.excellence.ffmpeg.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.excellence.exec.IListener;
import com.excellence.ffmpeg.FFmpeg;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private Button mButton = null;
    private TextView mTextView = null;

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
                String cmd = "-version";
                FFmpeg.addTask(cmd, new IListener() {
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
                });
            }
        });
    }
}
