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
         implementation("cn.chitanda:dynamicstatusbar:2.0")
    }
   ```

3. Use ContentProvider to initialize the framework, you don't need to do other things, all activities can automatically change the status bar color

   

