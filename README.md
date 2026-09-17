# NotMarth project template

This is a project template for a greenfield Java chatbot project named _NotMarth_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/notmarth/NotMarth.java` file, right-click it, and choose `Run 'NotMarth.main()'` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see the following output:
   ```
   ____________________________________________________________
    _   _  ___ _____ __  __    _    ____ _____ _   _
   | \ | |/ _ \_   _|  \/  |  / \  |  _ \_   _| | | |
   |  \| | | | || | | |\/| | / _ \ | |_) || | | |_| |
   | |\  | |_| || | | |  | |/ ___ \|  _ < | | |  _  |
   |_| \_|\___/ |_| |_|  |_/_/   \_\_| \_\|_| |_| |_|
   Hello! I'm NotMarth.
   What can I do for you?
   ____________________________________________________________
   Bye. Hope to see you again soon!
   ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating and running the executable JAR

Prerequisite: JDK 25.

The project uses the [Shadow Gradle plugin](https://gradleup.com/shadow/) to create an executable fat JAR. From the project directory, run:

```bash
./gradlew clean shadowJar
```

On Windows, run `gradlew.bat clean shadowJar` instead. The generated file is:

```text
build/libs/notmarth.jar
```

To distribute NotMarth, copy `build/libs/notmarth.jar` into an empty folder. Open a command window in that folder and run:

```bash
java -jar "notmarth.jar"
```

NotMarth stores its battle plan in `data/notmarth.txt` relative to the folder from which the JAR is launched. The JAR itself is generated under `build/`, which is ignored by Git and should not be committed. For distribution through GitHub, attach the generated JAR to a release instead.

## AI Usage Declaration

A usage of level AI-5 is used throughout the entire project. I used AI to create the multiple functionalities of this project, while doing a manual check through of the code using git diff and making tweaks where necessary. Each AI output description is also manually checked through to ensure that AI is producing the output in which I desire. After every commit, the code is tested using AI and also manually tested using testcases in the ui-test-plan.md file, before being pushed to the remote.
