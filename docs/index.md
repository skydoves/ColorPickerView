# Overview

<p align="center">
  <img src="https://github.com/skydoves/ColorPickerView/raw/main/art/art0.gif" width="280"/>
  <img src="https://github.com/skydoves/ColorPickerView/raw/main/art/art1.gif" width="280"/>
</p>

ColorPickerView enables you to obtain HSV colors, ARGB values, and Hex color codes from image drawables or your gallery pictures with a simple tap on the desired color. It offers additional features such as alpha and brightness slider bars, dialog support, and the ability to save and restore selected data.

## Key Features

- **HSV Color Palette**: Default HSV color wheel with full color spectrum
- **Custom Palettes**: Use any drawable image as a color palette
- **Color Values**: Get colors as integer, hex code, or ARGB array
- **Alpha Slider**: Adjust transparency with AlphaSlideBar
- **Brightness Slider**: Control brightness with BrightnessSlideBar
- **Dialog Support**: Built-in ColorPickerDialog for quick integration
- **FlagView**: Display selected color information with customizable flags
- **State Persistence**: Save and restore color picker states automatically
- **Gallery Integration**: Pick colors from gallery images

## Who's using ColorPickerView?

ColorPickerView is used by thousands of Android applications worldwide for color selection functionality.

!!! note "Featured on Google Dev Library"

    ColorPickerView is featured on the [Google Dev Library](https://devlibrary.withgoogle.com/products/android/repos/skydoves-colorpickerview), recognized for its quality and usefulness in the Android development community.

## Quick Start

Add the dependency below to your module's `build.gradle` file:

[![Maven Central](https://img.shields.io/maven-central/v/com.github.skydoves/colorpickerview.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.skydoves%22%20AND%20a:%22colorpickerview%22)

=== "Groovy"

    ```groovy
    dependencies {
        implementation("com.github.skydoves:colorpickerview:2.5.0")
    }
    ```

=== "Kotlin"

    ```kotlin
    dependencies {
        implementation("com.github.skydoves:colorpickerview:2.5.0")
    }
    ```

## Basic Example

### XML Layout

```xml
<com.skydoves.colorpickerview.ColorPickerView
    android:id="@+id/colorPickerView"
    android:layout_width="300dp"
    android:layout_height="300dp" />
```

### Kotlin/Java

```kotlin
val colorPickerView = findViewById<ColorPickerView>(R.id.colorPickerView)
colorPickerView.setColorListener(ColorEnvelopeListener { envelope, fromUser ->
    // Get color values
    val color = envelope.color
    val hexCode = envelope.hexCode
    val argb = envelope.argb
})
```

## ColorPicker Compose

If you're looking to implement a color picker in your Jetpack Compose project, you can use [colorpicker-compose](https://github.com/skydoves/colorpicker-compose) instead.

## Preview Gallery

<p align="center">
  <img src="https://user-images.githubusercontent.com/24237865/53681606-38f75000-3d2f-11e9-8586-848d638f23b1.gif" width="280"/>
</p>

| AlphaSlideBar | BrightnessSlideBar |
|:-------------:|:------------------:|
| <img src="https://user-images.githubusercontent.com/24237865/90913596-6ea66200-e417-11ea-893a-467e93189c2b.gif" width="100%"/> | <img src="https://user-images.githubusercontent.com/24237865/90913583-6c440800-e417-11ea-8645-c5f6d1bf97df.gif" width="100%"/> |

| ColorPickerDialog | Custom FlagView |
|:-----------------:|:---------------:|
| <img src="https://user-images.githubusercontent.com/24237865/45362890-0d619b80-b611-11e8-857b-e12f82978b53.jpg" width="100%"/> | <img src="https://user-images.githubusercontent.com/24237865/45364191-75fe4780-b614-11e8-81a5-04690a4392db.jpg" width="100%"/> |
