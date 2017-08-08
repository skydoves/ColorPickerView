# ColorPickerView
You can use like just ImageView and you can get HSV colors, RGB values, Html color codes <br>
from your gallery pictures or custom images just using touch.

![screenshot0](https://cloud.githubusercontent.com/assets/24237865/23684747/011279de-03e4-11e7-8cb3-3d5271efedc6.jpg)
![screenshot1](https://cloud.githubusercontent.com/assets/24237865/23684824/42e77472-03e4-11e7-9f5e-a58b7708dfd8.jpg)


## Including in your project
#### build.gradle
```java
repositories {
  mavenCentral() // or jcenter() works as well
}

dependencies {
  compile 'com.github.skydoves:colorpickerview:1.0.5'
}
```

#### or Maven
```xml
<dependency>
  <groupId>com.github.skydoves</groupId>
  <artifactId>colorpickerview</artifactId>
  <version>1.0.5</version>
</dependency>
```
    
## Usage
You can use like using just ImageView and you can get color from any images.

#### Add XML Namespace
First add below XML Namespace inside your XML layout file.

```xml
xmlns:app="http://schemas.android.com/apk/res-auto"
```

#### ColorPickerView in layout
```xml
<com.skydoves.colorpickerview.ColorPickerView
        android:id="@+id/colorPickerView"
        android:layout_width="300dp"
        android:layout_height="300dp"
        app:src="@drawable/palette"
        app:selector="@drawable/wheel" />
```

#### Attribute description
```
app:src="@drawable/palette" // set palette image
```

```
app:selector="@drawable/wheel" // set selector image. This isn't required always. If you don't need, don't use.
```

#### Color Selected Listener
```java
colorPickerView.setColorListener(new ColorPickerView.ColorListener() {
            @Override
            public void onColorSelected(int color) {

            }
        });
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
