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

package com.skydoves.colorpickerview.flag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.FadeUtils;

/** FlaView implements showing a flag above a selector. */
@SuppressWarnings("unused")
public abstract class FlagView extends RelativeLayout {

  private FlagMode flagMode = FlagMode.ALWAYS;
  private boolean flipAble = true;

  public FlagView(Context context, int layout) {
    super(context);
    initializeLayout(layout);
  }

  /** called whenever color is changed on {@link com.skydoves.colorpickerview.ColorPickerView}. */
  public abstract void onRefresh(ColorEnvelope colorEnvelope);

  public void receiveOnTouchEvent(MotionEvent event) {
    switch (event.getActionMasked()) {
      case MotionEvent.ACTION_DOWN:
        if (getFlagMode() == FlagMode.LAST) gone();
        else if (getFlagMode() == FlagMode.FADE) FadeUtils.fadeIn(this);
        break;
      case MotionEvent.ACTION_MOVE:
        if (getFlagMode() == FlagMode.LAST) gone();
        break;
      case MotionEvent.ACTION_UP:
        if (getFlagMode() == FlagMode.LAST) visible();
        else if (getFlagMode() == FlagMode.FADE) FadeUtils.fadeOut(this);
    }
  }

  private void initializeLayout(int layout) {
    View inflated = LayoutInflater.from(getContext()).inflate(layout, this);
    inflated.setLayoutParams(
        new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    inflated.measure(
        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    inflated.layout(0, 0, inflated.getMeasuredWidth(), inflated.getMeasuredHeight());
  }

  /** makes {@link FlagView} visible. */
  public void visible() {
    setVisibility(View.VISIBLE);
  }

  /** makes {@link FlagView} invisible. */
  public void gone() {
    setVisibility(View.GONE);
  }

  /**
   * gets the flag's mode of visibility action.
   *
   * @return {@link FlagMode}
   */
  public FlagMode getFlagMode() {
    return flagMode;
  }

  /**
   * sets the flag's mode of visibility action.
   *
   * @param flagMode {@link FlagMode}
   */
  public void setFlagMode(FlagMode flagMode) {
    this.flagMode = flagMode;
  }

  /**
   * gets is flag flip-able.
   *
   * @return true or false.
   */
  public boolean isFlipAble() {
    return flipAble;
  }

  /**
   * sets the flag being flipped down-sided automatically.
   *
   * @param flipAble true or false.
   */
  public void setFlipAble(boolean flipAble) {
    this.flipAble = flipAble;
  }
}
