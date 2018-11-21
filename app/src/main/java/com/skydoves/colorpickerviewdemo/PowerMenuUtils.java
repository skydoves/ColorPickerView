package com.skydoves.colorpickerviewdemo;

import androidx.lifecycle.LifecycleOwner;
import android.content.Context;
import android.graphics.Color;

import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

/**
 * Developed by skydoves on 2018-09-11.
 * Copyright (c) 2018 skydoves rights reserved.
 */

public class PowerMenuUtils {

    public static PowerMenu getPowerMenu(Context context, LifecycleOwner lifecycleOwner, OnMenuItemClickListener<PowerMenuItem> onMenuItemClickListener) {
        return new PowerMenu.Builder(context)
                .addItem(new PowerMenuItem("Palette", false))
                .addItem(new PowerMenuItem("Selector", false))
                .addItem(new PowerMenuItem("Dialog", false))
                .setLifecycleOwner(lifecycleOwner)
                .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT)
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
