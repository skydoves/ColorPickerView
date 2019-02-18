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

package com.skydoves.colorpickerviewdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;
import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.flag.FlagView;

@SuppressLint("ViewConstructor")
public class CustomFlag extends FlagView {

  private TextView textView;
  private AlphaTileView alphaTileView;

  /**
   * onBind Views
   *
   * @param context context
   * @param layout custom flagView's layout
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
  @SuppressLint("SetTextI18n")
  @Override
  public void onRefresh(ColorEnvelope colorEnvelope) {
    textView.setText("#" + colorEnvelope.getHexCode());
    alphaTileView.setPaintColor(colorEnvelope.getColor());
  }
}
