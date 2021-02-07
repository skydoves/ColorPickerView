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

package com.skydoves.colorpickerviewdemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.flag.BubbleFlag;
import com.skydoves.colorpickerview.flag.FlagMode;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.sliders.AlphaSlideBar;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import java.io.FileNotFoundException;
import java.io.InputStream;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

  private ColorPickerView colorPickerView;

  private boolean FLAG_PALETTE = false;
  private boolean FLAG_SELECTOR = false;

  private PowerMenu powerMenu;
  private final OnMenuItemClickListener<PowerMenuItem> powerMenuItemClickListener =
      new OnMenuItemClickListener<PowerMenuItem>() {
        @Override
        public void onItemClick(int position, PowerMenuItem item) {
          switch (position) {
            case 1:
              palette();
              break;
            case 2:
              paletteFromGallery();
              break;
            case 3:
              selector();
              break;
            case 4:
              dialog();
              break;
          }
          powerMenu.dismiss();
        }
      };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Timber.plant(new Timber.DebugTree());

    powerMenu = PowerMenuUtils.getPowerMenu(this, this, powerMenuItemClickListener);

    colorPickerView = findViewById(R.id.colorPickerView);
    BubbleFlag bubbleFlag = new BubbleFlag(this);
    bubbleFlag.setFlagMode(FlagMode.FADE);
    colorPickerView.setFlagView(bubbleFlag);
    colorPickerView.setColorListener(
        (ColorEnvelopeListener)
            (envelope, fromUser) -> {
              Timber.d("color: %s", envelope.getHexCode());
              setLayoutColor(envelope);
            });

    // attach alphaSlideBar
    final AlphaSlideBar alphaSlideBar = findViewById(R.id.alphaSlideBar);
    colorPickerView.attachAlphaSlider(alphaSlideBar);

    // attach brightnessSlideBar
    final BrightnessSlideBar brightnessSlideBar = findViewById(R.id.brightnessSlide);
    colorPickerView.attachBrightnessSlider(brightnessSlideBar);
    colorPickerView.setLifecycleOwner(this);
  }

  /**
   * set layout color & textView html code
   *
   * @param envelope ColorEnvelope by ColorEnvelopeListener
   */
  @SuppressLint("SetTextI18n")
  private void setLayoutColor(ColorEnvelope envelope) {
    TextView textView = findViewById(R.id.textView);
    textView.setText("#" + envelope.getHexCode());

    AlphaTileView alphaTileView = findViewById(R.id.alphaTileView);
    alphaTileView.setPaintColor(envelope.getColor());
  }

  /** shows the popup menu for changing options.. */
  public void overflowMenu(View view) {
    powerMenu.showAsAnchorLeftTop(view);
  }

  /** changes palette image using drawable resource. */
  private void palette() {
    if (FLAG_PALETTE) {
      colorPickerView.setHsvPaletteDrawable();
    } else {
      colorPickerView.setPaletteDrawable(ContextCompat.getDrawable(this, R.drawable.palettebar));
    }
    FLAG_PALETTE = !FLAG_PALETTE;
  }

  /** changes palette image from a gallery image. */
  private void paletteFromGallery() {
    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
    photoPickerIntent.setType("image/*");
    startActivityForResult(photoPickerIntent, 1000);
  }

  /** changes selector image using drawable resource. */
  private void selector() {
    if (FLAG_SELECTOR) {
      colorPickerView.setSelectorDrawable(ContextCompat.getDrawable(this, R.drawable.wheel));
    } else {
      colorPickerView.setSelectorDrawable(ContextCompat.getDrawable(this, R.drawable.wheel_dark));
    }
    FLAG_SELECTOR = !FLAG_SELECTOR;
  }

  /** shows ColorPickerDialog */
  private void dialog() {
    ColorPickerDialog.Builder builder =
        new ColorPickerDialog.Builder(this)
            .setTitle("ColorPicker Dialog")
            .setPreferenceName("Test")
            .setPositiveButton(
                getString(R.string.confirm),
                (ColorEnvelopeListener) (envelope, fromUser) -> setLayoutColor(envelope))
            .setNegativeButton(
                getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.dismiss());
    builder.getColorPickerView().setFlagView(new BubbleFlag(this));
    builder.show();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // user choose a picture from gallery
    if (requestCode == 1000 && resultCode == RESULT_OK) {
      try {
        final Uri imageUri = data.getData();
        if (imageUri != null) {
          final InputStream imageStream = getContentResolver().openInputStream(imageUri);
          final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
          Drawable drawable = new BitmapDrawable(getResources(), selectedImage);
          colorPickerView.setPaletteDrawable(drawable);
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onBackPressed() {
    if (powerMenu.isShowing()) {
      powerMenu.dismiss();
    } else {
      super.onBackPressed();
    }
  }
}
