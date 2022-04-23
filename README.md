<h1 align="center">ColorPickerView</h1></br>
<p align="center">
🎨 ColorPickerView implements getting HSV colors, ARGB values, Hex color codes from any image drawables or your gallery pictures by tapping on the desired color. It also supports alpha & brightness slider bar, dialog, and saving & restoring selected data.
</p>
<br>

<p align="center">
  <a href="https://devlibrary.withgoogle.com/products/android/repos/skydoves-colorpickerview"><img alt="Google" src="https://skydoves.github.io/badges/google-devlib.svg"/></a><br>
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=15"><img alt="API" src="https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://github.com/skydoves/ColorPickerView/actions"><img alt="Build Status" src="https://github.com/skydoves/ColorPickerView/workflows/Android%20CI/badge.svg"/></a>
  <a href="https://androidweekly.net/issues/issue-316"><img alt="Android Weekly" src="https://skydoves.github.io/badges/android-weekly.svg"/></a>
  <a href="https://skydoves.github.io/libraries/colorpickerview/javadoc/"><img alt="Javadoc" src="https://skydoves.github.io/badges/javadoc-colorpicker.svg"/></a>
</p> <br>

<p align="center">
<img src="/art/art0.gif" width="31%"/>
<img src="/art/art1.gif" width="31%"/>
</p>

## Including in your project 
[![Maven Central](https://img.shields.io/maven-central/v/com.github.skydoves/colorpickerview.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.skydoves%22%20AND%20a:%22colorpickerview%22)

### Gradle 
Add below codes to your **root** `build.gradle` file (not your module build.gradle file).
```gradle
allprojects {
    repositories {
        mavenCentral()
    }
}
```
And add a dependency code to your **module**'s `build.gradle` file.
```gradle
dependencies {
    implementation "com.github.skydoves:colorpickerview:2.2.4"
}
```

## SNAPSHOT 
[![ColorPickerView](https://img.shields.io/static/v1?label=snapshot&message=ColorPickerView&logo=apache%20maven&color=C71A36)](https://oss.sonatype.org/content/repositories/snapshots/com/github/skydoves/colorpickerview/) <br>
Snapshots of the current development version of ColorPickerView are available, which track [the latest versions](https://oss.sonatype.org/content/repositories/snapshots/com/github/skydoves/colorpickerview/).
```Gradle
repositories {
   maven {
     url 'https://oss.sonatype.org/content/repositories/snapshots/'
   }
}
```

<img src="https://user-images.githubusercontent.com/24237865/53681606-38f75000-3d2f-11e9-8586-848d638f23b1.gif" align="right" width="30%">

## Table of Contents
#### [1. ColorPickerView](https://github.com/skydoves/ColorPickerView#usage)
- [ColorPickerView in layout](https://github.com/skydoves/ColorPickerView#colorpickerview-in-xml-layout)
- [ColorListener](https://github.com/skydoves/ColorPickerView#colorlistener)
- [Palette](https://github.com/skydoves/ColorPickerView#palette)
- [ActionMode](https://github.com/skydoves/ColorPickerView#actionmode)
- [Debounce](https://github.com/skydoves/ColorPickerView#debounce)
- [Create using builder](https://github.com/skydoves/ColorPickerView#create-using-builder)
- [Restore and save state](https://github.com/skydoves/ColorPickerView#restore-and-save)
- [Palette from Gallery](https://github.com/skydoves/ColorPickerView#palette-from-gallery) <br>

__[2. AlphaSlideBar](https://github.com/skydoves/ColorPickerView#alphaslidebar)__ <br>
__[3. BrightnessSlideBar](https://github.com/skydoves/ColorPickerView#brightnessslidebar)__<br>
__[4. ColorPickerDialog](https://github.com/skydoves/ColorPickerView#colorpickerdialog)__ <br>
__[5. FlagView](https://github.com/skydoves/ColorPickerView#flagview)__ <br>
__[6. AlphaTileView](https://github.com/skydoves/ColorPickerView#alphatileview)__ <br>
__[7. ColorPickerView Methods](https://github.com/skydoves/ColorPickerView#colorpickerview-methods)__ <br>
__[8. Other Libraries](https://github.com/skydoves/ColorPickerView#other-libraries)__ <br>

## Usage
Add following XML namespace inside your XML layout file.

```gradle
xmlns:app="http://schemas.android.com/apk/res-auto"
```

### ColorPickerView in XML layout
We can use `ColorPickerView` without any customized attributes.<br>
This `ColorPickerView` will be initialized with the default HSV color palette and default selector.
```gradle
<com.skydoves.colorpickerview.ColorPickerView
   android:id="@+id/colorPickerView"
   android:layout_width="300dp"
   android:layout_height="300dp" />
```

### Attribute descriptions
We can customize the palette image and selector or various options using the below attributes.
```gradle
app:palette="@drawable/palette" // sets a custom palette image.
app:selector="@drawable/wheel" // sets a custom selector image.
app:selector_size="32dp" // sets a width & height size of the selector.
app:alpha_selector="0.8" // sets an alpha of thr selector.
app:alpha_flag="0.8" // sets an alpha of the flag.
app:actionMode="last" // sets action mode 'always' or 'last'.
// set an initial position of the selector using a specific color. This attribute will work with only a default HSV palette.
app:initialColor="@color/colorPrimary"
app:preferenceName="MyColorPicker" // sets a preference name.
app:debounceDuration="200" // sets a debounce duration of the invoking color listener.
```

### ColorListener
`ColorListener` is invoked when tapped by a user or selected a position by a function.
```java
colorPickerView.setColorListener(new ColorListener() {
    @Override
    public void onColorSelected(int color, boolean fromUser) {
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setBackgroundColor(color);
    }
});
```

### ColorEnvelope
`ColorEnvelope` is a wrapper class of color models for providing more variety of color models.<br>
We can get HSV color value, Hex string code, ARGB value from the `ColorEnvelope`.
```java
colorEnvelope.getColor() // returns a integer color.
colorEnvelope.getHexCode() // returns a hex code string.
colorEnvelope.getArgb() // returns a argb integer array.
```

### ColorEnvelope Listener
`ColorEnvelopeListener` extends `ColorListener` and it provides `ColorEnvelope` as a parameter.
```java
colorPickerView.setColorListener(new ColorEnvelopeListener() {
    @Override
    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
        linearLayout.setBackgroundColor(envelope.getColor());
        textView.setText("#" + envelope.getHexCode());
    }
});
```

### Palette
If we do not set any customized palette, the default palette drawable is the `ColorHsvPalette`.<br>
We can move and select a point on the palette using a specific color using the below methods.
```java
colorPickerView.selectByHsvColor(color);
colorPickerView.selectByHsvColorRes(R.color.colorPrimary);
```
We can change the default palette as a desired image drawable using the below method.<br>
But if we change the palette using another drawable, we can not use the `selectByHsvColor` method.
```java
colorPickerView.setPaletteDrawable(drawable);
```
If we want to change back to the default palette, we can change it using the below method.
```java
colorPickerView.setHsvPaletteDrawable();
```

### ActionMode
`ActionMode` is an option restrict to invoke the `ColorListener` by user actions.
```java
colorPickerView.setActionMode(ActionMode.LAST); // ColorListener will be invoked when the finger is released.
```

### Debounce
Only emits color values to the listener if a particular timespan has passed without it emitting using `debounceDuration` attribute.
We can set the `debounceDuration` on our xml layout file.
```xml
app:debounceDuration="150"  
```
Or we can set it programmatically.
```java
colorPickerView.setDebounceDuration(150);
```

### Create using builder
This is how to create `ColorPickerView`'s instance using `ColorPickerView.Builder` class.
```java
ColorPickerView colorPickerView = new ColorPickerView.Builder(context)
      .setColorListener(colorListener)
      .setPreferenceName("MyColorPicker");
      .setActionMode(ActionMode.LAST)
      .setAlphaSlideBar(alphaSlideBar)
      .setBrightnessSlideBar(brightnessSlideBar)
      .setFlagView(new CustomFlag(context, R.layout.layout_flag))
      .setPaletteDrawable(ContextCompat.getDrawable(context, R.drawable.palette))
      .setSelectorDrawable(ContextCompat.getDrawable(context, R.drawable.selector))
      .build();
```

### Initial color
We can set an initial color and set positions of selector and slideBars based on the initial color.<br>
This function will work only with a default HSV palette.<br>
If we set preference name using the `setPreferenceName` method,  this function will work only once.
```gradle
app:initialColor="@color/colorPrimary"
```
Or we can use this method programmatically.
```java
.setInitialColor(color);
.setInitialColorRes(R.color.colorPrimary);
```

### Restore and save
This is how to restore the state of `ColorPickerView`.<br>
`setPreferenceName()` method restores all of the saved states (selector, color) automatically.

```java
colorPickerView.setPreferenceName("MyColorPicker");
```

This is how to save the states of `ColorPickerView`.<br>
`setLifecycleOwner()` method saves all of the states automatically when the lifecycleOwner is destroy.
```java
colorPickerView.setLifecycleOwner(this);
```
Or we can save the states manually using the below method.
```java
ColorPickerPreferenceManager.getInstance(this).saveColorPickerData(colorPickerView);
```

### Manipulate and clear
We can manipulate and clear the saved states using `ColorPickerPreferenceManager`.
```java
ColorPickerPreferenceManager manager = ColorPickerPreferenceManager.getInstance(this);
manager.setColor("MyColorPicker", Color.RED); // manipulates the saved color data.
manager.setSelectorPosition("MyColorPicker", new Point(120, 120)); // manipulates the saved selector's position data.
manager.clearSavedAllData(); // clears all of the states.
manager.clearSavedColor("MyColorPicker"); // clears only saved color data. 
manager.restoreColorPickerData(colorPickerView); // restores the saved states manually.
```

### Palette from Gallery
Here is how to get a bitmap drawable from the gallery image and set it to the palette.<br><br>
<img src="https://user-images.githubusercontent.com/24237865/52941911-313dc000-33ad-11e9-8264-6d78f4ad613a.jpg" align="left" width="35%">

Declare below permission on your `AndroidManifest.xml` file.
```gradle
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```
The below codes will start the Gallery and we can choose the desired image.
```java
Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
photoPickerIntent.setType("image/*");
startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
```
In the `onActivityResult`, we can get a bitmap drawable from the gallery and set it as the palette. And We can change the palette image of the `ColorPickerView` using the `setPaletteDrawable` method.
```java
try {
  final Uri imageUri = data.getData();
  final InputStream imageStream = getContentResolver().openInputStream(imageUri);
  final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
  Drawable drawable = new BitmapDrawable(getResources(), selectedImage);
  colorPickerView.setPaletteDrawable(drawable);
} catch (FileNotFoundException e) {
  e.printStackTrace();
}
```

## AlphaSlideBar
AlphaSlideBar changes the transparency of the selected color. <br><br>
<img src="https://user-images.githubusercontent.com/24237865/90913596-6ea66200-e417-11ea-893a-467e93189c2b.gif" align="left" width="31%">

`AlphaSlideBar` in XML layout
```gradle
<com.skydoves.colorpickerview.sliders.AlphaSlideBar
   android:id="@+id/alphaSlideBar"
   android:layout_width="match_parent"
   android:layout_height="wrap_content"
   app:selector_AlphaSlideBar="@drawable/wheel" // sets a customized selector drawable.
   app:borderColor_AlphaSlideBar="@android:color/darker_gray" // sets a color of the border.
   app:borderSize_AlphaSlideBar="5"/> // sets a size of the border.
```
We can attach and connect the `AlphaSlideBar` to our `ColorPickerView` using `attachAlphaSlider` method.

```java
AlphaSlideBar alphaSlideBar = findViewById(R.id.alphaSlideBar);
colorPickerView.attachAlphaSlider(alphaSlideBar);
```
We can make it vertically using the below attributes.
```gradle
android:layout_width="280dp" // width must set a specific width size.
android:layout_height="wrap_content"
android:rotation="90"
```

## BrightnessSlideBar
BrightnessSlideBar changes the brightness of the selected color. <br><br>
<img src="https://user-images.githubusercontent.com/24237865/90913583-6c440800-e417-11ea-8645-c5f6d1bf97df.gif" align="left" width="31%">

`BrightnessSlideBar` in XML layout
```gradle
<com.skydoves.colorpickerview.sliders.BrightnessSlideBar
   android:id="@+id/brightnessSlide"
   android:layout_width="match_parent"
   android:layout_height="wrap_content"
   app:selector_BrightnessSlider="@drawable/wheel" // sets a customized selector drawable.
   app:borderColor_BrightnessSlider="@android:color/darker_gray" // sets a color of the border.
   app:borderSize_BrightnessSlider="5"/> // sets a size of the border.
```
We can attach and connect the `BrightnessSlideBar` to our `ColorPickerView` using `attachBrightnessSlider` method.

```java
BrightnessSlideBar brightnessSlideBar = findViewById(R.id.brightnessSlide);
colorPickerView.attachBrightnessSlider(brightnessSlideBar);
```
We can make it vertically using the below attributes.
```gradle
android:layout_width="280dp" // width must set a specific width size.
android:layout_height="wrap_content"
android:rotation="90"
```

## ColorPickerDialog
![dialog0](https://user-images.githubusercontent.com/24237865/45362890-0d619b80-b611-11e8-857b-e12f82978b53.jpg) 
![dialog1](https://user-images.githubusercontent.com/24237865/45362892-0d619b80-b611-11e8-9cc5-25518a9d392a.jpg) <br>

`ColorPickerDialog` can be used just like an AlertDialog and it provides colors by tapping from any drawable. <br>

```java
new ColorPickerDialog.Builder(this)
      .setTitle("ColorPicker Dialog")
      .setPreferenceName("MyColorPickerDialog")
      .setPositiveButton(getString(R.string.confirm),
          new ColorEnvelopeListener() {
              @Override
              public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                  setLayoutColor(envelope);
              }
          })
       .setNegativeButton(getString(R.string.cancel),
          new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                  dialogInterface.dismiss();
              }
           })
      .attachAlphaSlideBar(true) // the default value is true.
      .attachBrightnessSlideBar(true)  // the default value is true.
      .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
      .show();
```

we can get an instance of `ColorPickerView` from the `ColorPickerView.Builder` and we can customize it. <br>
```java
ColorPickerView colorPickerView = builder.getColorPickerView();
colorPickerView.setFlagView(new CustomFlag(this, R.layout.layout_flag)); // sets a custom flagView
builder.show(); // shows the dialog
```

## FlagView
We can implement showing a `FlagView` above and below on the selector.<br>
This library provides `BubbleFlagView` by default as we can see the [previews](https://github.com/skydoves/ColorPickerView#colorpickerview).<br>
Here is the example code for implementing it.

```java
BubbleFlag bubbleFlag = new BubbleFlag(this);
bubbleFlag.setFlagMode(FlagMode.FADE);
colorPickerView.setFlagView(bubbleFlag);
```

We can also fully customize the `FlagView` like below.<br>
![flag0](https://user-images.githubusercontent.com/24237865/45364191-75fe4780-b614-11e8-81a5-04690a4392db.jpg) 
![flag1](https://user-images.githubusercontent.com/24237865/45364194-75fe4780-b614-11e8-844c-136d14c91560.jpg) <br>

First, We need a customized layout like below.
```gradle
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

Second, we need to create a class that extends `FlagView`. Here is an example code.
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

And last, set the `FlagView` to the `ColorPickerView` using the `setFlagView` method.

```java
colorPickerView.setFlagView(new CustomFlag(this, R.layout.layout_flag));
```

### FlagMode
`FlagMode` is an option to decides the visibility action of the `FlagView`.
```java
colorPickerView.setFlagMode(FlagMode.ALWAYS); // showing always by tapping and dragging.
colorPickerView.setFlagMode(FlagMode.LAST); // showing only when finger released.
```

## AlphaTileView
![alphatileview](https://user-images.githubusercontent.com/24237865/45364416-09377d00-b615-11e8-9707-b83f55053480.jpg) <br>
`AlphaTileView` visualizes ARGB colors over the view.<br>
If we need to represent ARGB colors on the general view, it will not be showing accurately. Because a color will be mixed with the parent view's background color. so if we need to represent ARGB colors accurately, we can use the `AlphaTileView`.

```gradle
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
getColor() | int | gets the last selected color.
getColorEnvelope() | ColorEnvelope | gets the `ColorEnvelope` of the last selected color.
setPaletteDrawable(Drawable drawable) | void | changes palette drawable manually.
setSelectorDrawable(Drawable drawable) | void | changes selector drawable manually.
setSelectorPoint(int x, int y) | void | selects the specific coordinate of the palette manually.
selectByHsvColor(@ColorInt int color) | void | changes selector's selected point by a specific color.
selectByHsvColorRes(@ColorRes int resource) | void | changes selector's selected point by a specific color using a color resource.
setHsvPaletteDrawable() | void | changes the palette drawable as the default drawable (ColorHsvPalette).
selectCenter() | void | selects the center of the palette manually.
setInitialColor(@ColorInt int color) | void | changes selector's selected point by a specific color initially.
setInitialColorRes(@ColorRes int resource) | void | changes selector's selected point by a specific color initially using a color resource.
setActionMode(ActionMode) | void | sets the color listener's trigger action mode.
setFlagView(FlagView flagView) | void | sets `FlagView` on `ColorPickerView`.
attachAlphaSlider | void | linking an `AlphaSlideBar` on the `ColorPickerView`.
attachBrightnessSlider | void | linking an `BrightnessSlideBar` on the `ColorPickerView`.

## Other Libraries
Here are other ColorPicker related libraries!

### ColorPickerPreference
[A library](https://github.com/skydoves/ColorPickerPreference) that let you implement ColorPickerView, ColorPickerDialog, ColorPickerPreference. 

### Multi-ColorPickerView
You can get colors using multi selectors.<br>
At [here](https://github.com/skydoves/Multi-ColorPicker) you can get a more specialized library on multi-coloring.

![screenshot1128436220](https://user-images.githubusercontent.com/24237865/45586566-4614b400-b934-11e8-9098-2d4341dd695e.png)

## Find this library useful? :heart:
Support it by joining [stargazers](https://github.com/skydoves/ColorPickerView/stargazers) for this repository. :star: <br>
And __[follow](https://github.com/skydoves)__ me for my next creations! 🤩

# License
```xml
Copyright 2017 skydoves (Jaewoong Eum)

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
