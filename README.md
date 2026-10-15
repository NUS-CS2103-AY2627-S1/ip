# Duke project template

This is a project template for a greenfield Java project. It's named after the Java mascot _Duke_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Duke.java` file, right-click it, and choose `Run Duke.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    ____        _        
   |  _ \ _   _| | _____ 
   | | | | | | | |/ / _ \
   | |_| | |_| |   <  __/
   |____/ \__,_|_|\_\___|
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating and running a fat JAR

The project uses the Shadow Gradle plugin to package Rocky and its runtime dependencies into one runnable JAR file. Use JDK 25 and run these commands from the project root:

```bash
source ~/.sdkman/bin/sdkman-init.sh
sdk use java 25.0.3.fx-zulu
./gradlew shadowJar
```

The generated fat JAR is located at `build/libs/rocky-all.jar`. Run it with:

```bash
java -jar build/libs/rocky-all.jar
```

Run the JAR from the project root if you want Rocky to use the default task file at `./data/rocky.txt`. On Windows, use `gradlew.bat shadowJar` to create the JAR.
