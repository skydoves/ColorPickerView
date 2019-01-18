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
import android.graphics.Canvas;
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
import com.skydoves.colorpickerview.flag.FlagMode;
import com.skydoves.colorpickerview.flag.FlagView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.listeners.ColorListener;
import com.skydoves.colorpickerview.listeners.ColorPickerViewListener;
import com.skydoves.colorpickerview.sliders.AlphaSlideBar;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;
import java.util.Locale;

@SuppressWarnings({"WeakerAccess", "unchecked", "unused", "IntegerDivisionInFloatingPointContext"})
public class ColorPickerView extends FrameLayout {

    public ColorPickerViewListener mColorListener;
    private int selectedColor;
    private Point selectedPoint;
    private ImageView palette;
    private ImageView selector;
    private FlagView flagView;
    private Drawable paletteDrawable;
    private Drawable selectorDrawable;
    private AlphaSlideBar alphaSlideBar;
    private BrightnessSlideBar brightnessSlider;

    private ActionMode actionMode = ActionMode.ALWAYS;

    private float alpha_selector = 1.0f;
    private float alpha_flag = 1.0f;
    private boolean VISIBLE_FLAG = false;

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
                paletteDrawable = a.getDrawable(R.styleable.ColorPickerView_palette);
            if (a.hasValue(R.styleable.ColorPickerView_selector))
                selectorDrawable = a.getDrawable(R.styleable.ColorPickerView_selector);
            if (a.hasValue(R.styleable.ColorPickerView_alpha_selector))
                alpha_selector =
                        a.getFloat(R.styleable.ColorPickerView_alpha_selector, alpha_selector);
            if (a.hasValue(R.styleable.ColorPickerView_alpha_flag))
                alpha_flag = a.getFloat(R.styleable.ColorPickerView_alpha_flag, alpha_flag);
            if (a.hasValue(R.styleable.ColorPickerView_actionMode)) {
                int actionMode = a.getInteger(R.styleable.ColorPickerView_actionMode, 0);
                if (actionMode == 0) this.actionMode = ActionMode.ALWAYS;
                else if (actionMode == 1) this.actionMode = ActionMode.LAST;
            }
        } finally {
            a.recycle();
        }
    }

    private void onCreate() {
        setPadding(0, 0, 0, 0);
        palette = new ImageView(getContext());
        if (paletteDrawable != null) palette.setImageDrawable(paletteDrawable);

        FrameLayout.LayoutParams paletteParam =
                new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        paletteParam.gravity = Gravity.CENTER;
        addView(palette, paletteParam);

        selector = new ImageView(getContext());
        if (selectorDrawable != null) {
            selector.setImageDrawable(selectorDrawable);

            FrameLayout.LayoutParams selectorParam =
                    new LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            selectorParam.gravity = Gravity.CENTER;
            addView(selector, selectorParam);
            selector.setAlpha(alpha_selector);
        }

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
                                selectCenter();
                            }
                        });
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

    private boolean onTouchReceived(MotionEvent event) {
        Point snapPoint = new Point((int) event.getX(), (int) event.getY());
        int pixelColor = getColorFromBitmap(snapPoint.x, snapPoint.y);

        if (pixelColor != Color.TRANSPARENT && pixelColor != Color.BLACK) {
            selectedColor = pixelColor;
            selectedPoint = new Point(snapPoint.x, snapPoint.y);
            setCoordinate(snapPoint.x, snapPoint.y);
            handleFlagView(selectedPoint);

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

    private int getColorFromBitmap(float x, float y) {
        if (paletteDrawable == null) return 0;

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
            int x1 =
                    (int)
                            (scaleX
                                    * ((BitmapDrawable) palette.getDrawable())
                                            .getBitmap()
                                            .getHeight());
            float scaleY = mappedPoints[1] / rect.width();
            int y1 =
                    (int)
                            (scaleY
                                    * ((BitmapDrawable) palette.getDrawable())
                                            .getBitmap()
                                            .getWidth());
            return ((BitmapDrawable) palette.getDrawable()).getBitmap().getPixel(x1, y1);
        }
        return 0;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    public void fireColorListener(int color, boolean fromUser) {
        if (mColorListener != null) {
            selectedColor = color;
            if (mColorListener instanceof ColorListener) {
                ((ColorListener) mColorListener).onColorSelected(color, fromUser);
            } else if (mColorListener instanceof ColorEnvelopeListener) {
                ColorEnvelope envelope =
                        new ColorEnvelope(color, getHexCode(color), getColorARGB(color));
                ((ColorEnvelopeListener) mColorListener).onColorSelected(envelope, fromUser);
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

    private void notifyToSlideBars() {
        if (alphaSlideBar != null) alphaSlideBar.notifyColor();
        if (brightnessSlider != null) {
            brightnessSlider.notifyColor();

            if (brightnessSlider.assembleColor() != Color.WHITE)
                selectedColor = brightnessSlider.assembleColor();
            else if (alphaSlideBar != null) selectedColor = alphaSlideBar.assembleColor();
        }
    }

    public void setColorListener(ColorPickerViewListener colorListener) {
        mColorListener = colorListener;
    }

    private void handleFlagView(Point centerPoint) {
        centerPoint = getCenterPoint(centerPoint.x, centerPoint.y);
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

    public int getColor() {
        return selectedColor;
    }

    public ColorEnvelope getColorEnvelope() {
        return new ColorEnvelope(getColor(), getHexCode(getColor()), getColorARGB(getColor()));
    }

    public String getHexCode(int color) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return String.format(Locale.getDefault(), "%02X%02X%02X%02X", a, r, g, b);
    }

    public int[] getColorARGB(int color) {
        int[] argb = new int[4];
        argb[0] = Color.alpha(color);
        argb[1] = Color.red(color);
        argb[2] = Color.green(color);
        argb[3] = Color.blue(color);
        return argb;
    }

    public Point getSelectedPoint() {
        return selectedPoint;
    }

    public FlagView getFlagView() {
        return this.flagView;
    }

    public void setFlagView(FlagView flagView) {
        flagView.gone();
        addView(flagView);
        this.flagView = flagView;
        flagView.setAlpha(alpha_flag);
    }

    private Point getCenterPoint(int x, int y) {
        return new Point(
                x - (selector.getMeasuredWidth() / 2), y - (selector.getMeasuredHeight() / 2));
    }

    public float getSelectorX() {
        return selector.getX() - getSelectorHalfWidth();
    }

    public float getSelectorY() {
        return selector.getY() - getSelectorHalfHeight();
    }

    private int getSelectorHalfWidth() {
        return selector.getMeasuredWidth() / 2;
    }

    private int getSelectorHalfHeight() {
        return selector.getMeasuredHeight() / 2;
    }

    public void setSelectorPoint(int x, int y) {
        selectedColor = getColorFromBitmap(x, y);
        if (selectedColor != Color.TRANSPARENT) {
            selectedPoint = new Point(x, y);
            setCoordinate(x, y);
            fireColorListener(getColor(), false);
            notifyToSlideBars();
            handleFlagView(new Point(x, y));
        }
    }

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

    private void setCoordinate(int x, int y) {
        selector.setX(x - (selector.getMeasuredWidth() / 2));
        selector.setY(y - (selector.getMeasuredHeight() / 2));
    }

    public void setPaletteDrawable(Drawable drawable) {
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

    public void setSelectorDrawable(Drawable drawable) {
        selector.setImageDrawable(drawable);
    }

    public void selectCenter() {
        setSelectorPoint(getMeasuredWidth() / 2, getMeasuredHeight() / 2);
    }

    public ActionMode getActionMode() {
        return this.actionMode;
    }

    public void setActionMode(ActionMode actionMode) {
        this.actionMode = actionMode;
    }

    public void attachAlphaSlider(AlphaSlideBar alphaSlideBar) {
        this.alphaSlideBar = alphaSlideBar;
        alphaSlideBar.attachColorPickerView(this);
        alphaSlideBar.notifyColor();
    }

    public void attachBrightnessSlider(BrightnessSlideBar brightnessSlider) {
        this.brightnessSlider = brightnessSlider;
        brightnessSlider.attachColorPickerView(this);
        brightnessSlider.notifyColor();
    }

    public AlphaSlideBar getAlphaSlideBar() {
        return alphaSlideBar;
    }

    public BrightnessSlideBar getBrightnessSlider() {
        return brightnessSlider;
    }
}
