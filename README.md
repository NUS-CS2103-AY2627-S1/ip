# Nova

Nova is a chatbot application. Given below are instructions on how to set it up.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. To run the console interface, locate `src/main/java/nova/Nova.java`, right-click it, and choose
   `Run Nova.main()`. To run the JavaFX graphical interface instead, locate
   `src/main/java/nova/Launcher.java`, right-click it, and choose `Run Launcher.main()`. If the code editor is
   showing compile errors, try restarting the IDE. If the console setup is correct, you should see something
   like the output below:
   ```
    _   _                  
   | \ | | _____   ____ _ 
   |  \| |/ _ \ \ / / _` |
   | |\  | (_) \ V / (_| |
   |_| \_|\___/ \_/ \__,_|
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating and running the fat JAR

Nova uses the Shadow plugin to package the application and its dependencies into one executable JAR file.
Run the following command from the project root:

```powershell
.\gradlew.bat shadowJar
```

On macOS or Linux, use `./gradlew shadowJar` instead. The generated file is located at
`build/libs/nova.jar`.

Run the JAR from the project root with Java 25:

```powershell
java -jar build/libs/nova.jar
```

The packaged JAR starts Nova's JavaFX graphical interface.

Nova stores its data relative to the directory from which the JAR is run. Running it from the project root
keeps the data file at `data/nova.txt`.
