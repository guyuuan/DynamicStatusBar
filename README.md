# Dynamic Status Bar



Automatically modify the color of the android status bar icon.



## Usage

1. Add maven central to  your project

   ```groovy
    repositories {
           ...
           mavenCentral()
           ...
    }
   ```

2. Add the dependency

   ```groovy
    dependencies {
         implementation("cn.chitanda:dynamicstatusbar:1.0")
    }
   ```

3. Initialize after the activity starts

   ```kotlin
   override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           DynamicStatusBar.init(this)
   }
   ```

   

