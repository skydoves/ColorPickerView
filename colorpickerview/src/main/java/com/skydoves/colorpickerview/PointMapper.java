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

import android.graphics.Color;
import android.graphics.Point;

@SuppressWarnings("WeakerAccess")
class PointMapper {

  private ColorPickerView colorPickerView;

  protected Point getColorPoint(ColorPickerView colorPickerView, Point point) {
    this.colorPickerView = colorPickerView;
    if (colorPickerView.getColorFromBitmap(point.x, point.y) != Color.TRANSPARENT) return point;
    Point center =
        new Point(colorPickerView.getMeasuredWidth() / 2, colorPickerView.getMeasuredHeight() / 2);
    if (point.x <= center.x && point.y <= center.y) {
      return approximatedPoint(point, center);
    } else if (point.x >= center.x && point.y <= center.y) {
      return approximatedPoint(point, center);
    } else if (point.x >= center.x) {
      return approximatedPoint(point, center);
    } else {
      return approximatedPoint(point, center);
    }
  }

  private Point approximatedPoint(Point start, Point end) {
    if (getDistance(start, end) <= 3) return end;
    Point center = getCenterPoint(start, end);
    int color = colorPickerView.getColorFromBitmap(center.x, center.y);
    if (color == Color.TRANSPARENT) {
      return approximatedPoint(center, end);
    } else {
      return approximatedPoint(start, center);
    }
  }

  private Point getCenterPoint(Point start, Point end) {
    return new Point((end.x + start.x) / 2, (end.y + start.y) / 2);
  }

  private int getDistance(Point start, Point end) {
    return (int)
        Math.sqrt(
            Math.abs(end.x - start.x) * Math.abs(end.x - start.x)
                + Math.abs(end.y - start.y) * Math.abs(end.y - start.y));
  }
}
