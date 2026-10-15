# NiuLai project template

This is a project template for a greenfield Java project. It's named _NiuLai_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/niulai/NiuLai.java` file, right-click it, and choose `Run NiuLai.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   |\ | | |  | |     /\  |
   | \| | \__/ |___ /~~\ |
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Running the JavaFX GUI

From the project root, run:

```powershell
.\gradlew.bat run
```

The GUI supports the same commands as the command-line chatbot. Tasks are saved to `data\niulai.txt` as usual. To run the original command-line interface directly, run `niulai.NiuLai` from the IDE.

## Creating and running the fat JAR

This project uses the Gradle Shadow plugin to package the application and its runtime dependencies into one executable JAR file.

From the project root, run:

```powershell
.\gradlew.bat shadowJar
```

The generated file is `build\libs\niulai.jar`. Copy it into an empty folder, open a command window in that folder, and run:

```text
java -jar "niulai.jar"
```

The quotes are safe to keep and are required if the JAR filename contains spaces or characters such as `[`. The application stores its task data in a `data` folder relative to the folder from which the JAR is run.
