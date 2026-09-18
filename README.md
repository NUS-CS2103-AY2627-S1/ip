# Herta

Herta is a Java desktop task manager for managing todos, deadlines, and events.
See the [Herta User Guide](docs/README.md) for installation and command usage.

## Setting up in IntelliJ IDEA

Prerequisite: JDK 25. Update IntelliJ IDEA to the most recent version.

1. Open IntelliJ IDEA. If you are not on the welcome screen, click `File` > `Close Project` first.
1. Open the project in IntelliJ IDEA as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** as explained in the [JetBrains JDK setup guide](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. To run the command-line version, locate `src/main/java/herta/Herta.java`,
   right-click it, and choose `Run Herta.main()`. If the code editor shows
   compile errors, try restarting IntelliJ IDEA. If setup is correct, you
   should see output similar to this:
   ```
    _   _           _
   | | | | ___ _ __| |_ __ _
   | |_| |/ _ \ '__| __/ _` |
   |  _  |  __/ |  | || (_| |
   |_| |_|\___|_|   \__\__,_|
   ```

To launch the graphical interface, run `.\gradlew.bat run` from the project
root on Windows or `./gradlew run` on macOS/Linux.

**Warning:** Keep `src/main/java` as the Java source root. Do not rename these
folders or move Java files outside this path, as Gradle and other tools expect
this standard project layout.

## Building and testing

From the project root, run:

```powershell
.\gradlew.bat test
.\gradlew.bat shadowJar
```

Use `./gradlew test` and `./gradlew shadowJar` on macOS/Linux. The packaged
application is written to `build/libs/Herta.jar`.

## Task data policy

Herta stores active tasks in `data/herta.txt` and archived tasks in
`data/archive.txt`. Duplicate task records are allowed, and task descriptions
and dates are validated before saving.

## Git commit checks

Enable the repository's local commit-message hook once after cloning:

```powershell
git config core.hooksPath .githooks
```

The hook rejects overlong commit subjects and body lines, and requires a body
for commits that contain substantial changes. It runs before Git creates the
commit, so formatting problems are caught locally rather than by CI.

## AI Assistance

I have used OpenAI Codex as an AI-assisted coding tool throughout the development of this project. It was used to suggest, generate, explain, debug, and refine code, including command parsing, command dispatch, task operations, and EOF handling. I reviewed, adapted, and tested the resulting code.

## Third-party assets

The Herta avatar image is from *Honkai: Star Rail* and is used for a
non-commercial student project.

- Image: [Herta: Tea Break profile picture](https://honkai-star-rail.fandom.com/wiki/Herta:_Tea_Break_%28Profile_Picture%29)
- Copyright: © miHoYo/HoYoverse. All rights reserved.

This project is unofficial and is not affiliated with, endorsed, or sponsored
by HoYoverse.
