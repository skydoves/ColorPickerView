
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

public class ColorEnvelope {

    private int color;
    private String htmlCode;
    private int[] rgb;

    public ColorEnvelope(int color, String htmlCode, int[] rgb) {
        this.color = color;
        this.htmlCode = htmlCode;
        this.rgb = rgb;
    }

    public int getColor() {
        return color;
    }

    public String getHtmlCode() {
        return htmlCode;
    }

    public int[] getRgb() {
        return rgb;
    }
}
