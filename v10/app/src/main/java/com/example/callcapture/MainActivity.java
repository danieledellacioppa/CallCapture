package com.example.callcapture;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    TextView dbgConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbgConsole = findViewById(R.id.dbgConsole);
        dbgConsole.setText("");
        new DebugString("copilotino!", dbgConsole);

    }

}