package com.matrioska.vedetection.diprint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.matrioska.vedetection.R;

import java.util.List;

public class DiPrintInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_di_print_info);

        List<String> res = new DiPrint().detect(getBaseContext());

        addTextView("Detection Result: " + res.get(6), "\n\ncheck sospicious lib: "   + res.get(0) + "\ncheck host apk: " + res.get(1) + "\ncheck processes: " + res.get(2)
        + "\ncheck undeclared permissions: " + res.get(3) + "\ncheck APK directory: " + res.get(4) + "\ncheck stack trace: " + res.get(5));

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