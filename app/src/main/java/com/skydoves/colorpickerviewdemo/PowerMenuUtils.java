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

package com.skydoves.colorpickerviewdemo;

import android.content.Context;
import android.graphics.Color;
import androidx.lifecycle.LifecycleOwner;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.MenuEffect;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

@SuppressWarnings("WeakerAccess")
public class PowerMenuUtils {
  public static PowerMenu getPowerMenu(
      Context context,
      LifecycleOwner lifecycleOwner,
      OnMenuItemClickListener<PowerMenuItem> onMenuItemClickListener) {
    return new PowerMenu.Builder(context)
        .setHeaderView(R.layout.layout_header)
        .addItem(new PowerMenuItem("Palette", false))
        .addItem(new PowerMenuItem("Palette(Gallery)", false))
        .addItem(new PowerMenuItem("Selector", false))
        .addItem(new PowerMenuItem("Dialog", false))
        .setLifecycleOwner(lifecycleOwner)
        .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT)
        .setMenuEffect(MenuEffect.BODY)
        .setMenuRadius(10f)
        .setMenuShadow(10f)
        .setTextColor(context.getResources().getColor(R.color.md_grey_800))
        .setSelectedEffect(false)
        .setShowBackground(false)
        .setMenuColor(Color.WHITE)
        .setOnMenuItemClickListener(onMenuItemClickListener)
        .build();
  }
}
