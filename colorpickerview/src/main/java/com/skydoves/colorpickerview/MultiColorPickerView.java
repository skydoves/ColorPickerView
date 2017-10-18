
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
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MultiColorPickerView extends FrameLayout {

    private int selectedColor;
    private Point selectedPoint;

    private ImageView palette;
    private Selector mainSelector;

    private Drawable paletteDrawable;

    private List<Selector> selectorList;

    private float alpha = 0.5f;

    public MultiColorPickerView(Context context) {
        super(context);
    }

    public MultiColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        getAttrs(attrs);
        onCreate();
    }

    public MultiColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        getAttrs(attrs);
        onCreate();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MultiColorPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
        getAttrs(attrs);
        onCreate();
    }

    private void init() {
        selectorList = new ArrayList<>();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                onFirstLayout();
            }
        });
    }

    private void onFirstLayout() {
        selectCenter();
        loadListeners();
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MultiColorPickerView);
        try {
            if (a.hasValue(R.styleable.MultiColorPickerView_palette2))
                paletteDrawable = a.getDrawable(R.styleable.MultiColorPickerView_palette2);
        } finally {
            a.recycle();
        }
    }

    private void onCreate() {
        setPadding(0, 0, 0, 0);
        palette = new ImageView(getContext());
        if (paletteDrawable != null)
            palette.setImageDrawable(paletteDrawable);

        LayoutParams wheelParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wheelParams.gravity = Gravity.CENTER;
        addView(palette, wheelParams);
    }

    private void loadListeners() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mainSelector != null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mainSelector.getSelector().setPressed(true);
                            return onTouchReceived(event);
                        case MotionEvent.ACTION_MOVE:
                            mainSelector.getSelector().setPressed(true);
                            return onTouchReceived(event);
                        default:
                            mainSelector.getSelector().setPressed(false);
                            return false;
                    }
                } else
                    return false;
            }
        });
    }

    private boolean onTouchReceived(MotionEvent event) {
        Point snapPoint = new Point((int)event.getX(), (int)event.getY());
        selectedColor = getColorFromBitmap(snapPoint.x, snapPoint.y);

        // check validation
        if(getColor() != Color.TRANSPARENT) {
            mainSelector.getSelector().setX(snapPoint.x - (mainSelector.getSelector().getMeasuredWidth() / 2));
            mainSelector.getSelector().setY(snapPoint.y - (mainSelector.getSelector().getMeasuredHeight() / 2));
            selectedPoint = new Point(snapPoint.x, snapPoint.y);
            fireColorListener(getColor());
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

    private void fireColorListener(int color) {
        if (mainSelector.getColorListener() != null) {
            mainSelector.getColorListener().onColorSelected(color);
        }
    }

    public int getColor() {
        return selectedColor;
    }

    public String getColorHtml(){
        return String.format("%06X", (0xFFFFFF & selectedColor));
    }

    public int[] getColorRGB() {
        int[] rgb = new int[3];
        int color = (int) Long.parseLong(String.format("%06X", (0xFFFFFF & selectedColor)), 16);
        rgb[0] = (color >> 16) & 0xFF; // hex to int : R
        rgb[1] = (color >> 8) & 0xFF; // hex to int : G
        rgb[2] = (color >> 0) & 0xFF; // hex to int : B
        return rgb;
    }

    public void setSelectorPoint(int x, int y) {
        if(mainSelector != null) {
            mainSelector.getSelector().setX(x);
            mainSelector.getSelector().setY(y);
            selectedPoint = new Point(x, y);
            selectedColor = getColorFromBitmap(x, y);
            fireColorListener(getColor());
        }
    }

    private void removeSelector(int index) {
        if(index <= selectorList.size()) {
            removeView(selectorList.remove(index - 1).getSelector());
            selectorList.remove(index - 1);
        }
    }

    public void addSelector(Drawable drawable, ColorListener colorListener) {
        if(drawable == null || colorListener == null) return;

        final ImageView selectorImage = new ImageView(getContext());
        selectorImage.setImageDrawable(drawable);
        final Selector selector = new Selector(selectorImage, colorListener);

        selector.getSelector().setX(getMeasuredWidth()/2 - selector.getSelector().getWidth()/2);
        selector.getSelector().setY(getMeasuredHeight()/2- selector.getSelector().getHeight()/2);

        FrameLayout.LayoutParams thumbParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        thumbParams.gravity = Gravity.CENTER;

        selectorImage.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        alphaSwap(selector);
                        mainSelector = selector;

                        Matrix invertMatrix = new Matrix();
                        palette.getImageMatrix().invert(invertMatrix);

                        float[] mappedPoints = new float[]{motionEvent.getX(), motionEvent.getY()};
                        invertMatrix.mapPoints(mappedPoints);

                        MotionEvent event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, motionEvent.getX(), motionEvent.getY(), 0);
                        dispatchTouchEvent(event);
                        break;
                }
                return false;
            }
        });

        addView(selector.getSelector(), thumbParams);
        alphaSwap(selector);
        selectorList.add(selector);
        mainSelector = selector;
    }

    public void alphaSwap(Selector selector) {
        if(mainSelector != null) {
            mainSelector.getSelector().setAlpha(1.0f);
            selector.getSelector().setAlpha(alpha);
        }
    }

    public void selectCenter() {
        if(mainSelector != null)
            setSelectorPoint(getMeasuredWidth()/2 - mainSelector.getSelector().getWidth()/2, getMeasuredHeight()/2- mainSelector.getSelector().getHeight()/2);
    }

    public void setSelectorAlpha(float alpha) {
        this.alpha = alpha;
    }

    public Point getSelectPoint() {
        return selectedPoint;
    }
}
