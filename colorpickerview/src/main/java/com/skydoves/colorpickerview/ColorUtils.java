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
import java.util.Locale;

/** ColorUtils a util class for changing the form of colors. */
@SuppressWarnings("WeakerAccess")
class ColorUtils {
  /** changes color to string hex code. */
  public static String getHexCode(int color) {
    int a = Color.alpha(color);
    int r = Color.red(color);
    int g = Color.green(color);
    int b = Color.blue(color);
    return String.format(Locale.getDefault(), "%02X%02X%02X%02X", a, r, g, b);
  }

  /** changes color to argb integer array. */
  public static int[] getColorARGB(int color) {
    int[] argb = new int[4];
    argb[0] = Color.alpha(color);
    argb[1] = Color.red(color);
    argb[2] = Color.green(color);
    argb[3] = Color.blue(color);
    return argb;
  }
}
