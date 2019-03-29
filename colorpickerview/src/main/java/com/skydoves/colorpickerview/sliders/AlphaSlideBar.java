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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import com.skydoves.colorpickerview.R;
import com.skydoves.colorpickerview.preference.ColorPickerPreferenceManager;

/**
 * AlphaSlideBar extends {@link AbstractSlider} and more being specific to implement alpha slide.
 */
@SuppressWarnings("unused")
public class AlphaSlideBar extends AbstractSlider {

  private Bitmap backgroundBitmap;
  private AlphaTileDrawable drawable = new AlphaTileDrawable();

  public AlphaSlideBar(Context context) {
    super(context);
  }

  public AlphaSlideBar(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AlphaSlideBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public AlphaSlideBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  protected void getAttrs(AttributeSet attrs) {
    TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AlphaSlideBar);
    try {
      if (a.hasValue(R.styleable.AlphaSlideBar_selector_AlphaSlideBar))
        selectorDrawable = a.getDrawable(R.styleable.AlphaSlideBar_selector_AlphaSlideBar);
      if (a.hasValue(R.styleable.AlphaSlideBar_borderColor_AlphaSlideBar))
        borderColor = a.getColor(R.styleable.AlphaSlideBar_borderColor_AlphaSlideBar, borderColor);
      if (a.hasValue(R.styleable.AlphaSlideBar_borderSize_AlphaSlideBar))
        borderSize = a.getInt(R.styleable.AlphaSlideBar_borderSize_AlphaSlideBar, borderSize);
    } finally {
      a.recycle();
    }
  }

  @Override
  protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
    super.onSizeChanged(width, height, oldWidth, oldHeight);
    if (width > 0 && height > 0) {
      backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
      Canvas backgroundCanvas = new Canvas(backgroundBitmap);
      drawable.setBounds(0, 0, backgroundCanvas.getWidth(), backgroundCanvas.getHeight());
      drawable.draw(backgroundCanvas);
    }
  }

  @Override
  public void updatePaint(Paint colorPaint) {
    float[] hsv = new float[3];
    Color.colorToHSV(getColor(), hsv);
    int startColor = Color.HSVToColor(0, hsv);
    int endColor = Color.HSVToColor(255, hsv);
    Shader shader =
        new LinearGradient(
            0,
            0,
            getMeasuredWidth(),
            getMeasuredHeight(),
            startColor,
            endColor,
            Shader.TileMode.CLAMP);
    colorPaint.setShader(shader);
  }

  @Override
  public void onInflateFinished() {
    int defaultPosition = getMeasuredWidth() - selector.getMeasuredWidth();
    if (getPreferenceName() != null) {
      updateSelectorX(
          ColorPickerPreferenceManager.getInstance(getContext())
              .getAlphaSliderPosition(getPreferenceName(), defaultPosition));
    } else {
      selector.setX(defaultPosition);
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    canvas.drawBitmap(backgroundBitmap, 0, 0, null);
    super.onDraw(canvas);
  }

  @Override
  public int assembleColor() {
    float[] hsv = new float[3];
    Color.colorToHSV(getColor(), hsv);
    int alpha = (int) (selectorPosition * 255);
    return Color.HSVToColor(alpha, hsv);
  }
}
