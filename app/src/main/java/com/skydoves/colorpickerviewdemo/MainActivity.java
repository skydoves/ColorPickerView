package com.skydoves.colorpickerviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skydoves.colorpickerviewdemo.ColorPickerView.ColorPickerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ColorPickerView colorPickerView = (ColorPickerView)findViewById(R.id.colorPickerView);
        colorPickerView.setColorListener(new ColorPickerView.ColorListener() {
            @Override
            public void onColorSelected(int color) {
                TextView textView = (TextView)findViewById(R.id.textView);
                textView.setText("#" + colorPickerView.getColorHtml());

                LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
                linearLayout.setBackgroundColor(color);
            }
        });
    }
}
