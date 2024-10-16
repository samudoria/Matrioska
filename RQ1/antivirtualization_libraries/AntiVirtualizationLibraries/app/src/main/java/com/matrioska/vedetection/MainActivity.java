package com.matrioska.vedetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.matrioska.vedetection.antiplugin.AntiPluginInfo;
import com.matrioska.vedetection.diprint.DiPrintInfo;
import com.matrioska.vedetection.middle.MiddleInfo;
import com.matrioska.vedetection.parspace.ParSpaceInfo;

public class MainActivity extends AppCompatActivity {

    private static final String RTAG ="VE-DETECTION" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Button diprintButton = (Button) findViewById(R.id.diprintbutton);
        diprintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(RTAG,"DiPrint is trying to detect a virtual environment");
                startActivity(new Intent(MainActivity.this, DiPrintInfo.class));
            }
        });

        Button antipluginButton = (Button) findViewById(R.id.antipluginbutton);
        antipluginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(RTAG,"Anti-Plugin is trying to detect a virtual environment");
                startActivity(new Intent(MainActivity.this, AntiPluginInfo.class));
            }
        });

        Button parallelButton = (Button) findViewById(R.id.parallelbutton);
        parallelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(RTAG,"Parallel Space Traveling is trying to detect a virtual environment");
                startActivity(new Intent(MainActivity.this, ParSpaceInfo.class));
            }
        });

        Button middleButton = (Button) findViewById(R.id.middlebutton);
        middleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(RTAG,"App in the Middle is trying to detect a virtual environment");
                startActivity(new Intent(MainActivity.this, MiddleInfo.class));
            }
        });
    }
}