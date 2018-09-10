
/*
 * Copyright (C) 2018 skydoves
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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skydoves.colorpickerview.flag.FlagView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.listeners.ColorListener;
import com.skydoves.colorpickerview.listeners.ColorPickerViewListener;
import com.skydoves.colorpickerview.sliders.AlphaSlideBar;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

@SuppressWarnings({"WeakerAccess", "unchecked", "unused"})
public class ColorPickerDialog extends AlertDialog {

    private ColorPickerView colorPickerView;

    public ColorPickerDialog(Context context) {
        super(context);
        initColorPickerView();
    }

    protected ColorPickerDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initColorPickerView();
    }

    protected ColorPickerDialog(Context context, int themeResId) {
        super(context, themeResId);
        initColorPickerView();
    }

    private void initColorPickerView() {
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.layout_dialog_colorpicker, null);
        this.colorPickerView = view.findViewById(R.id.ColorPickerView);
        super.setView(view);
    }

    public void setFlagView(FlagView flagView) {
        this.colorPickerView.setFlagView(flagView);
    }

    public void setOnColorListener(ColorListener colorListener) {
        this.colorPickerView.setColorListener(colorListener);
    }

    public static class Builder extends AlertDialog.Builder {
        private ColorPickerView colorPickerView;
        private View view;

        private Context context;

        public Builder(Context context) {
            super(context);
            this.context = context;
            initColorPickerView();
        }

        public Builder(Context context, int themeResId) {
            super(context, themeResId);
            this.context = context;
            initColorPickerView();
        }

        private void initColorPickerView()  {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.view = layoutInflater.inflate(R.layout.layout_dialog_colorpicker, null);
            this.colorPickerView = view.findViewById(R.id.ColorPickerView);
            this.colorPickerView.setColorListener(new ColorListener() {
                @Override
                public void onColorSelected(int color, boolean fromUser) {
                    // nothing
                }
            });
            super.setView(view);
        }

        public void setFlagView(FlagView flagView) {
            this.colorPickerView.setFlagView(flagView);
        }

        public void setOnColorListener(ColorListener colorListener) {
            this.colorPickerView.setColorListener(colorListener);
        }

        public ColorPickerView getColorPickerView() {
            return this.colorPickerView;
        }

        public void attachAlphaSlideBar() {
            AlphaSlideBar alphaSlideBar = view.findViewById(R.id.AlphaSlideBar);
            colorPickerView.attachAlphaSlider(alphaSlideBar);
            alphaSlideBar.setVisibility(View.VISIBLE);
        }

        public void attachBrightnessSlideBar() {
            BrightnessSlideBar brightnessSlideBar = view.findViewById(R.id.BrightnessSlideBar);
            colorPickerView.attachBrightnessSlider(brightnessSlideBar);
            brightnessSlideBar.setVisibility(View.VISIBLE);
        }

        @Override
        public AlertDialog.Builder setPositiveButton(int textId, OnClickListener listener) {
            return super.setPositiveButton(textId, listener);
        }

        @SuppressWarnings("UnusedReturnValue")
        public AlertDialog.Builder setPositiveButton(CharSequence text, final ColorPickerViewListener colorListener) {
            OnClickListener onClickListener = new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(colorListener instanceof ColorListener) {
                        ((ColorListener)colorListener).onColorSelected(colorPickerView.getColor(), true);
                    } else if(colorListener instanceof ColorEnvelopeListener) {
                        ((ColorEnvelopeListener)colorListener).onColorSelected(colorPickerView.getColorEnvelope(), true);
                    }
                }
            };

            return super.setPositiveButton(text, onClickListener);
        }
    }

    /**
     * disable set overrides
     */
    @Override
    public void setContentView(int layoutResID) {
    }

    @Override
    public void setContentView(@NonNull View view) {
    }

    @Override
    public void setContentView(@NonNull View view, ViewGroup.LayoutParams params) {
    }

    @Override
    public void addContentView(@NonNull View view, ViewGroup.LayoutParams params) {
    }

    @Override
    public void setView(View view) {
    }

    @Override
    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
    }
}
