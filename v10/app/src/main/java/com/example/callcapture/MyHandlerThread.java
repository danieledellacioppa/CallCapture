package com.example.callcapture;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.TextView;

/**
 * Worker thread in separate file controlling its execute method with its own handler
 * All you have to do is create a new instance and use execute method to make it do whichever task
 * you want to be done
 *
 * This is a NON-UI Thread. Don't use it to manage graphics!
  */


public class MyHandlerThread extends HandlerThread
{
    private final Handler handler;
    private static final String TAG = "Worker";

    public MyHandlerThread()
    {
        super(TAG);
        start();
        handler = new Handler(getLooper());
    }

    public MyHandlerThread execute(Runnable task)
    {
        Log.d(TAG, "worker executing a task\n");
        handler.post(task);
        return this;
    }

    public MyHandlerThread execute(Runnable task, TextView t)
    {
        new DebugString("worker executing task with DbgMode active",t);
        handler.post(task);
        return this;
    }
}