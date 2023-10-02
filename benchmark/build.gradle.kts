import com.skydoves.colorpicker.Configuration

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
  id(libs.plugins.android.test.get().pluginId)
  id(libs.plugins.kotlin.android.get().pluginId)
  id(libs.plugins.baseline.profile.get().pluginId)
}

android {
  namespace = "com.skydoves.colorpickerview.benchmark"
  compileSdk = Configuration.compileSdk

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  kotlinOptions {
    jvmTarget = libs.versions.jvmTarget.get()
  }

  defaultConfig {
    minSdk = 24
    targetSdk = Configuration.targetSdk
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  targetProjectPath = ":app"

  testOptions.managedDevices.devices {
    maybeCreate<com.android.build.api.dsl.ManagedVirtualDevice>("pixel6api31").apply {
      device = "Pixel 6"
      apiLevel = 31
      systemImageSource = "aosp"
    }
  }
}

// This is the plugin configuration. Everything is optional. Defaults are in the
// comments. In this example, you use the GMD added earlier and disable connected devices.
baselineProfile {

  // This specifies the managed devices to use that you run the tests on. The default
  // is none.
  managedDevices += "pixel6api31"

  // This enables using connected devices to generate profiles. The default is true.
  // When using connected devices, they must be rooted or API 33 and higher.
  useConnectedDevices = false
}

dependencies {
  implementation(libs.androidx.test.runner)
  implementation(libs.androidx.test.uiautomator)
  implementation(libs.androidx.benchmark.macro)
  implementation(libs.androidx.profileinstaller)
}
