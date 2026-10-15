# Dingleberry project template

This is a project template for a greenfield Java project named Dingleberry. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/dingleberry/Dingleberry.java` file, right-click it, and choose `Run Dingleberry.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    ____  _              _      _                     
   |  _ \(_)_ __   __ _ | | ___| |__   ___ _ __ _ __ _   _
   | | | | | '_ \ / _` || |/ _ \ '_ \ / _ \ '__| '__| | | |
   | |_| | | | | | (_| || |  __/ |_) |  __/ |  | |  | |_| |
   |____/|_|_| |_|\__, ||_|\___|_.__/ \___|_|  |_|   \__, |
                  |___/                              |___/    
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building and running the fat JAR

The project uses the Shadow Gradle plugin to package the application classes and
runtime dependencies into one executable JAR file.

1. Open PowerShell in the project directory.
1. Make sure `JAVA_HOME` points to a JDK 25 installation.
1. Build the fat JAR:

   ```powershell
   .\gradlew.bat shadowJar
   ```

The resulting file is `build\libs\dingleberry.jar`. The `shadowJar` task includes
the `dingleberry.Dingleberry` main class in the JAR manifest, so it can be run
without manually assembling a classpath:

```powershell
java -jar build\libs\dingleberry.jar
```

The application reads commands from standard input. For example, type `list`
or `bye` after starting the JAR. Re-run `shadowJar` after source changes to
create an updated JAR.

Useful command examples:

- `list` shows all tasks in the current list.
- `find <keyword>` shows only tasks whose description contains `<keyword>`.
- `bye` exits the application.

## Attributions

See [ATTRIBUTIONS.md](ATTRIBUTIONS.md) for reused template material, generated
artwork, tutorials, and third-party tooling.
