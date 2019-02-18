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

package com.skydoves.colorpickerview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import com.skydoves.colorpickerview.flag.FlagMode;
import com.skydoves.colorpickerview.flag.FlagView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.listeners.ColorListener;
import com.skydoves.colorpickerview.listeners.ColorPickerViewListener;
import com.skydoves.colorpickerview.preference.ColorPickerPreferenceManager;
import com.skydoves.colorpickerview.sliders.AlphaSlideBar;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

/**
 * ColorPickerView implements getting HSV colors, ARGB values, Hex color codes from any image
 * drawables.
 *
 * <p>{@link ColorPickerViewListener} will be invoked whenever ColorPickerView is triggered by
 * {@link ActionMode} rules.
 *
 * <p>Implements {@link FlagView}, {@link AlphaSlideBar} and {@link BrightnessSlideBar} optional.
 */
@SuppressWarnings({"WeakerAccess", "unchecked", "unused", "IntegerDivisionInFloatingPointContext"})
public class ColorPickerView extends FrameLayout implements LifecycleObserver {

  private int selectedPureColor;
  private int selectedColor;
  private Point selectedPoint;
  private ImageView palette;
  private ImageView selector;
  private FlagView flagView;
  private Drawable paletteDrawable;
  private Drawable selectorDrawable;
  private AlphaSlideBar alphaSlideBar;
  private BrightnessSlideBar brightnessSlider;
  public ColorPickerViewListener colorListener;

  private ActionMode actionMode = ActionMode.ALWAYS;

  private float alpha_selector = 1.0f;
  private float alpha_flag = 1.0f;
  private boolean VISIBLE_FLAG = false;

  private String preferenceName;

  public ColorPickerView(Context context) {
    super(context);
  }

  public ColorPickerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    getAttrs(attrs);
    onCreate();
  }

  public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    getAttrs(attrs);
    onCreate();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    getAttrs(attrs);
    onCreate();
  }

  private void getAttrs(AttributeSet attrs) {
    TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ColorPickerView);
    try {
      if (a.hasValue(R.styleable.ColorPickerView_palette))
        this.paletteDrawable = a.getDrawable(R.styleable.ColorPickerView_palette);
      if (a.hasValue(R.styleable.ColorPickerView_selector))
        this.selectorDrawable = a.getDrawable(R.styleable.ColorPickerView_selector);
      if (a.hasValue(R.styleable.ColorPickerView_alpha_selector))
        this.alpha_selector =
            a.getFloat(R.styleable.ColorPickerView_alpha_selector, alpha_selector);
      if (a.hasValue(R.styleable.ColorPickerView_alpha_flag))
        this.alpha_flag = a.getFloat(R.styleable.ColorPickerView_alpha_flag, alpha_flag);
      if (a.hasValue(R.styleable.ColorPickerView_actionMode)) {
        int actionMode = a.getInteger(R.styleable.ColorPickerView_actionMode, 0);
        if (actionMode == 0) this.actionMode = ActionMode.ALWAYS;
        else if (actionMode == 1) this.actionMode = ActionMode.LAST;
      }
      if (a.hasValue(R.styleable.ColorPickerView_preferenceName)) {
        this.preferenceName = a.getString(R.styleable.ColorPickerView_preferenceName);
      }
    } finally {
      a.recycle();
    }
  }

  private void onCreate() {
    setPadding(0, 0, 0, 0);
    palette = new ImageView(getContext());
    if (paletteDrawable != null) {
      palette.setImageDrawable(paletteDrawable);
    } else {
      palette.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.palette));
    }

    FrameLayout.LayoutParams paletteParam =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    paletteParam.gravity = Gravity.CENTER;
    addView(palette, paletteParam);

    selector = new ImageView(getContext());
    if (selectorDrawable != null) {
      selector.setImageDrawable(selectorDrawable);
    } else {
      selector.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.wheel));
    }

    FrameLayout.LayoutParams selectorParam =
        new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    selectorParam.gravity = Gravity.CENTER;
    addView(selector, selectorParam);
    selector.setAlpha(alpha_selector);

    getViewTreeObserver()
        .addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
              @Override
              public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                  getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                  getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                onFinishInflated();
              }
            });
  }

  private void onFinishInflated() {
    if (getPreferenceName() != null) {
      ColorPickerPreferenceManager.getInstance(getContext()).restoreColorPickerData(this);
    } else {
      selectCenter();
    }
  }

  /**
   * initialize the {@link ColorPickerView} by {@link ColorPickerView.Builder}.
   *
   * @param builder {@link ColorPickerView.Builder}.
   */
  protected void onCreateByBuilder(Builder builder) {
    FrameLayout.LayoutParams params =
        new FrameLayout.LayoutParams(
            SizeUtils.dp2Px(getContext(), builder.width),
            SizeUtils.dp2Px(getContext(), builder.height));
    setLayoutParams(params);

    this.paletteDrawable = builder.paletteDrawable;
    this.selectorDrawable = builder.selectorDrawable;
    this.alpha_selector = builder.alpha_selector;
    this.alpha_flag = builder.alpha_flag;
    onCreate();

    if (builder.colorPickerViewListener != null) setColorListener(builder.colorPickerViewListener);
    if (builder.alphaSlideBar != null) attachAlphaSlider(builder.alphaSlideBar);
    if (builder.brightnessSlider != null) attachBrightnessSlider(builder.brightnessSlider);
    if (builder.actionMode != null) this.actionMode = builder.actionMode;
    if (builder.flagView != null) setFlagView(builder.flagView);
    if (builder.preferenceName != null) setPreferenceName(builder.preferenceName);
    if (builder.lifecycleOwner != null) setLifecycleOwner(builder.lifecycleOwner);
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getActionMasked()) {
      case MotionEvent.ACTION_DOWN:
        if (flagView != null && flagView.getFlagMode() == FlagMode.LAST) flagView.gone();
        selector.setPressed(true);
        return onTouchReceived(event);
      case MotionEvent.ACTION_MOVE:
        if (flagView != null && flagView.getFlagMode() == FlagMode.LAST) flagView.gone();
        selector.setPressed(true);
        return onTouchReceived(event);
      case MotionEvent.ACTION_UP:
        if (flagView != null && flagView.getFlagMode() == FlagMode.LAST) flagView.visible();
        selector.setPressed(true);
        return onTouchReceived(event);
      default:
        selector.setPressed(false);
        return false;
    }
  }

  /**
   * notify to the other views by the onTouchEvent.
   *
   * @param event {@link MotionEvent}.
   * @return notified or not.
   */
  private boolean onTouchReceived(MotionEvent event) {
    Point snapPoint = new Point((int) event.getX(), (int) event.getY());
    int pixelColor = getColorFromBitmap(snapPoint.x, snapPoint.y);

    if (pixelColor != Color.TRANSPARENT && pixelColor != Color.BLACK) {
      selectedPureColor = pixelColor;
      selectedColor = pixelColor;
      selectedPoint = new Point(snapPoint.x, snapPoint.y);
      setCoordinate(snapPoint.x, snapPoint.y);
      notifyToFlagView(selectedPoint);

      if (actionMode == ActionMode.LAST) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
          fireColorListener(getColor(), true);
          notifyToSlideBars();
        }
      } else {
        fireColorListener(getColor(), true);
        notifyToSlideBars();
      }
      return true;
    } else return false;
  }

  /**
   * gets a pixel color on the specific coordinate from the bitmap.
   *
   * @param x coordinate x.
   * @param y coordinate y.
   * @return selected color.
   */
  private int getColorFromBitmap(float x, float y) {
    Matrix invertMatrix = new Matrix();
    palette.getImageMatrix().invert(invertMatrix);

    float[] mappedPoints = new float[] {x, y};
    invertMatrix.mapPoints(mappedPoints);

    if (palette.getDrawable() != null
        && palette.getDrawable() instanceof BitmapDrawable
        && mappedPoints[0] > 0
        && mappedPoints[1] > 0
        && mappedPoints[0] < palette.getDrawable().getIntrinsicWidth()
        && mappedPoints[1] < palette.getDrawable().getIntrinsicHeight()) {

      invalidate();

      Rect rect = palette.getDrawable().getBounds();
      float scaleX = mappedPoints[0] / rect.height();
      int x1 = (int) (scaleX * ((BitmapDrawable) palette.getDrawable()).getBitmap().getHeight());
      float scaleY = mappedPoints[1] / rect.width();
      int y1 = (int) (scaleY * ((BitmapDrawable) palette.getDrawable()).getBitmap().getWidth());
      return ((BitmapDrawable) palette.getDrawable()).getBitmap().getPixel(x1, y1);
    }
    return 0;
  }

  /**
   * sets a {@link ColorPickerViewListener} on the {@link ColorPickerView}.
   *
   * @param colorListener {@link ColorListener} or {@link ColorEnvelopeListener}.
   */
  public void setColorListener(ColorPickerViewListener colorListener) {
    this.colorListener = colorListener;
  }

  /**
   * invokes {@link ColorListener} or {@link ColorEnvelopeListener} with a color value.
   *
   * @param color color.
   * @param fromUser triggered by user or not.
   */
  public void fireColorListener(int color, boolean fromUser) {
    if (colorListener != null) {
      selectedColor = color;
      if (getAlphaSlideBar() != null) {
        getAlphaSlideBar().notifyColor();
        selectedColor = getAlphaSlideBar().assembleColor();
      }
      if (getBrightnessSlider() != null) {
        getBrightnessSlider().notifyColor();
        selectedColor = getBrightnessSlider().assembleColor();
      }
      if (colorListener instanceof ColorListener) {
        ((ColorListener) colorListener).onColorSelected(selectedColor, fromUser);
      } else if (colorListener instanceof ColorEnvelopeListener) {
        ColorEnvelope envelope = new ColorEnvelope(selectedColor);
        ((ColorEnvelopeListener) colorListener).onColorSelected(envelope, fromUser);
      }

      if (flagView != null) flagView.onRefresh(getColorEnvelope());

      if (VISIBLE_FLAG) {
        VISIBLE_FLAG = false;
        if (selector != null) {
          selector.setAlpha(alpha_selector);
        }
        if (flagView != null) {
          flagView.setAlpha(alpha_flag);
        }
      }
    }
  }

  /** notify to sliders about a new trigger. */
  private void notifyToSlideBars() {
    if (alphaSlideBar != null) alphaSlideBar.notifyColor();
    if (brightnessSlider != null) {
      brightnessSlider.notifyColor();

      if (brightnessSlider.assembleColor() != Color.WHITE)
        selectedColor = brightnessSlider.assembleColor();
      else if (alphaSlideBar != null) selectedColor = alphaSlideBar.assembleColor();
    }
  }

  /**
   * notify to {@link FlagView} about a new trigger.
   *
   * @param point a new coordinate {@link Point}.
   */
  private void notifyToFlagView(Point point) {
    Point centerPoint = getCenterPoint(point.x, point.y);
    if (flagView != null) {
      if (flagView.getFlagMode() == FlagMode.ALWAYS) flagView.visible();
      int posX = centerPoint.x - flagView.getWidth() / 2 + selector.getWidth() / 2;
      if (centerPoint.y - flagView.getHeight() > 0) {
        flagView.setRotation(0);
        flagView.setX(posX);
        flagView.setY(centerPoint.y - flagView.getHeight());
        flagView.onRefresh(getColorEnvelope());
      } else if (flagView.isFlipAble()) {
        flagView.setRotation(180);
        flagView.setX(posX);
        flagView.setY(centerPoint.y + flagView.getHeight() - selector.getHeight() / 2);
        flagView.onRefresh(getColorEnvelope());
      }
      if (posX < 0) flagView.setX(0);
      if (posX + flagView.getMeasuredWidth() > getMeasuredWidth())
        flagView.setX(getMeasuredWidth() - flagView.getMeasuredWidth());
    }
  }

  /**
   * gets the selected color.
   *
   * @return the selected color.
   */
  public int getColor() {
    return selectedColor;
  }

  /**
   * gets the selected pure color without alpha and brightness.
   *
   * @return the selected pure color.
   */
  public int getPureColor() {
    return selectedPureColor;
  }

  /**
   * sets the pure color.
   *
   * @param color the pure color.
   */
  public void setPureColor(int color) {
    this.selectedPureColor = color;
  }

  /**
   * gets the {@link ColorEnvelope} of the selected color.
   *
   * @return {@link ColorEnvelope}.
   */
  public ColorEnvelope getColorEnvelope() {
    return new ColorEnvelope(getColor());
  }

  /**
   * gets a {@link FlagView}.
   *
   * @return {@link FlagView}.
   */
  public FlagView getFlagView() {
    return this.flagView;
  }

  /**
   * sets a {@link FlagView}.
   *
   * @param flagView {@link FlagView}.
   */
  public void setFlagView(@NonNull FlagView flagView) {
    flagView.gone();
    addView(flagView);
    this.flagView = flagView;
    flagView.setAlpha(alpha_flag);
  }

  /**
   * gets center coordinate of the selector.
   *
   * @param x coordinate x.
   * @param y coordinate y.
   * @return the center coordinate of the selector.
   */
  private Point getCenterPoint(int x, int y) {
    return new Point(x - (selector.getMeasuredWidth() / 2), y - (selector.getMeasuredHeight() / 2));
  }

  /**
   * gets a selector's selected coordinate x.
   *
   * @return a selected coordinate x.
   */
  public float getSelectorX() {
    return selector.getX() - (selector.getMeasuredWidth() / 2);
  }

  /**
   * gets a selector's selected coordinate y.
   *
   * @return a selected coordinate y.
   */
  public float getSelectorY() {
    return selector.getY() - (selector.getMeasuredHeight() / 2);
  }

  /**
   * gets a selector's selected coordinate.
   *
   * @return a selected coordinate {@link Point}.
   */
  public Point getSelectedPoint() {
    return selectedPoint;
  }

  /**
   * changes selector's selected point with notifies about changes manually.
   *
   * @param x coordinate x of the selector.
   * @param y coordinate y of the selector.
   */
  public void setSelectorPoint(int x, int y) {
    selectedColor = getColorFromBitmap(x, y);
    if (selectedColor != Color.TRANSPARENT) {
      selectedPoint = new Point(x, y);
      setCoordinate(x, y);
      fireColorListener(getColor(), false);
      notifyToSlideBars();
      notifyToFlagView(new Point(x, y));
    }
  }

  /**
   * changes selector's selected point without notifies.
   *
   * @param x coordinate x of the selector.
   * @param y coordinate y of the selector.
   */
  public void setCoordinate(int x, int y) {
    selector.setX(x - (selector.getMeasuredWidth() / 2));
    selector.setY(y - (selector.getMeasuredHeight() / 2));
  }

  /**
   * changes selector's selected point by a specific color.
   *
   * <p>It may not work properly if change the default palette drawable.
   *
   * @param color color.
   */
  public void selectByHsv(int color) {
    int radius = getMeasuredWidth() / 2;

    float[] hsv = new float[3];
    Color.colorToHSV(color, hsv);

    double x = hsv[1] * Math.cos(Math.toRadians(hsv[0]));
    double y = hsv[1] * Math.sin(Math.toRadians(hsv[0]));

    int pointX = (int) ((x + 1) * radius);
    int pointY = (int) ((1 - y) * radius);
    setSelectorPoint(pointX, pointY);
  }

  /**
   * changes palette drawable manually.
   *
   * @param drawable palette drawable.
   */
  public void setPaletteDrawable(@NonNull Drawable drawable) {
    removeView(palette);
    palette = new ImageView(getContext());
    paletteDrawable = drawable;
    palette.setImageDrawable(paletteDrawable);
    addView(palette);

    removeView(selector);
    addView(selector);

    if (flagView != null) {
      removeView(flagView);
      addView(flagView);
    }

    if (!VISIBLE_FLAG) {
      VISIBLE_FLAG = true;
      if (selector != null) {
        alpha_selector = selector.getAlpha();
        selector.setAlpha(0.0f);
      }
      if (flagView != null) {
        alpha_flag = flagView.getAlpha();
        flagView.setAlpha(0.0f);
      }
    }
  }

  /**
   * changes selector drawable manually.
   *
   * @param drawable selector drawable.
   */
  public void setSelectorDrawable(@NonNull Drawable drawable) {
    selector.setImageDrawable(drawable);
  }

  /** selects the center of the palette manually. */
  public void selectCenter() {
    setSelectorPoint(getMeasuredWidth() / 2, getMeasuredHeight() / 2);
  }

  /**
   * gets an {@link ActionMode}.
   *
   * @return {@link ActionMode}.
   */
  public ActionMode getActionMode() {
    return this.actionMode;
  }

  /**
   * sets an {@link ActionMode}.
   *
   * @param actionMode {@link ActionMode}.
   */
  public void setActionMode(ActionMode actionMode) {
    this.actionMode = actionMode;
  }

  /**
   * gets an {@link AlphaSlideBar}.
   *
   * @return {@link AlphaSlideBar}.
   */
  public AlphaSlideBar getAlphaSlideBar() {
    return alphaSlideBar;
  }

  /**
   * linking an {@link AlphaSlideBar} on the {@link ColorPickerView}.
   *
   * @param alphaSlideBar {@link AlphaSlideBar}.
   */
  public void attachAlphaSlider(@NonNull AlphaSlideBar alphaSlideBar) {
    this.alphaSlideBar = alphaSlideBar;
    alphaSlideBar.attachColorPickerView(this);
    alphaSlideBar.notifyColor();

    if (getPreferenceName() != null) {
      alphaSlideBar.setPreferenceName(getPreferenceName());
    }
  }

  /**
   * gets an {@link BrightnessSlideBar}.
   *
   * @return {@link BrightnessSlideBar}.
   */
  public BrightnessSlideBar getBrightnessSlider() {
    return brightnessSlider;
  }

  /**
   * linking an {@link BrightnessSlideBar} on the {@link ColorPickerView}.
   *
   * @param brightnessSlider {@link BrightnessSlideBar}.
   */
  public void attachBrightnessSlider(@NonNull BrightnessSlideBar brightnessSlider) {
    this.brightnessSlider = brightnessSlider;
    brightnessSlider.attachColorPickerView(this);
    brightnessSlider.notifyColor();

    if (getPreferenceName() != null) {
      brightnessSlider.setPreferenceName(getPreferenceName());
    }
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
    if (this.alphaSlideBar != null) {
      this.alphaSlideBar.setPreferenceName(preferenceName);
    }
    if (this.brightnessSlider != null) {
      this.brightnessSlider.setPreferenceName(preferenceName);
    }
  }

  /**
   * sets the {@link LifecycleOwner}.
   *
   * @param lifecycleOwner {@link LifecycleOwner}.
   */
  public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
    lifecycleOwner.getLifecycle().addObserver(this);
  }

  /**
   * removes this color picker observer from the the {@link LifecycleOwner}.
   *
   * @param lifecycleOwner {@link LifecycleOwner}.
   */
  public void removeLifecycleOwner(LifecycleOwner lifecycleOwner) {
    lifecycleOwner.getLifecycle().removeObserver(this);
  }

  /**
   * This method invoked by the {@link LifecycleOwner}'s life cycle.
   *
   * <p>OnDestroy would be called on the {@link LifecycleOwner}, all of the color picker data will
   * be saved automatically.
   */
  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  public void onDestroy() {
    ColorPickerPreferenceManager.getInstance(getContext()).saveColorPickerData(this);
  }

  /** Builder class for create {@link ColorPickerView}. */
  public static class Builder {
    private Context context;
    private ColorPickerViewListener colorPickerViewListener;
    private FlagView flagView;
    private Drawable paletteDrawable;
    private Drawable selectorDrawable;
    private AlphaSlideBar alphaSlideBar;
    private BrightnessSlideBar brightnessSlider;
    private ActionMode actionMode = ActionMode.ALWAYS;
    private float alpha_selector = 1.0f;
    private float alpha_flag = 1.0f;
    private int width = LayoutParams.MATCH_PARENT;
    private int height = LayoutParams.MATCH_PARENT;
    private String preferenceName;
    private LifecycleOwner lifecycleOwner;

    public Builder(Context context) {
      this.context = context;
    }

    public Builder setColorListener(ColorPickerViewListener colorPickerViewListener) {
      this.colorPickerViewListener = colorPickerViewListener;
      return this;
    }

    public Builder setPaletteDrawable(@NonNull Drawable palette) {
      this.paletteDrawable = palette;
      return this;
    }

    public Builder setSelectorDrawable(@NonNull Drawable selector) {
      this.selectorDrawable = selector;
      return this;
    }

    public Builder setFlagView(@NonNull FlagView flagView) {
      this.flagView = flagView;
      return this;
    }

    public Builder setAlphaSlideBar(AlphaSlideBar alphaSlideBar) {
      this.alphaSlideBar = alphaSlideBar;
      return this;
    }

    public Builder setBrightnessSlideBar(BrightnessSlideBar brightnessSlideBar) {
      this.brightnessSlider = brightnessSlideBar;
      return this;
    }

    public Builder setActionMode(ActionMode actionMode) {
      this.actionMode = actionMode;
      return this;
    }

    public Builder setSelectorAlpha(float alpha) {
      this.alpha_selector = alpha;
      return this;
    }

    public Builder setFlagAlpha(float alpha) {
      this.alpha_flag = alpha;
      return this;
    }

    public Builder setWidth(int width) {
      this.width = width;
      return this;
    }

    public Builder setHeight(int height) {
      this.height = height;
      return this;
    }

    public Builder setPreferenceName(String preferenceName) {
      this.preferenceName = preferenceName;
      return this;
    }

    public Builder setLifecycleOwner(LifecycleOwner lifecycleOwner) {
      this.lifecycleOwner = lifecycleOwner;
      return this;
    }

    public ColorPickerView build() {
      ColorPickerView colorPickerView = new ColorPickerView(context);
      colorPickerView.onCreateByBuilder(this);
      return colorPickerView;
    }
  }
}
