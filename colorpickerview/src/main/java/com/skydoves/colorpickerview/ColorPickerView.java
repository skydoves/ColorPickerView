
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

import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.listeners.ColorListener;
import com.skydoves.colorpickerview.listeners.ColorPickerViewListener;
import com.skydoves.colorpickerview.sliders.AlphaSlideBar;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

import java.util.Locale;

@SuppressWarnings({"WeakerAccess", "unchecked", "unused"})
public class ColorPickerView extends FrameLayout {

    private int selectedColor;
    private Point selectedPoint;

    private ImageView palette;
    private ImageView selector;

    private Drawable paletteDrawable;
    private Drawable selectorDrawable;

    public ColorPickerViewListener mColorListener;

    private boolean ACTON_UP = false;
    private boolean SELECT_CENTER = true;

    private AlphaSlideBar alphaSlideBar;
    private BrightnessSlideBar brightnessSlider;

    public ColorPickerView(Context context) {
        super(context);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        getAttrs(attrs);
        onCreate();
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        getAttrs(attrs);
        onCreate();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
        getAttrs(attrs);
        onCreate();
    }

    private void init() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
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

    private void getAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ColorPickerView);
        try {
            if (a.hasValue(R.styleable.ColorPickerView_palette))
                paletteDrawable = a.getDrawable(R.styleable.ColorPickerView_palette);
            if (a.hasValue(R.styleable.ColorPickerView_selector))
                selectorDrawable = a.getDrawable(R.styleable.ColorPickerView_selector);
        } finally {
            a.recycle();
        }
    }

    private void onCreate() {
        setPadding(0, 0, 0, 0);
        palette = new ImageView(getContext());
        if (paletteDrawable != null)
            palette.setImageDrawable(paletteDrawable);

        FrameLayout.LayoutParams wheelParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wheelParams.gravity = Gravity.CENTER;
        addView(palette, wheelParams);

        selector = new ImageView(getContext());
        if (selectorDrawable != null) {
            selector.setImageDrawable(selectorDrawable);

            FrameLayout.LayoutParams thumbParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            thumbParams.gravity = Gravity.CENTER;
            addView(selector, thumbParams);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP:
                if(ACTON_UP) {
                    selector.setPressed(true);
                    return onTouchReceived(event);
                }
                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if(!ACTON_UP) {
                    selector.setPressed(true);
                    return onTouchReceived(event);
                }
                break;
            default:
                selector.setPressed(false);
                return false;
        }
        return true;
    }

    private boolean onTouchReceived(MotionEvent event) {
        Point snapPoint = new Point((int)event.getX(), (int)event.getY());
        int pixelColor = getColorFromBitmap(snapPoint.x, snapPoint.y);

        if(pixelColor != Color.TRANSPARENT && pixelColor != Color.BLACK) {
            selectedColor = pixelColor;
            selector.setX(snapPoint.x - (selector.getMeasuredWidth() / 2));
            selector.setY(snapPoint.y - (selector.getMeasuredHeight() / 2));
            selectedPoint = new Point(snapPoint.x, snapPoint.y);

            if(alphaSlideBar != null)
                alphaSlideBar.notifyColor();
            if(brightnessSlider != null) {
                brightnessSlider.notifyColor();

                if(brightnessSlider.assembleColor() != Color.WHITE)
                    selectedColor = brightnessSlider.assembleColor();
                else if(alphaSlideBar != null)
                    selectedColor = alphaSlideBar.assembleColor();
            }

            fireColorListener(getColor(), true);
            return true;
        } else
            return false;
    }

    private int getColorFromBitmap(float x, float y) {
        if (paletteDrawable == null) return 0;

        Matrix invertMatrix = new Matrix();
        palette.getImageMatrix().invert(invertMatrix);

        float[] mappedPoints = new float[]{x, y};
        invertMatrix.mapPoints(mappedPoints);

        if (palette.getDrawable() != null && palette.getDrawable() instanceof BitmapDrawable &&
                mappedPoints[0] > 0 && mappedPoints[1] > 0 &&
                mappedPoints[0] < palette.getDrawable().getIntrinsicWidth() && mappedPoints[1] < palette.getDrawable().getIntrinsicHeight()) {

            invalidate();
            return ((BitmapDrawable) palette.getDrawable()).getBitmap().getPixel((int) mappedPoints[0], (int) mappedPoints[1]);
        }
        return 0;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    public void fireColorListener(int color, boolean fromUser) {
        if (mColorListener != null) {
            if(mColorListener instanceof ColorListener) {
                ((ColorListener) mColorListener).onColorSelected(color, fromUser);
            } else if(mColorListener instanceof ColorEnvelopeListener) {
                ColorEnvelope envelope = new ColorEnvelope(color, getHexCode(color), getColorARGB(color));
                ((ColorEnvelopeListener) mColorListener).onColorSelected(envelope, fromUser);
            }
        }
    }

    public void setColorListener(ColorPickerViewListener colorListener) {
        mColorListener = colorListener;
    }

    public int getColor() {
        return selectedColor;
    }

    public String getHexCode(int color) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return String.format(Locale.getDefault(), "0x%02X%02X%02X%02X", a, r, g, b);
    }

    public int[] getColorARGB(int color) {
        int[] argb = new int[4];
        argb[0] =  Color.alpha(color);
        argb[1] =  Color.red(color);
        argb[2] = Color.green(color);
        argb[3] = Color.blue(color);
        return argb;
    }

    public Point getSelectedPoint() {
        return selectedPoint;
    }

    public void setSelectorPoint(int x, int y) {
        selector.setX(x);
        selector.setY(y);
        selectedPoint = new Point(x, y);
        selectedColor = getColorFromBitmap(x, y);
        fireColorListener(getColor(), false);
    }

    public void setPaletteDrawable(Drawable drawable) {
        removeView(palette);
        palette = new ImageView(getContext());
        paletteDrawable = drawable;
        palette.setImageDrawable(paletteDrawable);
        addView(palette);

        removeView(selector);
        addView(selector);

        if(SELECT_CENTER) {
            selector.setX(getMeasuredWidth() / 2 - selector.getWidth() / 2);
            selector.setY(getMeasuredHeight() / 2 - selector.getHeight() / 2);
        }
    }

    public void setPaletteDrawableSelectCenter(boolean selectCenter) {
        this.SELECT_CENTER = selectCenter;
    }

    public void setSelectorDrawable(Drawable drawable) {
        selector.setImageDrawable(drawable);
    }

    public void selectCenter() {
        setSelectorPoint(getMeasuredWidth() / 2 - selector.getWidth() / 2,
                getMeasuredHeight() / 2- selector.getHeight() / 2);
    }

    public void setACTON_UP(boolean value) {
        this.ACTON_UP = value;
    }

    public boolean getACTON_UP() {
        return this.ACTON_UP;
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
