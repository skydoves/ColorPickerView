# ColorPickerView

`ColorPickerView` is the core component that displays a color palette and allows users to select colors by tapping or dragging.

<p align="center">
  <img src="https://user-images.githubusercontent.com/24237865/53681606-38f75000-3d2f-11e9-8586-848d638f23b1.gif" width="280"/>
</p>

## Palette

### Default HSV Palette

If you do not set any custom palette, the default palette will be the `ColorHsvPalette`. You can manually select a specific point for the selector by specifying a color value:

```kotlin
colorPickerView.selectByHsvColor(color)
colorPickerView.selectByHsvColorRes(R.color.colorPrimary)
```

### Custom Palette

You can change the palette to any desired image drawable:

```kotlin
colorPickerView.setPaletteDrawable(drawable)
```

To revert to the default HSV palette:

```kotlin
colorPickerView.setHsvPaletteDrawable()
```

### Palette from Gallery

You can pick colors from gallery images by setting a bitmap drawable as the palette.

<img src="https://user-images.githubusercontent.com/24237865/52941911-313dc000-33ad-11e9-8264-6d78f4ad613a.jpg" width="280"/>

First, declare the permission in your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

Launch the gallery picker:

```kotlin
val photoPickerIntent = Intent(Intent.ACTION_PICK)
photoPickerIntent.type = "image/*"
startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY)
```

In `onActivityResult`, set the selected image as the palette:

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
        try {
            val imageUri = data?.data
            val imageStream = contentResolver.openInputStream(imageUri!!)
            val selectedImage = BitmapFactory.decodeStream(imageStream)
            val drawable = BitmapDrawable(resources, selectedImage)
            colorPickerView.setPaletteDrawable(drawable)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }
}
```

## ActionMode

`ActionMode` controls when the `ColorListener` is invoked based on user actions.

```kotlin
// Invoke listener only when finger is released
colorPickerView.setActionMode(ActionMode.LAST)

// Invoke listener on every touch event (default)
colorPickerView.setActionMode(ActionMode.ALWAYS)
```

| Mode | Description |
|------|-------------|
| `ALWAYS` | ColorListener is invoked on every touch event (down, move, up) |
| `LAST` | ColorListener is invoked only when the finger is released |

## Debounce

To limit how frequently the color listener is called, you can set a debounce duration:

=== "XML"

    ```xml
    app:debounceDuration="150"
    ```

=== "Kotlin"

    ```kotlin
    colorPickerView.setDebounceDuration(150)
    ```

This is useful for performance optimization when updating UI based on color changes.

## Selector

### Custom Selector Drawable

You can customize the selector appearance:

```kotlin
colorPickerView.setSelectorDrawable(drawable)
```

### Selector Position

Programmatically move the selector to a specific position:

```kotlin
colorPickerView.setSelectorPoint(x, y)
```

Or select the center of the palette:

```kotlin
colorPickerView.selectCenter()
```

## Zoom

A bitmap palette can be zoomed by a pinch gesture, which helps to pick tiny color spots of an image.
The gesture is disabled by default and it doesn't affect the default HSV palette.

=== "Kotlin"

    ```kotlin
    colorPickerView.setPaletteDrawable(drawable)
    colorPickerView.isZoomEnabled = true
    colorPickerView.maxZoom = 5.0f // the default value is 3.0f.
    ```

=== "Java"

    ```java
    colorPickerView.setPaletteDrawable(drawable);
    colorPickerView.setZoomEnabled(true);
    colorPickerView.setMaxZoom(5.0f); // the default value is 3.0f.
    ```

A pinch gesture zooms the palette in and out and a two finger drag moves the zoomed palette,
while a single touch still selects a color. `resetZoom()` restores the original scale and position.

## Methods Reference

| Method | Return | Description |
|--------|--------|-------------|
| `getColor()` | `Int` | Gets the last selected color |
| `getColorEnvelope()` | `ColorEnvelope` | Gets the ColorEnvelope of the last selected color |
| `setPaletteDrawable(Drawable)` | `void` | Changes the palette drawable |
| `setSelectorDrawable(Drawable)` | `void` | Changes the selector drawable |
| `setSelectorPoint(Int, Int)` | `void` | Selects a specific coordinate on the palette |
| `selectByHsvColor(Int)` | `void` | Moves selector to match a specific color |
| `selectByHsvColorRes(Int)` | `void` | Moves selector using a color resource |
| `setHsvPaletteDrawable()` | `void` | Resets to default HSV palette |
| `selectCenter()` | `void` | Selects the center of the palette |
| `setInitialColor(Int)` | `void` | Sets initial selector position by color |
| `setInitialColorRes(Int)` | `void` | Sets initial selector position by color resource |
| `setActionMode(ActionMode)` | `void` | Sets the listener trigger mode |
| `setFlagView(FlagView)` | `void` | Sets a FlagView on the ColorPickerView |
| `attachAlphaSlider(AlphaSlideBar)` | `void` | Links an AlphaSlideBar |
| `attachBrightnessSlider(BrightnessSlideBar)` | `void` | Links a BrightnessSlideBar |
| `setEnabled(Boolean)` | `void` | Enables or disables the color picker |
| `setDebounceDuration(Long)` | `void` | Sets the debounce duration in milliseconds |
| `setSelectorPointValidation(Boolean)` | `void` | Approximates the selected point to the nearest valid color (default: true) |
| `setResetBrightnessOnLowSaturation(Boolean)` | `void` | Resets the brightness slider on a low saturation color (default: true) |
| `setSyncSlidersWithPaletteColor(Boolean)` | `void` | Moves the sliders to the alpha and the brightness of a picked pixel (default: true) |
| `setZoomEnabled(Boolean)` | `void` | Enables the pinch zoom gesture for a bitmap palette (default: false) |
| `setMaxZoom(Float)` | `void` | Sets the maximum scale of the zoom gesture (default: 3.0) |
| `getZoomScale()` | `Float` | Gets the current scale of the zoomed palette |
| `resetZoom()` | `void` | Restores the zoomed palette to its original scale and position |

## Lifecycle Management

To properly save and restore the color picker state, set a lifecycle owner:

```kotlin
colorPickerView.setLifecycleOwner(this)
```

This automatically saves the state when the activity or fragment is destroyed.

!!! warning "Important"

    Always set the lifecycle owner to prevent state loss and ensure proper cleanup.
