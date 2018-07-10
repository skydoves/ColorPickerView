package com.skydoves.colorpickerviewdemo;

/*
 * Copyright (C) 2017 skydoves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skydoves.colorpickerview.MultiColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorListener;

public class ExampleMultiColorPickerView extends AppCompatActivity {

    private MultiColorPickerView multiColorPickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_color_picker_view_example);

        multiColorPickerView = findViewById(R.id.multiColorPickerView);

        multiColorPickerView.setSelectorAlpha(0.6f);
        multiColorPickerView.addSelector(ContextCompat.getDrawable(this, R.drawable.wheel), selector0_colorListener);
        multiColorPickerView.addSelector(ContextCompat.getDrawable(this, R.drawable.wheel), selector1_colorListener);
        multiColorPickerView.addSelector(ContextCompat.getDrawable(this, R.drawable.wheel), selector2_colorListener);
        multiColorPickerView.addSelector(ContextCompat.getDrawable(this, R.drawable.wheel), selector3_colorListener);
    }

    private ColorListener selector0_colorListener = new ColorListener() {
        @Override
        public void onColorSelected(int color, boolean fromUser) {
            TextView textView = findViewById(R.id.textView0);
            textView.setText("#" + multiColorPickerView.getColorHtml());

            LinearLayout linearLayout = findViewById(R.id.linearLayout0);
            linearLayout.setBackgroundColor(color);
        }
    };

    private ColorListener selector1_colorListener = new ColorListener() {
        @Override
        public void onColorSelected(int color, boolean fromUser) {
            TextView textView = findViewById(R.id.textView1);
            textView.setText("#" + multiColorPickerView.getColorHtml());

            LinearLayout linearLayout = findViewById(R.id.linearLayout1);
            linearLayout.setBackgroundColor(color);
        }
    };

    private ColorListener selector2_colorListener = new ColorListener() {
        @Override
        public void onColorSelected(int color, boolean fromUser) {
            TextView textView = findViewById(R.id.textView2);
            textView.setText("#" + multiColorPickerView.getColorHtml());

            LinearLayout linearLayout = findViewById(R.id.linearLayout2);
            linearLayout.setBackgroundColor(color);
        }
    };

    private ColorListener selector3_colorListener = new ColorListener() {
        @Override
        public void onColorSelected(int color, boolean fromUser) {
            TextView textView = findViewById(R.id.textView3);
            textView.setText("#" + multiColorPickerView.getColorHtml());

            LinearLayout linearLayout = findViewById(R.id.linearLayout3);
            linearLayout.setBackgroundColor(color);
        }
    };
}
