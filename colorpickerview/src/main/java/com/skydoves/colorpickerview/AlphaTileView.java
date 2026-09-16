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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.skydoves.colorpickerview.sliders.AlphaTileDrawable;

/** AlphaTileView visualizes ARGB color on the canvas using {@link AlphaTileDrawable}. */
@SuppressWarnings("unused")
public class AlphaTileView extends View {

  private Paint colorPaint;
  private Paint strokePaint;
  private Bitmap backgroundBitmap;
  private float strokeSize = 0f;
  private final AlphaTileDrawable.Builder builder = new AlphaTileDrawable.Builder();

  public AlphaTileView(Context context) {
    super(context);
    onCreate();
  }

  public AlphaTileView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    onCreate();
    getAttrs(attrs);
  }

  public AlphaTileView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    onCreate();
    getAttrs(attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public AlphaTileView(
      Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    onCreate();
    getAttrs(attrs);
  }

  private void onCreate() {
    this.colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    this.strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    this.strokePaint.setStyle(Paint.Style.STROKE);
    this.strokePaint.setColor(Color.TRANSPARENT);
    this.setBackgroundColor(Color.WHITE);
  }

  private void getAttrs(AttributeSet attrs) {
    TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AlphaTileView);
    try {
      if (a.hasValue(R.styleable.AlphaTileView_tileSize)) {
        builder.setTileSize(a.getInt(R.styleable.AlphaTileView_tileSize, builder.getTileSize()));
      }
      if (a.hasValue(R.styleable.AlphaTileView_tileOddColor)) {
        builder.setTileOddColor(
            a.getInt(R.styleable.AlphaTileView_tileOddColor, builder.getTileOddColor()));
      }
      if (a.hasValue(R.styleable.AlphaTileView_tileEvenColor)) {
        builder.setTileEvenColor(
            a.getInt(R.styleable.AlphaTileView_tileEvenColor, builder.getTileEvenColor()));
      }
      if (a.hasValue(R.styleable.AlphaTileView_tileStrokeColor)) {
        strokePaint.setColor(
            a.getColor(R.styleable.AlphaTileView_tileStrokeColor, strokePaint.getColor()));
      }
      if (a.hasValue(R.styleable.AlphaTileView_tileStrokeSize)) {
        setStrokeSize(a.getDimension(R.styleable.AlphaTileView_tileStrokeSize, strokeSize));
      }
    } finally {
      a.recycle();
    }
  }

  @Override
  protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
    super.onSizeChanged(width, height, oldWidth, oldHeight);
    if (width <= 0 || height <= 0) {
      backgroundBitmap = null;
      return;
    }

    AlphaTileDrawable drawable = builder.build();
    backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    if (!backgroundBitmap.isRecycled()) {
      Canvas backgroundCanvas = new Canvas(backgroundBitmap);
      drawable.setBounds(0, 0, backgroundCanvas.getWidth(), backgroundCanvas.getHeight());
      drawable.draw(backgroundCanvas);
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (backgroundBitmap != null && !backgroundBitmap.isRecycled()) {
      canvas.drawBitmap(backgroundBitmap, 0, 0, null);
    }
    canvas.drawRect(0, 0, getWidth(), getMeasuredHeight(), colorPaint);
    drawStroke(canvas);
  }

  /** draws the stroke inside of the view, so it isn't covered by the painted color. */
  private void drawStroke(Canvas canvas) {
    if (strokeSize <= 0 || Color.alpha(strokePaint.getColor()) == 0) return;

    float half = strokeSize * 0.5f;
    canvas.drawRect(
        half, half, getWidth() - half, getMeasuredHeight() - half, strokePaint);
  }

  public void setPaintColor(int color) {
    colorPaint.setColor(color);
    invalidate();
  }

  /**
   * sets a color of the stroke, which is drawn on the edge of the view. it can be used for
   * distinguishing the painted color from the background.
   *
   * @param color a color of the stroke.
   */
  public void setStrokeColor(@ColorInt int color) {
    strokePaint.setColor(color);
    invalidate();
  }

  /**
   * sets a color resource of the stroke, which is drawn on the edge of the view.
   *
   * @param resource a color resource of the stroke.
   */
  public void setStrokeColorRes(@ColorRes int resource) {
    setStrokeColor(ContextCompat.getColor(getContext(), resource));
  }

  /**
   * sets a size of the stroke in pixels, which is drawn on the edge of the view.
   *
   * @param strokeSize a size of the stroke in pixels.
   */
  public void setStrokeSize(float strokeSize) {
    this.strokeSize = Math.max(0f, strokeSize);
    this.strokePaint.setStrokeWidth(this.strokeSize);
    invalidate();
  }

  /**
   * sets a size of the stroke using a dimension resource.
   *
   * @param resource a dimension resource of the stroke size.
   */
  public void setStrokeSizeRes(@DimenRes int resource) {
    setStrokeSize(getContext().getResources().getDimension(resource));
  }

  @Override
  public void setBackgroundColor(int color) {
    setPaintColor(color);
  }
}
