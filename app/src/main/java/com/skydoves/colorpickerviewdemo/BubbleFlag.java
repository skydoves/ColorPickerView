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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.widget.ImageView;
import androidx.core.widget.ImageViewCompat;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.flag.FlagView;

@SuppressLint("ViewConstructor")
public class BubbleFlag extends FlagView {

  private ImageView bubble;

  /**
   * onBind Views
   *
   * @param context context
   * @param layout custom flagView's layout
   */
  public BubbleFlag(Context context, int layout) {
    super(context, layout);
    this.bubble = findViewById(R.id.imageView);
  }

  /**
   * invoked when selector moved
   *
   * @param colorEnvelope provide color, hexCode, argb
   */
  @Override
  public void onRefresh(ColorEnvelope colorEnvelope) {
    ImageViewCompat.setImageTintList(bubble, ColorStateList.valueOf(colorEnvelope.getColor()));
  }
}
