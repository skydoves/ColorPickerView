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

package com.skydoves.colorpickerview.kotlin

import android.content.Context
import com.skydoves.colorpickerview.ColorPickerDialog

@DslMarker
internal annotation class ColorPickerDsl

/**
 * Creates a lambda scope for implementing [ColorPickerDialog] using its [ColorPickerDialog.Builder].
 *
 * @param block lambda scope for receiving [ColorPickerDialog.Builder].
 * @return new instance of [ColorPickerDialog].
 */
@JvmSynthetic
@ColorPickerDsl
inline fun Context.colorPickerDialog(block: ColorPickerDialog.Builder.() -> Unit) =
  ColorPickerDialog.Builder(this).apply(block)
