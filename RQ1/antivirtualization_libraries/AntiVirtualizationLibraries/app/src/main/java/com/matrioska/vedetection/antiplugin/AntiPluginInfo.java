package com.matrioska.vedetection.antiplugin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.matrioska.vedetection.R;

import java.util.List;

public class AntiPluginInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_plugin_info);

        startService(new Intent(this, TheService.class));

        List<String> res = new AntiPlugin().detect(getBaseContext());

        addTextView("Detection Result: ", "\n\ncheck undeclared permissions: "   + res.get(0) + "\nApplication info: " + res.get(1) + "\ncheck process info: " + res.get(2)
                + "\ncheck running services: " + res.get(3) + "\ncheck UID processes: " + res.get(4) + "\ncheck unregistered filter: please see the log for more information\ncheck enabled component: please see the log, the service should be stopped soon");


    }

    private void addTextView(String text1, String text2 ){
        LinearLayout rl = (LinearLayout) findViewById(R.id.rl);
        LinearLayout ll = new LinearLayout(this);

        ll.setOrientation(LinearLayout.VERTICAL);
        TextView tv1 = new TextView(this);
        tv1.setText(text1);
        ll.addView(tv1);
        TextView tv2 = new TextView(this);

        tv2.setText(text2);
        ll.addView(tv2);
        rl.addView(ll);
    }
}