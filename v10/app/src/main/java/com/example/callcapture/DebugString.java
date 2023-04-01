package com.example.callcapture;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * this class prepares a string to be sent out as a debug info.
 * Debug info will be displayed on the UI Textview widget as long as DEBUG = true
 * This is to make debugging easier and accessible at all times especially on our new case with
 * CTOUCH riva being unable to run apps from Android Studio. Unless we can access debug info on a
 * logcat via fileManager on the TV Screen this is what makes feel less uncomfortable and display
 * errors and messagess in real time.
 * This info will be displayed on a UserInterface thread meaning this class will not handle the
 * textView but will ask the UI-Thread to do it instead
 * The candidates being accepted are String[ ] and String
 *
 * --the example below refers to Gen2Backend but the output format is still the same--
 *
 * Say a thread with id = 72093 launches a DebugString to say what info were present in the message
 * got from the server.
 * Say MESSAGE: NODEDATA|\xa50a0007|f|t|\x00000050|active|0|||2022-09-15 14:51:43.074221+01]
 * Then the debug output will be something like this
 * [1665061640988]◖2 ◂ 72093◗ , readFromServer() got: : NODEDATA|\xa50a0007|f|t|\x00000050|active|0|||2022-09-15 14:51:43.074221+01]
 * with [1665061640988] being a system clock value indicating when did it happen.
 * while ◖2 ◂ 72093◗ reports on the left the thread who's writing to the console and on the right
 * the thread who requested the debugInfo to be written
 *
 * if you use this class from the UI-Thread to write let's say the latitude of a node
 * with id =\xa50a0003 what you'll get is the following:
 *
 * [1665061669428]◖2 ◂ 2◗ , \xa50a0003 latitude is 51.50761]
 */
public class DebugString
{
    final String TAG = this.getClass().toString();
    boolean DEBUG =true;
    ArrayList<String> dbg = new ArrayList<>();
    TextView textView;
    String operationTime;

    // this constructor allows to pass an array of information which can be of any size.
    // Depends on how many thing you want to take into account

    private void initialSetting(TextView t)
    {
        //This is roughly the time when DebugString class has been initiated
        operationTime="["+new Date().getTime()+"]";

        //I'm taking the textView to display output on
        textView=t;

        //this is the thread id of the current thread asking to write debug info
        dbg.add(" ◂ "+Thread.currentThread().getId()+"◗"+" ");
    }

    public void appendString(String s)
    {
        if (s == null)
            dbg.add("NULL");
        else
            dbg.add(s);
    }

    DebugString(String[] array, TextView t)
    {
            initialSetting(t);
            for (String s : array)
                appendString(s);
            this.debugInfo();
    }

    // This Constructor allows us to pass just a string as parameter instead of an array
    DebugString(String s, TextView t)
    {
            initialSetting(t);
            appendString(s);
            this.debugInfo();
    }

    public void debugInfo()
    {
        Handler handler = new Handler(Looper.getMainLooper());

        //this is the only place where you can actually use the textView.
        //the runnable we're passing to handler.post() will be executed on the main thread
        //which is the thread who better controls graphic objects and also created the textView in
        //the first place. Never allow another thread to modify a graphical object who was created
        //in another thread
        handler.post(() -> {
            if(DEBUG)
               textView.append(operationTime);
            Log.d(TAG, operationTime);
            if(DEBUG)
                textView.append("◖"+Thread.currentThread().getId());
            Log.d(TAG, "◖"+Thread.currentThread().getId());
            if(DEBUG)
                for (int i=0; i<dbg.size();i++)
                    textView.append(dbg.get(i));
            Log.d(TAG, dbg.toString());
            if(DEBUG)
                textView.append("\n");
            Log.d(TAG, "\n");
        });

    }

}
