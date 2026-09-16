# AlphaTileView

`AlphaTileView` is a custom view that displays ARGB colors with a checkered tile background, making transparent colors visible and distinguishable.

<img src="https://user-images.githubusercontent.com/24237865/45364416-09377d00-b615-11e8-9707-b83f55053480.jpg" width="280"/>

## Why Use AlphaTileView?

When displaying colors with transparency on a regular view, the alpha channel mixes with the parent view's background color, making it difficult to see the actual color. `AlphaTileView` solves this by showing colors against a checkered pattern (similar to design tools like Photoshop).

| Regular View | AlphaTileView |
|:------------:|:-------------:|
| Transparent colors blend with background | Transparent colors shown against checkered pattern |

## XML Layout

```xml
<com.skydoves.colorpickerview.AlphaTileView
    android:id="@+id/alphaTileView"
    android:layout_width="55dp"
    android:layout_height="55dp"
    app:tileSize="20"
    app:tileEvenColor="@color/tile_even"
    app:tileOddColor="@color/tile_odd" />
```

## XML Attributes

| Attribute | Description | Default |
|-----------|-------------|---------|
| `tileSize` | The size of each repeating tile in dp | 10 |
| `tileEvenColor` | The color of even tiles | White |
| `tileOddColor` | The color of odd tiles | Light Gray |
| `tileStrokeColor` | The color of the stroke, which is drawn on the edge | Transparent |
| `tileStrokeSize` | The size of the stroke, which is drawn on the edge | 0dp |

## Setting the Color

Set the display color programmatically:

=== "Kotlin"

    ```kotlin
    val alphaTileView = findViewById<AlphaTileView>(R.id.alphaTileView)
    alphaTileView.setPaintColor(Color.argb(128, 255, 0, 0)) // Semi-transparent red
    ```

=== "Java"

    ```java
    AlphaTileView alphaTileView = findViewById(R.id.alphaTileView);
    alphaTileView.setPaintColor(Color.argb(128, 255, 0, 0)); // Semi-transparent red
    ```

## Stroke

A stroke separates the painted color from the background when both of them have the same color:

=== "Kotlin"

    ```kotlin
    alphaTileView.setStrokeColor(Color.DKGRAY)
    alphaTileView.setStrokeSize(4f) // in pixels, setStrokeSizeRes takes a dimension resource.
    ```

=== "Java"

    ```java
    alphaTileView.setStrokeColor(Color.DKGRAY);
    alphaTileView.setStrokeSize(4f); // in pixels, setStrokeSizeRes takes a dimension resource.
    ```

## With ColorEnvelope

Use `AlphaTileView` with `ColorEnvelopeListener` to display the selected color:

```kotlin
colorPickerView.setColorListener(ColorEnvelopeListener { envelope, fromUser ->
    alphaTileView.setPaintColor(envelope.color)
    textView.text = "#${envelope.hexCode}"
})
```

## Custom Tile Colors

Create custom tile colors for different visual styles:

```xml
<!-- In colors.xml -->
<color name="tile_light">#FFFFFF</color>
<color name="tile_dark">#CCCCCC</color>
```

```xml
<com.skydoves.colorpickerview.AlphaTileView
    android:id="@+id/alphaTileView"
    android:layout_width="60dp"
    android:layout_height="60dp"
    app:tileSize="12"
    app:tileEvenColor="@color/tile_light"
    app:tileOddColor="@color/tile_dark" />
```

## Use Cases

### Color Preview

Display the currently selected color with proper alpha visualization:

```xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="16dp">

    <com.skydoves.colorpickerview.AlphaTileView
        android:id="@+id/colorPreview"
        android:layout_width="48dp"
        android:layout_height="48dp"
        app:tileSize="8" />

    <TextView
        android:id="@+id/hexCode"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_gravity="center_vertical" />

</LinearLayout>
```

### In Custom FlagView

Use `AlphaTileView` inside a custom `FlagView` to show accurate color previews:

```kotlin
class CustomFlag(context: Context, layout: Int) : FlagView(context, layout) {

    private val alphaTileView: AlphaTileView = findViewById(R.id.flag_color_preview)
    private val hexCodeText: TextView = findViewById(R.id.flag_hex_code)

    override fun onRefresh(colorEnvelope: ColorEnvelope) {
        alphaTileView.setPaintColor(colorEnvelope.color)
        hexCodeText.text = "#${colorEnvelope.hexCode}"
    }
}
```

### Color History/Palette

Create a row of color swatches that properly display transparency:

```xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal">

    <com.skydoves.colorpickerview.AlphaTileView
        android:id="@+id/color1"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_margin="4dp" />

    <com.skydoves.colorpickerview.AlphaTileView
        android:id="@+id/color2"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_margin="4dp" />

    <com.skydoves.colorpickerview.AlphaTileView
        android:id="@+id/color3"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_margin="4dp" />

</LinearLayout>
```

## Complete Example

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val colorPickerView = findViewById<ColorPickerView>(R.id.colorPickerView)
        val alphaSlideBar = findViewById<AlphaSlideBar>(R.id.alphaSlideBar)
        val alphaTileView = findViewById<AlphaTileView>(R.id.alphaTileView)
        val hexCodeText = findViewById<TextView>(R.id.hexCodeText)
        val argbText = findViewById<TextView>(R.id.argbText)

        // Attach alpha slider to see transparency effect
        colorPickerView.attachAlphaSlider(alphaSlideBar)

        colorPickerView.setColorListener(ColorEnvelopeListener { envelope, fromUser ->
            // Update the AlphaTileView with the selected color
            alphaTileView.setPaintColor(envelope.color)

            // Update text displays
            hexCodeText.text = "#${envelope.hexCode}"
            val argb = envelope.argb
            argbText.text = "A:${argb[0]} R:${argb[1]} G:${argb[2]} B:${argb[3]}"
        })
    }
}
```

## Tips

!!! tip "Tile Size"

    Smaller tile sizes create a finer checkered pattern, which can be better for small preview areas. Larger tiles work well for bigger preview areas.

!!! tip "Contrast"

    Choose tile colors with enough contrast to make the checkered pattern visible but not distracting. Light gray and white is a common combination.
