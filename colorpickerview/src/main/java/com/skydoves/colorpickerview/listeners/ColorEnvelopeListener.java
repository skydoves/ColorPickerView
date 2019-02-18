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

package com.skydoves.colorpickerview.listeners;

import com.skydoves.colorpickerview.ColorEnvelope;

/**
 * ColorEnvelopeListener is invoked whenever {@link com.skydoves.colorpickerview.ColorPickerView} is
 * triggered.
 */
public interface ColorEnvelopeListener extends ColorPickerViewListener {
  /**
   * invoked by {@link com.skydoves.colorpickerview.ColorPickerView}.
   *
   * @param envelope {@link ColorEnvelope}
   * @param fromUser triggered by the user(true) or not(false).
   */
  void onColorSelected(ColorEnvelope envelope, boolean fromUser);
}
