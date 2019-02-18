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

/** ColorEnvelope is a wrapper class of colors for provide various forms of color. */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ColorEnvelope {

  private int color;
  private String hexCode;
  private int[] argb;

  public ColorEnvelope(int color) {
    this.color = color;
    this.hexCode = ColorUtils.getHexCode(color);
    this.argb = ColorUtils.getColorARGB(color);
  }

  /**
   * gets envelope's color.
   *
   * @return color.
   */
  public int getColor() {
    return color;
  }

  /**
   * gets envelope's hex code value.
   *
   * @return hex code.
   */
  public String getHexCode() {
    return hexCode;
  }

  /**
   * gets envelope's argb color.
   *
   * @return argb integer array.
   */
  public int[] getArgb() {
    return argb;
  }
}
