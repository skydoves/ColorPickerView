package com.skydoves.colorpickerviewdemo;

import android.content.Context;
import android.widget.TextView;

import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.flag.FlagView;

/**
 * Developed by skydoves on 2018-02-11.
 * Copyright (c) 2018 skydoves rights reserved.
 */

public class CustomFlag extends FlagView {

    private TextView textView;
    private AlphaTileView alphaTileView;

    /**
     * onBind Views
     *
     * @param context context
     * @param layout  custom flagView's layout
     */
    public CustomFlag(Context context, int layout) {
        super(context, layout);
        textView = findViewById(R.id.flag_color_code);
        alphaTileView = findViewById(R.id.flag_color_layout);
    }

    /**
     * invoked when selector moved
     *
     * @param colorEnvelope provide color, hexCode, argb
     */
    @Override
    public void onRefresh(ColorEnvelope colorEnvelope) {
        textView.setText("#" + colorEnvelope.getHexCode());
        alphaTileView.setPaintColor(colorEnvelope.getColor());
    }
}
