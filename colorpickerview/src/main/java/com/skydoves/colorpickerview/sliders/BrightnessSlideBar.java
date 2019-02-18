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

package com.skydoves.colorpickerview.sliders;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import com.skydoves.colorpickerview.R;
import com.skydoves.colorpickerview.preference.ColorPickerPreferenceManager;

/**
 * BrightnessSlideBar extends {@link AbstractSlider} and more being specific to implement brightness
 * slide.
 */
@SuppressWarnings("unused")
public class BrightnessSlideBar extends AbstractSlider {

  public BrightnessSlideBar(Context context) {
    super(context);
  }

  public BrightnessSlideBar(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public BrightnessSlideBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public BrightnessSlideBar(
      Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  protected void getAttrs(AttributeSet attrs) {
    TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BrightnessSlideBar);
    try {
      if (a.hasValue(R.styleable.BrightnessSlideBar_selector_BrightnessSlider))
        selectorDrawable = a.getDrawable(R.styleable.BrightnessSlideBar_selector_BrightnessSlider);
      if (a.hasValue(R.styleable.BrightnessSlideBar_borderColor_BrightnessSlider))
        borderColor =
            a.getColor(R.styleable.BrightnessSlideBar_borderColor_BrightnessSlider, borderColor);
      if (a.hasValue(R.styleable.BrightnessSlideBar_borderSize_BrightnessSlider))
        borderSize =
            a.getInt(R.styleable.BrightnessSlideBar_borderSize_BrightnessSlider, borderSize);
    } finally {
      a.recycle();
    }
  }

  @Override
  protected void updatePaint(Paint colorPaint) {
    float[] hsv = new float[3];
    Color.colorToHSV(getColor(), hsv);
    hsv[2] = 0;
    int startColor = Color.HSVToColor(hsv);
    hsv[2] = 1;
    int endColor = Color.HSVToColor(hsv);
    Shader shader =
        new LinearGradient(
            0, 0, getWidth(), getHeight(), startColor, endColor, Shader.TileMode.CLAMP);
    colorPaint.setShader(shader);
  }

  @Override
  public void onInflateFinished() {
    int defaultPosition = getMeasuredWidth() - selector.getMeasuredWidth();
    if (getPreferenceName() != null) {
      updateSelectorX(
          ColorPickerPreferenceManager.getInstance(getContext())
              .getBrightnessSliderPosition(getPreferenceName(), defaultPosition));
    } else {
      selector.setX(defaultPosition);
    }
  }

  @Override
  public int assembleColor() {
    float[] hsv = new float[3];
    Color.colorToHSV(getColor(), hsv);
    hsv[2] = selectorPosition;
    if (colorPickerView != null && colorPickerView.getAlphaSlideBar() != null) {
      int alpha = (int) (colorPickerView.getAlphaSlideBar().getSelectorPosition() * 255);
      return Color.HSVToColor(alpha, hsv);
    }
    return Color.HSVToColor(hsv);
  }
}
