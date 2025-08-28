package com.example.cardiohealth.Controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.NumberPicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardiohealth.Helper.Utils;
import com.example.cardiohealth.R;

public class SettingsActivity extends AppCompatActivity {

        NumberPicker np1, np2,np3,np4;
        EditText et;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);
            int ip1 = Utils.getIPNumber(this, Utils.IP1);
            np1 = (NumberPicker) findViewById(R.id.np1);
            np1.setMaxValue(255);
            np1.setMinValue(0);
            np1.setValue(ip1);

            int ip2 =  Utils.getIPNumber(this, Utils.IP2);
            np2 = (NumberPicker) findViewById(R.id.np2);
            np2.setMaxValue(255);
            np2.setMinValue(0);
            np2.setValue(ip2);

            int ip3 =  Utils.getIPNumber(this, Utils.IP3);
            np3 = (NumberPicker) findViewById(R.id.np3);
            np3.setMaxValue(255);
            np3.setMinValue(0);
            np3.setValue(ip3);

            int ip4 =  Utils.getIPNumber(this, Utils.IP4);
            np4 = (NumberPicker) findViewById(R.id.np4);
            np4.setMaxValue(255);
            np4.setMinValue(0);
            np4.setValue(ip4);

            int port =  Utils.getPortNumber(this);
            et = (EditText) findViewById(R.id.etNumPort);
            et.setText(Integer.toString(port));

            Button bt = (Button)findViewById(R.id.button);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int numberport = Integer.parseInt(et.getText().toString());
                    Utils.setWSAddress(SettingsActivity.this,np1.getValue(),np2.getValue(),np3.getValue(),np4.getValue(), numberport);
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            });
        }
    }
