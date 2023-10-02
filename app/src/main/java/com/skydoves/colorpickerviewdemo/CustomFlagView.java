/*
 * Designed and developed by 2017 skydoves (Jaewoong Eum)
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

import android.content.Context;
import android.widget.TextView;

import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.flag.FlagView;

public class CustomFlagView extends FlagView {

  private TextView textView;
  private AlphaTileView alphaTileView;

  public CustomFlagView(Context context, int layout) {
    super(context, layout);
    textView = findViewById(R.id.flag_color_code);
    alphaTileView = findViewById(R.id.flag_color_layout);
  }

  @Override
  public void onRefresh(ColorEnvelope colorEnvelope) {
    textView.setText("#" + colorEnvelope.getHexCode());
    alphaTileView.setPaintColor(colorEnvelope.getColor());
  }

  @Override
  public void onFlipped(Boolean isFlipped) {
    if (isFlipped) {
      textView.setRotation(180f);
    } else {
      textView.setRotation(0f);
    }
  }
}
