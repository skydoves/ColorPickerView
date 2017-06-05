
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

package com.skydoves.colorpickerviewdemo.ColorPickerView;

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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.skydoves.colorpickerviewdemo.R;

public class ColorPickerView extends FrameLayout {

    private int selectedColor;
    private Point selectedPoint;

    private ImageView imageView;
    private ImageView selector;

    @Nullable
    private Drawable imageViewDrawable;
    private Drawable selectorDrawable;

    @Nullable
    protected ColorListener mColorListener;

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
                onFirstLayout();
            }
        });
    }

    private void onFirstLayout() {
        selectedPoint = new Point(getMeasuredWidth()/2, getMeasuredHeight()/2);
        onTouchReceived(
                MotionEvent.obtain(System.currentTimeMillis(),
                        System.currentTimeMillis() + 100,
                        MotionEvent.ACTION_UP,
                        getMeasuredWidth() / 2,
                        getMeasuredHeight() / 2,
                        0)
        );
        loadListeners();
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.colorpicker);
        try {
            if (a.hasValue(R.styleable.colorpicker_src))
                imageViewDrawable = a.getDrawable(R.styleable.colorpicker_src);
            if (a.hasValue(R.styleable.colorpicker_selector))
                selectorDrawable = a.getDrawable(R.styleable.colorpicker_selector);
        } finally {
            a.recycle();
        }
    }

    private void onCreate() {
        imageView = new ImageView(getContext());
        if (imageViewDrawable != null)
            imageView.setImageDrawable(imageViewDrawable);

        FrameLayout.LayoutParams wheelParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wheelParams.gravity = Gravity.CENTER;
        addView(imageView, wheelParams);

        selector = new ImageView(getContext());
        if (selectorDrawable != null) {
            selector.setImageDrawable(selectorDrawable);

            FrameLayout.LayoutParams thumbParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            thumbParams.gravity = Gravity.CENTER;
            addView(selector, thumbParams);
        }
    }

    private void loadListeners() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        selector.setPressed(true);
                        return onTouchReceived(event);
                    case MotionEvent.ACTION_MOVE:
                        selector.setPressed(true);
                        return onTouchReceived(event);
                    default:
                        selector.setPressed(false);
                        return false;
                }
            }
        });
    }

    private boolean onTouchReceived(@NonNull MotionEvent event) {
        Point snapPoint = new Point((int)event.getX(), (int)event.getY());
        selectedColor = getColorFromBitmap(snapPoint.x, snapPoint.y);

        // check is selector pointed position is transparent field?
        if(getColor() != Color.TRANSPARENT) {
            selector.setX(snapPoint.x - (selector.getMeasuredWidth() / 2));
            selector.setY(snapPoint.y - (selector.getMeasuredHeight() / 2));
            selectedPoint = new Point(snapPoint.x, snapPoint.y);
            fireColorListener(getColor());
        }
        // else
        else{
            selector.setX(selectedPoint.x - (selector.getMeasuredWidth() / 2));
            selector.setY(selectedPoint.y - (selector.getMeasuredHeight() / 2));
            selectedColor = getColorFromBitmap(selectedPoint.x , selectedPoint.y);
            fireColorListener(selectedColor);
        }
        return true;
    }

    private int getColorFromBitmap(float x, float y) {
        if (imageViewDrawable == null) return 0;

        Matrix invertMatrix = new Matrix();
        imageView.getImageMatrix().invert(invertMatrix);

        float[] mappedPoints = new float[]{x, y};
        invertMatrix.mapPoints(mappedPoints);

        if (imageView.getDrawable() != null && imageView.getDrawable() instanceof BitmapDrawable &&
                mappedPoints[0] > 0 && mappedPoints[1] > 0 &&
                mappedPoints[0] < imageView.getDrawable().getIntrinsicWidth() && mappedPoints[1] < imageView.getDrawable().getIntrinsicHeight()) {

            invalidate();
            return ((BitmapDrawable) imageView.getDrawable()).getBitmap().getPixel((int) mappedPoints[0], (int) mappedPoints[1]);
        }
        return 0;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    private void fireColorListener(int color) {
        if (mColorListener != null) {
            mColorListener.onColorSelected(color);
        }
    }

    public void setColorListener(@Nullable ColorListener colorListener) {
        mColorListener = colorListener;
    }

    public interface ColorListener {
        void onColorSelected(int color);
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
        selector.setX(x);
        selector.setY(y);
        selectedPoint = new Point(x, y);
        selectedColor = getColorFromBitmap(x, y);
        fireColorListener(getColor());
    }

    public void setPaletteDrawable(Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    public void setSelectorDrawable(Drawable drawable) {
        selector.setImageDrawable(drawable);
    }
}
