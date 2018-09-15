# ColorPickerView
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![API](https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=15)
[![Android Weekly](https://img.shields.io/badge/Android%20Weekly-%23316-orange.svg)](https://androidweekly.net/issues/issue-316)
[![Build Status](https://travis-ci.org/skydoves/ColorPickerView.svg?branch=master)](https://travis-ci.org/skydoves/ColorPickerView) <br>
You can use ColorPickerView just like ImageView and get HSV colors, ARGB values, Hex color codes <br>
from your gallery pictures or custom images by tapping on the desired color. <br>

![img0](https://user-images.githubusercontent.com/24237865/45309043-ccf51580-b55d-11e8-8985-02fc2d3a7250.jpg) 
![img1](https://user-images.githubusercontent.com/24237865/45308725-1db83e80-b55d-11e8-84b0-1d48c0405365.jpg)

## Including in your project
### Gradle
Add below codes to your **root** `build.gradle` file (not your module build.gradle file).
```gradle
allprojects {
    repositories {
        jcenter()
    }
}
```
And add a dependency code to your **module**'s `build.gradle` file.
```gradle
dependencies {
     implementation "com.github.skydoves:colorpickerview:2.0.0"
}
```

### or Maven
```xml
<dependency>
  <groupId>com.github.skydoves</groupId>
  <artifactId>colorpickerview</artifactId>
  <version>2.0.0</version>
</dependency>
```
    
## Usage
You can use like using just ImageView and you can get color from any images.

#### Add XML Namespace
First add below XML Namespace inside your XML layout file.

```gradle
xmlns:app="http://schemas.android.com/apk/res-auto"
```

#### ColorPickerView in layout
```xml
<com.skydoves.colorpickerview.ColorPickerView
        android:id="@+id/colorPickerView"
        android:layout_width="300dp"
        android:layout_height="300dp"
        app:palette="@drawable/palette"
        app:selector="@drawable/wheel" />
```

#### Attribute descriptions
```gradle
app:palette="@drawable/palette" // set palette image. Must be needed.
app:selector="@drawable/wheel" // set selector image. optional.
app:alpha_selector="0.8" // set selector's alpha. optional.
app:alpha_flag="0.8" // set flag's alpha. optional.
```

#### Color Selected Listener
You can listen to only an int value of a color by using `ColorListener`.
```java
colorPickerView.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(int color) {
                LinearLayout linearLayout = findViewById(R.id.linearLayout);
                linearLayout.setBackgroundColor(color);
            }
        });
```

#### ColorEnvelope Listener
Or you can listen to an instance has HSV color, hex color code, argb by using `ColorEnvelopeListener`.
```java
colorPickerView.setColorListener(new ColorEnvelopeListener() {
            @Override
            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                linearLayout.setBackgroundColor(envelope.getColor());
                textView.setText("#" + envelope.getHexCode());
            }
        });
```

#### ColorEnvelope
onColorSelected method receives a ColorEnvelope's instance from ColorPickerView. <br>
`ColorEnvelope` provides HSV color, hex color code, argb.
```java
colorEnvelope.getColor() // int
colorEnvelope.getHexCode() // String
colorEnvelope.getArgb() // int[4]
```

### AlphaSlideBar(Optional)
![alpha_slide](https://user-images.githubusercontent.com/24237865/45362228-43058500-b60f-11e8-9b13-0b2e01a892de.jpg) <br>
You can change the transparency value of a selected color by using AlphaSlideBar. <br>

#### AlphaSlideBar in layout
```xml
<com.skydoves.colorpickerview.sliders.AlphaSlideBar
        android:id="@+id/alphaSlideBar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:selector_AlphaSlideBar="@drawable/wheel" // set palette image. Must be needed.
        app:borderColor_AlphaSlideBar="@android:color/darker_gray" // set border color. optional.
        app:borderSize_AlphaSlideBar="5"/> // set border size. optional.
```
You can attach to ColorPickerView like below.

```java
final AlphaSlideBar alphaSlideBar = findViewById(R.id.alphaSlideBar);
colorPickerView.attachAlphaSlider(alphaSlideBar);
```

### BrightnessSlideBar(Optional)
![brigngtness_slide](https://user-images.githubusercontent.com/24237865/45362230-439e1b80-b60f-11e8-96ec-6907ab0ef678.jpg) <br>
You can change the brightness value of a selected color by using BrightnessSlideBar. <br>

#### BrightnessSlideBar in layout
```xml
<com.skydoves.colorpickerview.sliders.BrightnessSlideBar
        android:id="@+id/brightnessSlide"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="15dp"
        app:selector_BrightnessSlider="@drawable/wheel" // set palette image. Must be needed.
        app:borderColor_BrightnessSlider="@android:color/darker_gray" // set border color. optional.
        app:borderSize_BrightnessSlider="5"/> // set border size. optional.
```
You can attach to ColorPickerView like below.

```java
final BrightnessSlideBar brightnessSlideBar = findViewById(R.id.brightnessSlide);
colorPickerView.attachBrightnessSlider(brightnessSlideBar);
```

### ColorPickerDialog
![dialog0](https://user-images.githubusercontent.com/24237865/45362890-0d619b80-b611-11e8-857b-e12f82978b53.jpg) 
![dialog1](https://user-images.githubusercontent.com/24237865/45362892-0d619b80-b611-11e8-9cc5-25518a9d392a.jpg) <br>

Can be used just like using AlertDialog and provides colors from any images. <br>
It extends AlertDialog, so you can customizing themes also. <br>

```java
ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
builder.setTitle("ColorPicker Dialog");
builder.setFlagView(new CustomFlag(this, R.layout.layout_flag));
builder.setPositiveButton(getString(R.string.confirm), new ColorEnvelopeListener() {
    @Override
    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
        setLayoutColor(envelope);
    }
});
builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
    }
});
builder.attachAlphaSlideBar(); // attach AlphaSlideBar
builder.attachBrightnessSlideBar(); // attach BrightnessSlideBar
builder.show(); // show dialog
```

And you can get a ColorPickerView instance from `ColorPickerDialog.Builder`. <br> So you can customize ColorPickerDialog.
```java
ColorPickerView colorPickerView = builder.getColorPickerView();
colorPickerView.setPaletteDrawable(ContextCompat.getDrawable(this, R.drawable.palettebar));
```

### FlagView(Optional)
![flag0](https://user-images.githubusercontent.com/24237865/45364191-75fe4780-b614-11e8-81a5-04690a4392db.jpg) 
![flag1](https://user-images.githubusercontent.com/24237865/45364194-75fe4780-b614-11e8-844c-136d14c91560.jpg) <br><br>

`FlagView` lets you can show a flag above a selector. This is optional.<br>
First, create Flag layout as your taste like below. 
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="100dp"
    android:layout_height="40dp"
    android:background="@drawable/flag"
    android:orientation="horizontal">

    <LinearLayout
        android:id="@+id/flag_color_layout"
        android:layout_width="20dp"
        android:layout_height="20dp"
        android:layout_marginTop="6dp"
        android:layout_marginLeft="5dp"
        android:orientation="vertical"/>

    <TextView
        android:id="@+id/flag_color_code"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="6dp"
        android:layout_marginLeft="10dp"
        android:layout_marginRight="5dp"
        android:textSize="14dp"
        android:textColor="@android:color/white"
        android:maxLines="1"
        android:ellipsize="end"
        android:textAppearance="?android:attr/textAppearanceSmall"
        tools:text="#ffffff"/>
</LinearLayout>
```

Second, create CustomFlagView extending `FlagView`. This is an example code.
```java
public class CustomFlag extends FlagView {

    private TextView textView;
    private AlphaTileView alphaTileView;

    public CustomFlag(Context context, int layout) {
        super(context, layout);
        textView = findViewById(R.id.flag_color_code);
        alphaTileView = findViewById(R.id.flag_color_layout);
    }

    @Override
    public void onRefresh(ColorEnvelope colorEnvelope) {
        textView.setText("#" + colorEnvelope.getHexCode());
        alphaTileView.setPaintColor(colorEnvelope.getColor());
    }
}
```

The last, set FlagView on ColorPickerView or ColorPickerDialog.Builder.

```java
colorPickerView.setFlagView(new CustomFlag(this, R.layout.layout_flag));
```
```java
ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
builder.setFlagView(new CustomFlag(this, R.layout.layout_flag));
```

#### Modes
You can set FlagView's showing mode.
```java
colorPickerView.setFlagMode(FlagMode.ALWAYS); // showing always flagView
colorPickerView.setFlagMode(FlagMode.LAST); // showing flagView when touch Action_UP
```

### AlphaTileView
![alphatileview](https://user-images.githubusercontent.com/24237865/45364416-09377d00-b615-11e8-9707-b83f55053480.jpg) <br>
AlphaTileView presents layout color as ARGB.<br>
If you want to present ARGB color on general views, it will not be presented accurately.<br>
because it will be mixed with the parent view's background color.<br>
So if you want to show ARGB color accurately, should use AlphaTileView.<br>

```xml
<com.skydoves.colorpickerview.AlphaTileView
            android:id="@+id/alphaTileView"
            android:layout_width="55dp"
            android:layout_height="55dp"
            app:tileSize="20" // the size of the repeating tile
            app:tileEvenColor="@color/tile_even" // the color of even tiles
            app:tileOddColor="@color/tile_odd"/> // the color of odd tiles
```

### ColorPickerView Methods
Methods | Return | Description
--- | --- | ---
getColor() | int | the last selected color
getColorEnvelope() | ColorEnvelope | returns ColorEnvelope. It has the last selected Color, Hex, ARGB values
setPaletteDrawable(Drawable drawable) | void | change palette drawable resource
setSelectorDrawable(Drawable drawable) | void | change selector drawable resource
setSelectorPoint(int x, int y) | void | moving selector at point(x, y)
selectCenter() | void | select center of drawable image
setACTION\_UP(Boolean) | void | ColorListener only listening when ACTION\_UP.
setFlagView(FlagView flagview) | void | sets FlagView on ColorPickerView
setFlagMode(FlagMode flagMode) | void | sets FlagMode on ColorPickerView
setFlipable(boolean flipable) | void | sets FlagView be flipbed when go out the ColorPickerView
attachAlphaSlider | void | attach an AlphaSlider
attachBrightnessSlider | void | attach a BrightnessSlider

## Other Libraries
Other libraries released related to color picker!

### ColorPickerPreference
[A library](https://github.com/skydoves/ColorPickerPreference) that let you implement ColorPickerView, ColorPickerDialog, ColorPickerPreference. 

### Multi-ColorPickerView
You can get colors using multi selectors.<br>
At [here](https://github.com/skydoves/Multi-ColorPicker) you can get a more specialized library in multi-coloring.

![screenshot1128436220](https://user-images.githubusercontent.com/24237865/45586566-4614b400-b934-11e8-9098-2d4341dd695e.png)

# License
```xml
Copyright 2017 skydoves

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
