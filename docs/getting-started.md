# Getting Started

This guide covers the basics of setting up and using ColorPickerView in your Android application.

## Installation

Add the dependency below to your module's `build.gradle` file:

[![Maven Central](https://img.shields.io/maven-central/v/com.github.skydoves/colorpickerview.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.skydoves%22%20AND%20a:%22colorpickerview%22)

=== "Groovy"

    ```groovy
    dependencies {
        implementation("com.github.skydoves:colorpickerview:2.4.0")
    }
    ```

=== "Kotlin"

    ```kotlin
    dependencies {
        implementation("com.github.skydoves:colorpickerview:2.4.0")
    }
    ```

## XML Namespace

Add the following XML namespace inside your XML layout file to use custom attributes:

```xml
xmlns:app="http://schemas.android.com/apk/res-auto"
```

## Basic Setup

### XML Layout

You can simply use `ColorPickerView` by defining it in your XML layout files. This will initialize with the default HSV color palette and selector.

```xml
<com.skydoves.colorpickerview.ColorPickerView
    android:id="@+id/colorPickerView"
    android:layout_width="300dp"
    android:layout_height="300dp" />
```

### ColorListener

`ColorListener` is triggered when a user taps the `ColorPickerView` or when a position is selected programmatically.

=== "Kotlin"

    ```kotlin
    colorPickerView.setColorListener { color, fromUser ->
        linearLayout.setBackgroundColor(color)
    }
    ```

=== "Java"

    ```java
    colorPickerView.setColorListener(new ColorListener() {
        @Override
        public void onColorSelected(int color, boolean fromUser) {
            linearLayout.setBackgroundColor(color);
        }
    });
    ```

### ColorEnvelopeListener

`ColorEnvelopeListener` extends `ColorListener` and provides `ColorEnvelope` as a parameter, giving access to a variety of color values.

=== "Kotlin"

    ```kotlin
    colorPickerView.setColorListener(ColorEnvelopeListener { envelope, fromUser ->
        linearLayout.setBackgroundColor(envelope.color)
        textView.text = "#${envelope.hexCode}"
    })
    ```

=== "Java"

    ```java
    colorPickerView.setColorListener(new ColorEnvelopeListener() {
        @Override
        public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
            linearLayout.setBackgroundColor(envelope.getColor());
            textView.setText("#" + envelope.getHexCode());
        }
    });
    ```

## ColorEnvelope

`ColorEnvelope` is a versatile wrapper class for color models, offering a wide range of color-related data.

```kotlin
envelope.color    // returns an integer color
envelope.hexCode  // returns an ARGB hex code string (e.g., "FFFF5722")
envelope.rgbHexCode // returns an RGB hex code string without the alpha (e.g., "FF5722")
envelope.argb     // returns an ARGB integer array [alpha, red, green, blue]
```

## XML Attributes

You can customize the palette image, selector, and various options using XML attributes:

```xml
<com.skydoves.colorpickerview.ColorPickerView
    android:id="@+id/colorPickerView"
    android:layout_width="300dp"
    android:layout_height="300dp"
    app:palette="@drawable/palette"
    app:selector="@drawable/colorpickerview_wheel"
    app:selector_size="32dp"
    app:alpha_selector="0.8"
    app:alpha_flag="0.8"
    app:actionMode="last"
    app:initialColor="@color/colorPrimary"
    app:preferenceName="MyColorPicker"
    app:debounceDuration="200"
    app:zoomEnabled="true"
    app:maxZoom="5.0" />
```

| Attribute | Description |
|-----------|-------------|
| `palette` | Sets a custom palette image drawable |
| `selector` | Sets a custom selector image drawable |
| `selector_size` | Sets the width & height of the selector |
| `alpha_selector` | Sets the alpha of the selector (0.0-1.0) |
| `alpha_flag` | Sets the alpha of the flag view |
| `actionMode` | Sets action mode: `always` or `last` |
| `initialColor` | Sets an initial position using a specific color |
| `preferenceName` | Sets a preference name for state persistence |
| `debounceDuration` | Sets debounce duration in milliseconds |
| `selectorPointValidation` | Approximates the selected point to the nearest valid color |
| `resetBrightnessOnLowSaturation` | Resets the brightness slider on a low saturation color |
| `syncSlidersWithPaletteColor` | Moves the sliders to the alpha and the brightness of a picked pixel |
| `zoomEnabled` | Enables the pinch zoom gesture for a bitmap palette |
| `maxZoom` | Sets the maximum scale of the zoom gesture |

## Create using Builder

You can create a `ColorPickerView` instance programmatically using `ColorPickerView.Builder`:

=== "Kotlin"

    ```kotlin
    val colorPickerView = ColorPickerView.Builder(context)
        .setColorListener(colorListener)
        .setPreferenceName("MyColorPicker")
        .setActionMode(ActionMode.LAST)
        .setAlphaSlideBar(alphaSlideBar)
        .setBrightnessSlideBar(brightnessSlideBar)
        .setFlagView(CustomFlag(context, R.layout.layout_flag))
        .setPaletteDrawable(ContextCompat.getDrawable(context, R.drawable.palette))
        .setSelectorDrawable(ContextCompat.getDrawable(context, R.drawable.selector))
        .build()
    ```

=== "Java"

    ```java
    ColorPickerView colorPickerView = new ColorPickerView.Builder(context)
        .setColorListener(colorListener)
        .setPreferenceName("MyColorPicker")
        .setActionMode(ActionMode.LAST)
        .setAlphaSlideBar(alphaSlideBar)
        .setBrightnessSlideBar(brightnessSlideBar)
        .setFlagView(new CustomFlag(context, R.layout.layout_flag))
        .setPaletteDrawable(ContextCompat.getDrawable(context, R.drawable.palette))
        .setSelectorDrawable(ContextCompat.getDrawable(context, R.drawable.selector))
        .build();
    ```

## Initial Color

You can set an initial color to position the selector and slide bars. This works only with the default HSV palette.

=== "XML"

    ```xml
    app:initialColor="@color/colorPrimary"
    ```

=== "Kotlin"

    ```kotlin
    colorPickerView.setInitialColor(color)
    // or using a color resource
    colorPickerView.setInitialColorRes(R.color.colorPrimary)
    ```

!!! note

    If you set a preference name using `setPreferenceName`, the initial color will only be applied once (on first launch).
