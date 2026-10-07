# Drax

This is a project template for a greenfield Java project. The chatbot is named _Drax_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/drax.Drax.java` file, right-click it, and choose `Run drax.Drax.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```text
   ██████╗ ██████╗  █████╗ ██╗  ██╗
   ██╔══██╗██╔══██╗██╔══██╗╚██╗██╔╝
   ██║  ██║██████╔╝███████║ ╚███╔╝
   ██║  ██║██╔══██╗██╔══██║ ██╔██╗
   ██████╔╝██║  ██║██║  ██║██╔╝ ██╗
   ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Running the JavaFX interface

Use the Zulu FX JDK `25.0.3.fx-zulu` for both the project SDK and Gradle JVM.
On macOS with SDKMAN, select it with `sdk use java 25.0.3.fx-zulu`, then run `./gradlew run`.
Gradle's Java launch tasks enable native access for `javafx.graphics` so JavaFX can load its native libraries without warnings.
Reload the Gradle project in IntelliJ after changing `build.gradle`.

If you run `drax.Main` or `drax.Launcher` using an IntelliJ Application configuration instead of Gradle,
add `--enable-native-access=javafx.graphics` to that configuration's **VM options** (not program arguments).

The project uses JavaFX 25.0.1, matching the Java 25 runtime bundled with the Zulu FX JDK.
The FXML files declare JavaFX 25; keep this declaration when saving the views in Scene Builder.
