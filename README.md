# ColorPickerView
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://travis-ci.org/skydoves/ColorPickerView.svg?branch=master)](https://travis-ci.org/skydoves/ColorPickerView)
[![Android Weekly](https://img.shields.io/badge/Android%20Weekly-%23316-orange.svg)](https://androidweekly.net/issues/issue-316)<br>
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
You can listen to only an int value of color by using **ColorListener**.
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
Or you can listen to an instance has HSV color, hex color code, argb by using **ColorEnvelopeListener**.
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
ColorEnvelope provides HSV color, hex color code, argb.
```java
colorEnvelope.getColor() // int
colorEnvelope.getHexCode() // String
colorEnvelope.getArgb() // int[4]
```

#### Methods
Methods | Return | Description
--- | --- | ---
getColor() | int | the last selected color
getColorHtml() | String | the last selected Html color code
getColorRGB() | int[3] | the last selected color's RGB value.<br> int[0] : R, int[1] : G, int[2] : B
setPaletteDrawable(Drawable drawable) | void | change palette drawable resource
setSelectorDrawable(Drawable drawable) | void | change selector drawable resource
setSelectorPoint(int x, int y) | void | moving selector at point(x, y)
selectCenter() | void | select center of drawable image
setACTION\_UP(Boolean) | void | ColorListener only listening when ACTION\_UP.

## Other Libraries
Other libraries released related to color picker!

### ColorPickerPreference
[A library](https://github.com/skydoves/ColorPickerPreference) that let you implement ColorPickerView, ColorPickerDialog, ColorPickerPreference. 

### Multi-ColorPickerView
You can get colors using multi selectors.<br>
At [here](https://github.com/skydoves/Multi-ColorPicker) you can get a more specialized library in multi-coloring.

![111](https://user-images.githubusercontent.com/24237865/31853730-9bb0ecfe-b6c8-11e7-9730-c16095042c1a.jpg)

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
