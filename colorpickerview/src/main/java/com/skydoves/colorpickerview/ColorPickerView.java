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

package com.skydoves.colorpickerview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.FloatRange;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.content.res.AppCompatResources;
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
@SuppressWarnings("unused")
public class ColorPickerView extends FrameLayout implements LifecycleObserver {

  @ColorInt private int selectedPureColor;
  @ColorInt private int selectedColor;
  private Point selectedPoint;
  private ImageView palette;
  private ImageView selector;
  private FlagView flagView;
  private Drawable paletteDrawable;
  private Drawable selectorDrawable;
  private AlphaSlideBar alphaSlideBar;
  private BrightnessSlideBar brightnessSlider;
  public ColorPickerViewListener colorListener;
  private long debounceDuration = 0;
  private final Handler debounceHandler = new Handler();

  private ActionMode actionMode = ActionMode.ALWAYS;

  @FloatRange(from = 0.0, to = 1.0)
  private float selector_alpha = 1.0f;

  @FloatRange(from = 0.0, to = 1.0)
  private float flag_alpha = 1.0f;

  private boolean flag_isFlipAble = true;

  @Px private int selectorSize = 0;

  private boolean VISIBLE_FLAG = false;

  private String preferenceName;
  private final ColorPickerPreferenceManager preferenceManager =
      ColorPickerPreferenceManager.getInstance(getContext());

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
      if (a.hasValue(R.styleable.ColorPickerView_palette)) {
        this.paletteDrawable = a.getDrawable(R.styleable.ColorPickerView_palette);
      }
      if (a.hasValue(R.styleable.ColorPickerView_selector)) {
        int resourceId = a.getResourceId(R.styleable.ColorPickerView_selector, -1);
        if (resourceId != -1) {
          this.selectorDrawable = AppCompatResources.getDrawable(getContext(), resourceId);
        }
      }
      if (a.hasValue(R.styleable.ColorPickerView_selector_alpha)) {
        this.selector_alpha =
            a.getFloat(R.styleable.ColorPickerView_selector_alpha, selector_alpha);
      }
      if (a.hasValue(R.styleable.ColorPickerView_selector_size)) {
        this.selectorSize =
            a.getDimensionPixelSize(R.styleable.ColorPickerView_selector_size, selectorSize);
      }
      if (a.hasValue(R.styleable.ColorPickerView_flag_alpha)) {
        this.flag_alpha = a.getFloat(R.styleable.ColorPickerView_flag_alpha, flag_alpha);
      }
      if (a.hasValue(R.styleable.ColorPickerView_flag_isFlipAble)) {
        this.flag_isFlipAble =
            a.getBoolean(R.styleable.ColorPickerView_flag_isFlipAble, flag_isFlipAble);
      }
      if (a.hasValue(R.styleable.ColorPickerView_actionMode)) {
        int actionMode = a.getInteger(R.styleable.ColorPickerView_actionMode, 0);
        if (actionMode == 0) {
          this.actionMode = ActionMode.ALWAYS;
        } else if (actionMode == 1) this.actionMode = ActionMode.LAST;
      }
      if (a.hasValue(R.styleable.ColorPickerView_debounceDuration)) {
        this.debounceDuration =
            a.getInteger(R.styleable.ColorPickerView_debounceDuration, (int) debounceDuration);
      }
      if (a.hasValue(R.styleable.ColorPickerView_preferenceName)) {
        this.preferenceName = a.getString(R.styleable.ColorPickerView_preferenceName);
      }
      if (a.hasValue(R.styleable.ColorPickerView_initialColor)) {
        setInitialColor(a.getColor(R.styleable.ColorPickerView_initialColor, Color.WHITE));
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
    if (selectorSize != 0) {
      selectorParam.width = SizeUtils.dp2Px(getContext(), selectorSize);
      selectorParam.height = SizeUtils.dp2Px(getContext(), selectorSize);
    }
    selectorParam.gravity = Gravity.CENTER;
    addView(selector, selectorParam);
    selector.setAlpha(selector_alpha);

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

  @Override
  protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
    super.onSizeChanged(width, height, oldWidth, oldHeight);

    if (palette.getDrawable() == null) {
      Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
      palette.setImageDrawable(new ColorHsvPalette(getResources(), bitmap));
    }
  }

  private void onFinishInflated() {
    if (getParent() != null && getParent() instanceof ViewGroup) {
      ((ViewGroup) getParent()).setClipChildren(false);
    }

    if (getPreferenceName() != null) {
      preferenceManager.restoreColorPickerData(this);
      final int persisted = preferenceManager.getColor(getPreferenceName(), -1);
      if (palette.getDrawable() instanceof ColorHsvPalette && persisted != -1) {
        post(
            () -> {
              try {
                selectByHsvColor(persisted);
              } catch (IllegalAccessException e) {
                e.printStackTrace();
              }
            });
      }
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
    this.selector_alpha = builder.selector_alpha;
    this.flag_alpha = builder.flag_alpha;
    this.selectorSize = builder.selectorSize;
    this.debounceDuration = builder.debounceDuration;
    onCreate();

    if (builder.colorPickerViewListener != null) setColorListener(builder.colorPickerViewListener);
    if (builder.alphaSlideBar != null) attachAlphaSlider(builder.alphaSlideBar);
    if (builder.brightnessSlider != null) attachBrightnessSlider(builder.brightnessSlider);
    if (builder.actionMode != null) this.actionMode = builder.actionMode;
    if (builder.flagView != null) setFlagView(builder.flagView);
    if (builder.preferenceName != null) setPreferenceName(builder.preferenceName);
    if (builder.initialColor != 0) setInitialColor(builder.initialColor);
    if (builder.lifecycleOwner != null) setLifecycleOwner(builder.lifecycleOwner);
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (!this.isEnabled()) {
      return false;
    }
    switch (event.getActionMasked()) {
      case MotionEvent.ACTION_DOWN:
      case MotionEvent.ACTION_MOVE:
      case MotionEvent.ACTION_UP:
        if (getFlagView() != null) getFlagView().receiveOnTouchEvent(event);
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
  @MainThread
  private boolean onTouchReceived(final MotionEvent event) {
    Point snapPoint =
        PointMapper.getColorPoint(this, new Point((int) event.getX(), (int) event.getY()));
    int pixelColor = getColorFromBitmap(snapPoint.x, snapPoint.y);

    this.selectedPureColor = pixelColor;
    this.selectedColor = pixelColor;
    this.selectedPoint = PointMapper.getColorPoint(this, new Point(snapPoint.x, snapPoint.y));
    setCoordinate(snapPoint.x, snapPoint.y);

    if (actionMode == ActionMode.LAST) {
      notifyToFlagView(this.selectedPoint);
      if (event.getAction() == MotionEvent.ACTION_UP) {
        notifyColorChanged();
      }
    } else {
      notifyColorChanged();
    }
    return true;
  }

  public boolean isHuePalette() {
    return palette.getDrawable() != null && palette.getDrawable() instanceof ColorHsvPalette;
  }

  /**
   * notifies color changes to {@link ColorListener}, {@link FlagView}. {@link AlphaSlideBar},
   * {@link BrightnessSlideBar} with the debounce duration.
   */
  private void notifyColorChanged() {
    this.debounceHandler.removeCallbacksAndMessages(null);
    Runnable debounceRunnable =
        () -> {
          fireColorListener(getColor(), true);
          notifyToFlagView(selectedPoint);
        };
    this.debounceHandler.postDelayed(debounceRunnable, this.debounceDuration);
  }

  /**
   * gets a pixel color on the specific coordinate from the bitmap.
   *
   * @param x coordinate x.
   * @param y coordinate y.
   * @return selected color.
   */
  protected int getColorFromBitmap(float x, float y) {
    Matrix invertMatrix = new Matrix();
    palette.getImageMatrix().invert(invertMatrix);

    float[] mappedPoints = new float[] {x, y};
    invertMatrix.mapPoints(mappedPoints);

    if (palette.getDrawable() != null
        && palette.getDrawable() instanceof BitmapDrawable
        && mappedPoints[0] >= 0
        && mappedPoints[1] >= 0
        && mappedPoints[0] < palette.getDrawable().getIntrinsicWidth()
        && mappedPoints[1] < palette.getDrawable().getIntrinsicHeight()) {

      invalidate();

      if (palette.getDrawable() instanceof ColorHsvPalette) {
        x = x - getWidth() * 0.5f;
        y = y - getHeight() * 0.5f;
        double r = Math.sqrt(x * x + y * y);
        float radius = Math.min(getWidth(), getHeight()) * 0.5f;
        float[] hsv = {0, 0, 1};
        hsv[0] = (float) (Math.atan2(y, -x) / Math.PI * 180f) + 180;
        hsv[1] = Math.max(0f, Math.min(1f, (float) (r / radius)));
        return Color.HSVToColor(hsv);
      } else {
        Rect rect = palette.getDrawable().getBounds();
        float scaleX = mappedPoints[0] / rect.width();
        int x1 = (int) (scaleX * ((BitmapDrawable) palette.getDrawable()).getBitmap().getWidth());
        float scaleY = mappedPoints[1] / rect.height();
        int y1 = (int) (scaleY * ((BitmapDrawable) palette.getDrawable()).getBitmap().getHeight());
        return ((BitmapDrawable) palette.getDrawable()).getBitmap().getPixel(x1, y1);
      }
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
  public void fireColorListener(@ColorInt int color, final boolean fromUser) {
    if (this.colorListener != null) {
      this.selectedColor = color;
      if (getAlphaSlideBar() != null) {
        getAlphaSlideBar().notifyColor();
        this.selectedColor = getAlphaSlideBar().assembleColor();
      }
      if (getBrightnessSlider() != null) {
        getBrightnessSlider().notifyColor();
        this.selectedColor = getBrightnessSlider().assembleColor();
      }

      if (colorListener instanceof ColorListener) {
        ((ColorListener) colorListener).onColorSelected(selectedColor, fromUser);
      } else if (colorListener instanceof ColorEnvelopeListener) {
        ColorEnvelope envelope = new ColorEnvelope(selectedColor);
        ((ColorEnvelopeListener) colorListener).onColorSelected(envelope, fromUser);
      }

      if (this.flagView != null) {
        this.flagView.onRefresh(getColorEnvelope());
        invalidate();
      }

      if (VISIBLE_FLAG) {
        VISIBLE_FLAG = false;
        if (this.selector != null) {
          this.selector.setAlpha(selector_alpha);
        }
        if (this.flagView != null) {
          this.flagView.setAlpha(flag_alpha);
        }
      }
    }
  }

  /** notify to sliders about a new trigger. */
  private void notifyToSlideBars() {
    if (alphaSlideBar != null) alphaSlideBar.notifyColor();
    if (brightnessSlider != null) {
      brightnessSlider.notifyColor();

      if (brightnessSlider.assembleColor() != Color.WHITE) {
        selectedColor = brightnessSlider.assembleColor();
      } else if (alphaSlideBar != null) selectedColor = alphaSlideBar.assembleColor();
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
      if (flagView.isFlipAble()) {
        if (centerPoint.y - flagView.getHeight() > 0) {
          flagView.setRotation(0);
          flagView.setX(posX);
          flagView.setY(centerPoint.y - flagView.getHeight());
        } else {
          flagView.setRotation(180);
          flagView.setX(posX);
          flagView.setY(centerPoint.y + flagView.getHeight() - selector.getHeight() * 0.5f);
        }
      } else {
        flagView.setRotation(0);
        flagView.setX(posX);
        flagView.setY(centerPoint.y - flagView.getHeight());
      }
      flagView.onRefresh(getColorEnvelope());
      if (posX < 0) flagView.setX(0);
      if (posX + flagView.getMeasuredWidth() > getMeasuredWidth()) {
        flagView.setX(getMeasuredWidth() - flagView.getMeasuredWidth());
      }
    }
  }

  /**
   * gets the selected color.
   *
   * @return the selected color.
   */
  public @ColorInt int getColor() {
    return selectedColor;
  }

  /**
   * gets an alpha value from the selected color.
   *
   * @return alpha from the selected color.
   */
  public @FloatRange(from = 0.0, to = 1.0) float getAlpha() {
    return Color.alpha(getColor()) / 255f;
  }

  /**
   * gets the selected pure color without alpha and brightness.
   *
   * @return the selected pure color.
   */
  public @ColorInt int getPureColor() {
    return selectedPureColor;
  }

  /**
   * sets the pure color.
   *
   * @param color the pure color.
   */
  public void setPureColor(@ColorInt int color) {
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
    flagView.setAlpha(flag_alpha);
    flagView.setFlipAble(flag_isFlipAble);
  }

  /**
   * gets a debounce duration.
   *
   * <p>only emit a color to the listener if a particular timespan has passed without it emitting
   * another value.
   *
   * @return debounceDuration.
   */
  public long getDebounceDuration() {
    return this.debounceDuration;
  }

  /**
   * sets a debounce duration.
   *
   * <p>only emit a color to the listener if a particular timespan has passed without it emitting
   * another value.
   *
   * @param debounceDuration intervals.
   */
  public void setDebounceDuration(long debounceDuration) {
    this.debounceDuration = debounceDuration;
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
   * gets a selector.
   *
   * @return selector.
   */
  public ImageView getSelector() {
    return this.selector;
  }

  /**
   * gets a selector's selected coordinate x.
   *
   * @return a selected coordinate x.
   */
  public float getSelectorX() {
    return selector.getX() - (selector.getMeasuredWidth() * 0.5f);
  }

  /**
   * gets a selector's selected coordinate y.
   *
   * @return a selected coordinate y.
   */
  public float getSelectorY() {
    return selector.getY() - (selector.getMeasuredHeight() * 0.5f);
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
    Point mappedPoint = PointMapper.getColorPoint(this, new Point(x, y));
    int color = getColorFromBitmap(mappedPoint.x, mappedPoint.y);
    selectedPureColor = color;
    selectedColor = color;
    selectedPoint = new Point(mappedPoint.x, mappedPoint.y);
    setCoordinate(mappedPoint.x, mappedPoint.y);
    fireColorListener(getColor(), false);
    notifyToFlagView(selectedPoint);
  }

  /**
   * moves selector's selected point with notifies about changes manually.
   *
   * @param x coordinate x of the selector.
   * @param y coordinate y of the selector.
   */
  public void moveSelectorPoint(int x, int y, @ColorInt int color) {
    selectedPureColor = color;
    selectedColor = color;
    selectedPoint = new Point(x, y);
    setCoordinate(x, y);
    fireColorListener(getColor(), false);
    notifyToFlagView(selectedPoint);
  }

  /**
   * changes selector's selected point without notifies.
   *
   * @param x coordinate x of the selector.
   * @param y coordinate y of the selector.
   */
  public void setCoordinate(int x, int y) {
    selector.setX(x - (selector.getMeasuredWidth() * 0.5f));
    selector.setY(y - (selector.getMeasuredHeight() * 0.5f));
  }

  /**
   * select a point by a specific color. this method will not work if the default palette drawable
   * is not {@link ColorHsvPalette}.
   *
   * @param color a starting color.
   */
  public void setInitialColor(@ColorInt final int color) {
    if (getPreferenceName() == null
        || (getPreferenceName() != null
            && preferenceManager.getColor(getPreferenceName(), -1) == -1)) {
      post(
          () -> {
            try {
              selectByHsvColor(color);
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            }
          });
    }
  }

  /**
   * select a point by a specific color resource. this method will not work if the default palette
   * drawable is not {@link ColorHsvPalette}.
   *
   * @param colorRes a starting color resource.
   */
  public void setInitialColorRes(@ColorRes final int colorRes) {
    setInitialColor(ContextCompat.getColor(getContext(), colorRes));
  }

  /**
   * changes selector's selected point by a specific color.
   *
   * <p>It will throw an exception if the default palette drawable is not {@link ColorHsvPalette}.
   *
   * @param color color.
   */
  public void selectByHsvColor(@ColorInt int color) throws IllegalAccessException {
    if (palette.getDrawable() instanceof ColorHsvPalette) {
      float[] hsv = new float[3];
      Color.colorToHSV(color, hsv);

      float centerX = getWidth() * 0.5f;
      float centerY = getHeight() * 0.5f;
      float radius = hsv[1] * Math.min(centerX, centerY);
      int pointX = (int) (radius * Math.cos(Math.toRadians(hsv[0])) + centerX);
      int pointY = (int) (-radius * Math.sin(Math.toRadians(hsv[0])) + centerY);

      Point mappedPoint = PointMapper.getColorPoint(this, new Point(pointX, pointY));
      selectedPureColor = color;
      selectedColor = color;
      selectedPoint = new Point(mappedPoint.x, mappedPoint.y);
      if (getAlphaSlideBar() != null) {
        getAlphaSlideBar().setSelectorByHalfSelectorPosition(getAlpha());
      }
      if (getBrightnessSlider() != null) {
        getBrightnessSlider().setSelectorByHalfSelectorPosition(hsv[2]);
      }
      setCoordinate(mappedPoint.x, mappedPoint.y);
      fireColorListener(getColor(), false);
      notifyToFlagView(selectedPoint);
    } else {
      throw new IllegalAccessException(
          "selectByHsvColor(@ColorInt int color) can be called only "
              + "when the palette is an instance of ColorHsvPalette. Use setHsvPaletteDrawable();");
    }
  }

  /**
   * changes selector's selected point by a specific color resource.
   *
   * <p>It may not work properly if change the default palette drawable.
   *
   * @param resource a color resource.
   */
  public void selectByHsvColorRes(@ColorRes int resource) throws IllegalAccessException {
    selectByHsvColor(ContextCompat.getColor(getContext(), resource));
  }

  /**
   * The default palette drawable is {@link ColorHsvPalette} if not be set the palette drawable
   * manually. This method can be used for changing as {@link ColorHsvPalette} from another palette
   * drawable.
   */
  public void setHsvPaletteDrawable() {
    Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
    setPaletteDrawable(new ColorHsvPalette(getResources(), bitmap));
  }

  /**
   * changes palette drawable manually.
   *
   * @param drawable palette drawable.
   */
  public void setPaletteDrawable(Drawable drawable) {
    removeView(palette);
    palette = new ImageView(getContext());
    paletteDrawable = drawable;
    palette.setImageDrawable(paletteDrawable);
    addView(palette);

    removeView(selector);
    addView(selector);

    selectedPureColor = Color.WHITE;
    notifyToSlideBars();

    if (flagView != null) {
      removeView(flagView);
      addView(flagView);
    }

    if (!VISIBLE_FLAG) {
      VISIBLE_FLAG = true;
      if (selector != null) {
        selector_alpha = selector.getAlpha();
        selector.setAlpha(0.0f);
      }
      if (flagView != null) {
        flag_alpha = flagView.getAlpha();
        flagView.setAlpha(0.0f);
      }
    }
  }

  /**
   * changes selector drawable manually.
   *
   * @param drawable selector drawable.
   */
  public void setSelectorDrawable(Drawable drawable) {
    selector.setImageDrawable(drawable);
  }

  /** selects the center of the palette manually. */
  public void selectCenter() {
    setSelectorPoint(getMeasuredWidth() / 2, getMeasuredHeight() / 2);
  }

  /**
   * sets enabling or not the ColorPickerView and slide bars.
   *
   * @param enabled true/false flag for making enable or not.
   */
  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);

    selector.setVisibility(enabled ? VISIBLE : INVISIBLE);

    if (getAlphaSlideBar() != null) {
      getAlphaSlideBar().setEnabled(enabled);
    }

    if (getBrightnessSlider() != null) {
      getBrightnessSlider().setEnabled(enabled);
    }

    if (enabled) {
      palette.clearColorFilter();
    } else {
      int color = Color.argb(70, 255, 255, 255);
      palette.setColorFilter(color);
    }
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
  public @Nullable AlphaSlideBar getAlphaSlideBar() {
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
  public @Nullable BrightnessSlideBar getBrightnessSlider() {
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
  public @Nullable String getPreferenceName() {
    return preferenceName;
  }

  /**
   * sets the preference name.
   *
   * @param preferenceName preference name.
   */
  public void setPreferenceName(@Nullable String preferenceName) {
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
    preferenceManager.saveColorPickerData(this);
  }

  /** Builder class for create {@link ColorPickerView}. */
  public static class Builder {
    private final Context context;
    private ColorPickerViewListener colorPickerViewListener;
    private int debounceDuration = 0;
    private FlagView flagView;
    private Drawable paletteDrawable;
    private Drawable selectorDrawable;
    private AlphaSlideBar alphaSlideBar;
    private BrightnessSlideBar brightnessSlider;
    private ActionMode actionMode = ActionMode.ALWAYS;
    @ColorInt private int initialColor = 0;

    @FloatRange(from = 0.0, to = 1.0)
    private float selector_alpha = 1.0f;

    @FloatRange(from = 0.0, to = 1.0)
    private float flag_alpha = 1.0f;

    private boolean flag_isFlipAble = false;

    @Dp private int selectorSize = 0;
    @Dp private int width = LayoutParams.MATCH_PARENT;
    @Dp private int height = LayoutParams.MATCH_PARENT;
    private String preferenceName;
    private LifecycleOwner lifecycleOwner;

    public Builder(Context context) {
      this.context = context;
    }

    public Builder setColorListener(ColorPickerViewListener colorPickerViewListener) {
      this.colorPickerViewListener = colorPickerViewListener;
      return this;
    }

    public Builder setDebounceDuration(int debounceDuration) {
      this.debounceDuration = debounceDuration;
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

    public Builder setSelectorAlpha(@FloatRange(from = 0.0, to = 1.0) float alpha) {
      this.selector_alpha = alpha;
      return this;
    }

    public Builder setFlagAlpha(@FloatRange(from = 0.0, to = 1.0) float alpha) {
      this.flag_alpha = alpha;
      return this;
    }

    public Builder setFlagIsFlipAble(boolean isFlipAble) {
      this.flag_isFlipAble = isFlipAble;
      return this;
    }

    public Builder setSelectorSize(@Dp int size) {
      this.selectorSize = size;
      return this;
    }

    public Builder setWidth(@Dp int width) {
      this.width = width;
      return this;
    }

    public Builder setHeight(@Dp int height) {
      this.height = height;
      return this;
    }

    public Builder setInitialColor(@ColorInt int initialColor) {
      this.initialColor = initialColor;
      return this;
    }

    public Builder setInitialColorRes(@ColorRes int initialColorRes) {
      this.initialColor = ContextCompat.getColor(context, initialColorRes);
      return this;
    }

    public Builder setPreferenceName(@Nullable String preferenceName) {
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
