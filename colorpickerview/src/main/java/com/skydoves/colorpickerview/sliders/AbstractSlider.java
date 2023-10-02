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

package com.skydoves.colorpickerview.sliders;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import com.skydoves.colorpickerview.ActionMode;
import com.skydoves.colorpickerview.ColorPickerView;

/** AbstractSlider is the abstract class for implementing sliders. */
@SuppressWarnings("unused")
abstract class AbstractSlider extends FrameLayout {

  public ColorPickerView colorPickerView;
  protected Paint colorPaint;
  protected Paint borderPaint;
  protected float selectorPosition = 1.0f;
  protected int selectedX = 0;
  protected Drawable selectorDrawable;
  protected int borderSize = 2;
  protected int borderColor = Color.BLACK;
  protected int color = Color.WHITE;
  protected ImageView selector;
  protected String preferenceName;

  public AbstractSlider(Context context) {
    super(context);
    onCreate();
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    selector.setVisibility(enabled ? VISIBLE : INVISIBLE);
    this.setClickable(enabled);
  }

  public AbstractSlider(Context context, AttributeSet attrs) {
    super(context, attrs);
    getAttrs(attrs);
    onCreate();
  }

  public AbstractSlider(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    getAttrs(attrs);
    onCreate();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public AbstractSlider(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    getAttrs(attrs);
    onCreate();
  }

  /** gets attribute sets style from layout */
  protected abstract void getAttrs(AttributeSet attrs);

  /** update paint color whenever the triggered colors are changed. */
  protected abstract void updatePaint(Paint colorPaint);

  /**
   * assembles about the selected color.
   *
   * @return assembled color.
   */
  public abstract @ColorInt int assembleColor();

  private void onCreate() {
    this.colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    this.borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    this.borderPaint.setStyle(Paint.Style.STROKE);
    this.borderPaint.setStrokeWidth(borderSize);
    this.borderPaint.setColor(borderColor);
    this.setBackgroundColor(Color.WHITE);

    selector = new ImageView(getContext());
    if (selectorDrawable != null) {
      setSelectorDrawable(selectorDrawable);
    }

    initializeSelector();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    float width = getWidth();
    float height = getMeasuredHeight();
    canvas.drawRect(0, 0, width, height, colorPaint);
    canvas.drawRect(0, 0, width, height, borderPaint);
  }

  /** called by {@link ColorPickerView} whenever {@link ColorPickerView} is triggered. */
  public void notifyColor() {
    color = colorPickerView.getPureColor();
    updatePaint(colorPaint);
    invalidate();
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (!this.isEnabled()) {
      return false;
    }

    if (colorPickerView != null) {
      switch (event.getActionMasked()) {
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_MOVE:
          selector.setPressed(true);
          if (event.getX() > getWidth() || event.getX() < 0) {
            return false;
          } else {
            onTouchReceived(event);
            return true;
          }
        default:
          selector.setPressed(false);
          return false;
      }
    } else {
      return false;
    }
  }

  private void onTouchReceived(MotionEvent event) {
    float eventX = event.getX();
    float left = selector.getWidth() / 2f;
    float right = getWidth() - left;
    if (eventX > right) eventX = right;
    selectorPosition = (eventX - left) / (right - left);
    if (selectorPosition < 0) selectorPosition = 0;
    if (selectorPosition > 1.0f) selectorPosition = 1.0f;
    Point snapPoint = new Point((int) event.getX(), (int) event.getY());
    selectedX = (int) getBoundaryX(snapPoint.x);
    selector.setX(selectedX);
    if (colorPickerView.getActionMode() == ActionMode.LAST) {
      if (event.getAction() == MotionEvent.ACTION_UP) {
        colorPickerView.fireColorListener(assembleColor(), true);
      }
    } else {
      colorPickerView.fireColorListener(assembleColor(), true);
    }

    if (colorPickerView.getFlagView() != null) {
      colorPickerView.getFlagView().receiveOnTouchEvent(event);
    }

    int maxPos = getWidth() - selector.getWidth();
    if (selector.getX() >= maxPos) selector.setX(maxPos);
    if (selector.getX() <= 0) selector.setX(0);
  }

  public void updateSelectorX(int x) {
    float left = selector.getWidth() / 2f;
    float right = getWidth() - left;
    selectorPosition = (x - left) / (right - left);
    if (selectorPosition < 0) selectorPosition = 0;
    if (selectorPosition > 1.0f) selectorPosition = 1.0f;
    selectedX = (int) getBoundaryX(x);
    selector.setX(selectedX);
    colorPickerView.fireColorListener(assembleColor(), false);
  }

  public void setSelectorPosition(@FloatRange(from = 0.0, to = 1.0) float selectorPosition) {
    this.selectorPosition = Math.min(selectorPosition, 1.0f);
    float x = (getWidth() * selectorPosition) - getSelectorSize() - getBorderHalfSize();
    selectedX = (int) getBoundaryX(x);
    selector.setX(selectedX);
  }

  public void setSelectorByHalfSelectorPosition(
      @FloatRange(from = 0.0, to = 1.0) float selectorPosition) {
    this.selectorPosition = Math.min(selectorPosition, 1.0f);
    float x = (getWidth() * selectorPosition) - (getSelectorSize() * 0.5f) - getBorderHalfSize();
    selectedX = (int) getBoundaryX(x);
    selector.setX(selectedX);
  }

  private float getBoundaryX(float x) {
    int maxPos = getWidth() - selector.getWidth() / 2;
    if (x >= maxPos) return maxPos;
    if (x <= getSelectorSize() / 2f) return 0;
    return x - getSelectorSize() / 2f;
  }

  protected int getSelectorSize() {
    return (int) (selector.getWidth());
  }

  protected int getBorderHalfSize() {
    return (int) (borderSize * 0.5f);
  }

  private void initializeSelector() {
    getViewTreeObserver()
        .addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
              @Override
              public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                onInflateFinished();
              }
            });
  }

  /**
   * sets a drawable of the selector.
   *
   * @param drawable drawable of the selector.
   */
  public void setSelectorDrawable(Drawable drawable) {
    removeView(selector);
    this.selectorDrawable = drawable;
    this.selector.setImageDrawable(drawable);
    FrameLayout.LayoutParams thumbParams =
        new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    thumbParams.gravity = Gravity.CENTER;
    addView(selector, thumbParams);
  }

  /**
   * sets a drawable resource of the selector.
   *
   * @param resource a drawable resource of the selector.
   */
  public void setSelectorDrawableRes(@DrawableRes int resource) {
    Drawable drawable = ResourcesCompat.getDrawable(getContext().getResources(), resource, null);
    setSelectorDrawable(drawable);
  }

  /**
   * sets a color of the slider border.
   *
   * @param color color of the slider border.
   */
  public void setBorderColor(@ColorInt int color) {
    this.borderColor = color;
    this.borderPaint.setColor(color);
    invalidate();
  }

  /**
   * sets a color resource of the slider border.
   *
   * @param resource color resource of the slider border.
   */
  public void setBorderColorRes(@ColorRes int resource) {
    int color = ContextCompat.getColor(getContext(), resource);
    setBorderColor(color);
  }

  /**
   * sets a size of the slide border.
   *
   * @param borderSize ize of the slide border.
   */
  public void setBorderSize(int borderSize) {
    this.borderSize = borderSize;
    this.borderPaint.setStrokeWidth(borderSize);
    invalidate();
  }

  /**
   * sets a size of the slide border using dimension resource.
   *
   * @param resource a size of the slide border.
   */
  public void setBorderSizeRes(@DimenRes int resource) {
    int borderSize = (int) getContext().getResources().getDimension(resource);
    setBorderSize(borderSize);
  }

  /** called when the inflating finished. */
  public abstract void onInflateFinished();

  /**
   * gets assembled color
   *
   * @return color
   */
  public int getColor() {
    return color;
  }

  /**
   * attaches {@link ColorPickerView} to slider.
   *
   * @param colorPickerView {@link ColorPickerView}.
   */
  public void attachColorPickerView(ColorPickerView colorPickerView) {
    this.colorPickerView = colorPickerView;
  }

  /**
   * gets selector's position ratio.
   *
   * @return selector's position ratio.
   */
  protected float getSelectorPosition() {
    return this.selectorPosition;
  }

  /**
   * gets selected x coordinate.
   *
   * @return selected x coordinate.
   */
  public int getSelectedX() {
    return this.selectedX;
  }

  /**
   * gets the preference name.
   *
   * @return preference name.
   */
  public String getPreferenceName() {
    return preferenceName;
  }

  /**
   * sets the preference name.
   *
   * @param preferenceName preference name.
   */
  public void setPreferenceName(String preferenceName) {
    this.preferenceName = preferenceName;
  }
}
