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

package com.skydoves.colorpickerview.sliders;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

/**
 * AlphaTileDrawable visualizes ARGB color on the {@link
 * com.skydoves.colorpickerview.AlphaTileView}.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class AlphaTileDrawable extends Drawable {

  private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private int tileSize;
  private int tileOddColor;
  private int tileEvenColor;

  public AlphaTileDrawable() {
    super();
    Builder builder = new Builder();
    this.tileSize = builder.tileSize;
    this.tileOddColor = builder.tileOddColor;
    this.tileEvenColor = builder.tileEvenColor;
    drawTiles();
  }

  public AlphaTileDrawable(Builder builder) {
    super();
    this.tileSize = builder.tileSize;
    this.tileOddColor = builder.tileOddColor;
    this.tileEvenColor = builder.tileEvenColor;
    drawTiles();
  }

  private void drawTiles() {
    Bitmap bitmap = Bitmap.createBitmap(tileSize * 2, tileSize * 2, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    Rect rect = new Rect(0, 0, tileSize, tileSize);

    Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    bitmapPaint.setStyle(Paint.Style.FILL);

    bitmapPaint.setColor(tileOddColor);
    drawTile(canvas, rect, bitmapPaint, 0, 0);
    drawTile(canvas, rect, bitmapPaint, tileSize, tileSize);

    bitmapPaint.setColor(tileEvenColor);
    drawTile(canvas, rect, bitmapPaint, -tileSize, 0);
    drawTile(canvas, rect, bitmapPaint, tileSize, -tileSize);

    paint.setShader(
        new BitmapShader(bitmap, BitmapShader.TileMode.REPEAT, BitmapShader.TileMode.REPEAT));
  }

  private void drawTile(Canvas canvas, Rect rect, Paint bitmapPaint, int dx, int dy) {
    rect.offset(dx, dy);
    canvas.drawRect(rect, bitmapPaint);
  }

  @Override
  public void draw(@NonNull Canvas canvas) {
    canvas.drawPaint(paint);
  }

  @Override
  public void setAlpha(int alpha) {
    paint.setAlpha(alpha);
  }

  @Override
  public void setColorFilter(ColorFilter colorFilter) {
    paint.setColorFilter(colorFilter);
  }

  @Override
  public int getOpacity() {
    return PixelFormat.OPAQUE;
  }

  /** Builder class for create {@link AlphaTileDrawable}. */
  @SuppressWarnings("UnusedReturnValue")
  public static class Builder {
    private int tileSize = 25;
    private int tileOddColor = 0xFFFFFFFF;
    private int tileEvenColor = 0xFFCBCBCB;

    public int getTileSize() {
      return tileSize;
    }

    public Builder setTileSize(int tileSize) {
      this.tileSize = tileSize;
      return this;
    }

    public int getTileOddColor() {
      return tileOddColor;
    }

    public Builder setTileOddColor(int color) {
      this.tileOddColor = color;
      return this;
    }

    public int getTileEvenColor() {
      return tileEvenColor;
    }

    public Builder setTileEvenColor(int color) {
      this.tileEvenColor = color;
      return this;
    }

    public AlphaTileDrawable build() {
      return new AlphaTileDrawable(this);
    }
  }
}
