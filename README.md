# PotatoBot

PotatoBot is a Java command-line application for managing tasks.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. Locate `src/main/java/potatobot/PotatoBot.java`, right-click it, and choose `Run PotatoBot.main()`. If the code editor shows compilation errors, try restarting the IDE.

**Warning:** Keep the `src/main/java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files outside this folder path), as this is the standard Java source location expected by build tools.

## Building and running the fat JAR

The Shadow plugin packages the application and all runtime dependencies into one executable JAR. Run the following command from the project root:

```shell
./gradlew shadowJar
```

On Windows, use:

```bat
gradlew.bat shadowJar
```

The generated file is `build/libs/potatobot.jar`. Run it with Java 25:

```shell
java -jar build/libs/potatobot.jar
```

Use `./gradlew clean shadowJar` when you want Gradle to remove previous build output before creating the JAR again.
