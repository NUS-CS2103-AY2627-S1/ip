# Cookie project template

This is a project template for a greenfield Java project named _Cookie_. Given below are instructions on how to use it.
See the [Cookie User Guide](docs/README.md) for the available task commands.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/cookie/Cookie.java` file, right-click it, and choose `Run Cookie.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
     ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
    ██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
    ██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗
    ██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝
    ╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
     ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building and running the cross-platform fat JAR

The project uses the Gradle Shadow plugin to package Cookie and its runtime dependencies into one executable JAR.
The JAR includes JavaFX native libraries for Windows x64, macOS Apple Silicon, and Linux x64.

From the project root, create the JAR with:

```powershell
.\gradlew.bat clean shadowJar
```

The generated fat JAR is located at:

```text
build\libs\cookie.jar
```

On macOS or Linux, use `./gradlew` in place of `.\gradlew.bat`. Run Cookie from the project root with:

```bash
java -jar build/libs/cookie.jar
```

Running it from the project root keeps Cookie's default `data\cookie.txt` storage path relative to this project. Enter commands such as `list` or `bye` in the console. The JAR can be rebuilt after source changes by running `shadowJar` again; `clean` is optional but ensures that the output is regenerated from scratch.
