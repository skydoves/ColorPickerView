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

import com.skydoves.colorpickerview.ColorPickerView;

public abstract class AbstractSlider extends FrameLayout {

    public ColorPickerView colorPickerView;
    protected Paint colorPaint;
    protected Paint borderPaint;
    protected float selectorPosition = 1f;
    protected Drawable selectorDrawable;
    protected int borderSize = 2;
    protected int borderColor = Color.BLACK;
    protected int color = Color.WHITE;
    private ImageView selector;

    public AbstractSlider(Context context) {
        super(context);
        onCreate();
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

    protected abstract void getAttrs(AttributeSet attrs);

    protected abstract void updatePaint(Paint colorPaint);

    public abstract int assembleColor();

    private void onCreate() {
        this.colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.borderPaint.setStyle(Paint.Style.STROKE);
        this.borderPaint.setStrokeWidth(borderSize);
        this.borderPaint.setColor(borderColor);
        this.setBackgroundColor(Color.WHITE);

        selector = new ImageView(getContext());
        if (selectorDrawable != null) {
            selector.setImageDrawable(selectorDrawable);

            FrameLayout.LayoutParams thumbParams =
                  new LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            thumbParams.gravity = Gravity.CENTER_VERTICAL;
            addView(selector, thumbParams);
        }

        initializeSelector();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getMeasuredWidth();
        float height = getMeasuredHeight();
        canvas.drawRect(0, 0, width, height, colorPaint);
        canvas.drawRect(0, 0, width, height, borderPaint);
    }

    public void notifyColor() {
        color = colorPickerView.getColor();
        updatePaint(colorPaint);
        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (colorPickerView != null) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_UP:
                    selector.setPressed(true);
                    onTouchReceived(event);
                    return true;
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    selector.setPressed(true);
                    onTouchReceived(event);
                    return true;
                default:
                    selector.setPressed(false);
                    return false;
            }
        } else return false;
    }

    private void onTouchReceived(MotionEvent event) {
        float eventX = event.getX();
        float left = selector.getMeasuredWidth();
        float right = getMeasuredWidth() - selector.getMeasuredWidth();
        if (eventX < left) eventX = left;
        if (eventX > right) eventX = right;
        selectorPosition = (eventX - left) / (right - left);

        Point snapPoint = new Point((int) event.getX(), (int) event.getY());
        selector.setX(snapPoint.x - (selector.getMeasuredWidth() / 2));
        if (colorPickerView.getACTON_UP()) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                colorPickerView.fireColorListener(assembleColor(), true);
            }
        } else {
            colorPickerView.fireColorListener(assembleColor(), true);
        }

        int maxPos = getMeasuredWidth() - selector.getMeasuredWidth();
        if (selector.getX() >= maxPos) selector.setX(maxPos);
        if (selector.getX() <= 0) selector.setX(0);
    }

    private void initializeSelector() {
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
                            selector.setX(getMeasuredWidth() - selector.getMeasuredWidth());
                        }
                    });
    }

    public int getColor() {
        return color;
    }

    public void attachColorPickerView(ColorPickerView colorPickerView) {
        this.colorPickerView = colorPickerView;
    }

    public float getSelectorPosition() {
        return this.selectorPosition;
    }
}
