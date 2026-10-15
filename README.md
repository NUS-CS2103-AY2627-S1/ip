# PeanutButterCat

PeanutButterCat is a cozy, snack-powered task keeper that stores your tasks in its imaginary task jar. Its warm
peanut-butter palette, illustrated cat avatar, and playful phrases give the chatbot a friendly, consistent identity.
Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/peanutbuttercat/PeanutButterCat.java` file, right-click it, and choose `Run PeanutButterCat.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   ____________________________________________________________
    /\_/\
   ( o.o )  PeanutButterCat
    > u <
   Hello! I'm PeanutButterCat, your cozy, snack-powered task keeper.
   Tell me what's on your plate, and I'll tuck it into the task jar.
   ____________________________________________________________
   bye
   ____________________________________________________________
   The task jar is safe with me. Stay smooth, and see you soon!
   ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Build and run the executable JAR

This project uses the Gradle Shadow plugin to package the application and its runtime dependencies into one executable (fat) JAR.

From the project root, use JDK 25 and run:

```powershell
.\gradlew.bat shadowJar
```

The generated file is:

```text
build\libs\peanutbuttercat.jar
```

Run it from the project root with:

```powershell
java -jar build\libs\peanutbuttercat.jar
```

The application stores tasks in `data\peanutbuttercat.txt`, relative to the directory from which the JAR is run. If
the file does not exist yet, PeanutButterCat starts with an empty task jar and creates the file after the first task
change. To rebuild a fresh JAR, run `.\gradlew.bat clean shadowJar`; `clean` removes previous build outputs before
Shadow creates the new JAR.

## Acknowledgements and reused code

This project began from the [SE-EDU Duke project template](https://github.com/se-edu/duke),
including its Gradle project structure and JavaFX chatbot GUI foundation. Those materials were adapted and extended
for PeanutButterCat. The original template contributors are credited in [CONTRIBUTORS.md](CONTRIBUTORS.md).
