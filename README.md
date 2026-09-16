# LeBron James

LeBron James is a chatbot built as a greenfield Java project. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/lebronjames/LebronJames.java` file, right-click it, and choose `Run LebronJames.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see the following output:
   ```
   What's good? I'm LeBron James. They call me The King. 👑
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Running the app

Prerequisite: **Java 25**. The Gradle build pins its toolchain to Java 25, so
the app will not start on an older JDK. On macOS, switch to it with:

```
sdk use java 25.0.3.fx-zulu
```

`sdk use` applies only to the terminal window you type it in, so a new tab or
shell needs it again.

Run every command below from the project root (use `gradlew.bat` instead of
`./gradlew` on Windows).

### The GUI - the normal way to run it

```
./gradlew run
```

This compiles the current source, then starts `lebronjames.Launcher`, which
opens the JavaFX window. Because it recompiles first, it always runs your
latest code, so this is the command to use while working on the project. Once
the greeting appears, type a command such as `list` into the text field at the
bottom and press Enter.

### From the packaged JAR

```
./gradlew shadowJar
java -jar build/libs/lebronjames.jar
```

This runs the same GUI, but from the self-contained JAR described under
[Packaging the app as a JAR file](#packaging-the-app-as-a-jar-file). Use it to
check what someone receiving the JAR would see; it is not needed for everyday
development, since the JAR is only rebuilt when you ask for it.

### The text interface

`LebronJames` keeps a `main` of its own that runs the chatbot in the terminal
rather than in a window, which is useful for a quick check without the GUI:

```
./gradlew classes
java -cp build/classes/java/main:build/resources/main lebronjames.LebronJames
```

Gradle's `run` task is wired to `Launcher`, so the text interface has to be
started by naming its class directly. It does not touch JavaFX, so no module
path is required. On Windows, separate the two classpath entries with `;`
instead of `:`.

## Building and running with Gradle

The project uses the Gradle wrapper, so Gradle itself does not need to be
installed: `gradlew` downloads the right version on first use. Run these from
the project root (use `gradlew.bat` instead of `./gradlew` on Windows).

| Command | What it does |
| --- | --- |
| `./gradlew run` | Compiles the chatbot and starts the GUI (see [Running the app](#running-the-app)). |
| `./gradlew build` | Compiles, runs the tests, and packages the app into `build/libs/lebronjames.jar`. |
| `./gradlew shadowJar` | Builds just that JAR, without running the tests. |
| `./gradlew test` | Runs the JUnit tests. |
| `./gradlew clean` | Deletes everything under `build/`. |

## Packaging the app as a JAR file

The app ships as a single executable "fat" JAR: it holds the compiled classes
and every dependency, so it runs on any machine with Java 25 installed and
nothing else.

The JavaFX native libraries for **64-bit Windows, 64-bit Linux and Apple
Silicon macOS** are all packed into that one file, so the same download works on
any of the three. Only one processor architecture per operating system can be
included, because every JavaFX build names its libraries identically and a flat
JAR can hold only one file of a given name. For an Intel Mac, change
`mac-aarch64` to `mac` in the platform list in `build.gradle` and rebuild.

**To create it**, run this from the project root:

```
./gradlew shadowJar
```

(or `./gradlew build`, which also runs the tests first.)

**To locate it**, look in `build/libs/`:

```
build/libs/lebronjames.jar
```

The JAR is not committed to the repository - generated binaries do not belong
in version control, and `/build/` is listed in `.gitignore`. It is distributed
through a GitHub release instead.

**To run it**:

1. Copy `lebronjames.jar` into an empty folder.
2. Open a command window in that folder.
3. Run:

   ```
   java -jar "lebronjames.jar"
   ```

The quotes are not normally needed, but they matter if the folder path contains
spaces or characters such as `[`.

Your tasks are saved to `data/lebronjames.txt`, created next to the JAR the
first time you add a task, and reloaded the next time you start the app. To
start over, delete that file.
