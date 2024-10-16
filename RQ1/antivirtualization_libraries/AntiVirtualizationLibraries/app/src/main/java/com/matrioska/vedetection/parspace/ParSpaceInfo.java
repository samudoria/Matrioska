package com.matrioska.vedetection.parspace;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.matrioska.vedetection.R;

import java.util.List;

public class ParSpaceInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_par_space_info);

        List<String> res = new ParSpace().detect(getBaseContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String generalResult;
            if(res.get(0).equals("real") && res.get(1).equals("real"))
                generalResult = "real";
            else
                generalResult = "virtualization";
            addTextView("Detection Result: " + generalResult, "\n\ncheck stack trace: " + res.get(0) + "\ncheck memory mapping file: " + res.get(1));
        }else {
            String generalResult;
            if(res.get(0).equals("real") && res.get(1).equals("real"))
                generalResult = "real";
            else
                generalResult = "virtualization";
            addTextView("Detection Result: " + generalResult, "\n\ncheck stack trace: " + res.get(0) + "\ncheck memory mapping file: " + res.get(1));
        }
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