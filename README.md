# Anders

Meet **Anders**, your lantern keeper: a calm woodland companion who helps you
find a path through todos, deadlines, and events, one small step at a time.

- **A gentle voice:** "A new trail marker. I've added this task" and
  "One more light along the path. Task marked as done."
- **A woodland desk:** forest green, warm parchment, and amber accents, with a
  serif title and readable system text.
- **A little light:** original vector lantern and compass icons in the header,
  conversation, and window icon. No external images or font downloads are needed.

## Run the app

The release JAR bundles JavaFX for **Windows x64, Linux x64, and macOS on Intel
or Apple Silicon**. Use **64-bit Java 25** matching your computer.
You do not need Gradle or a separate JavaFX installation to run the JAR.

1. Install [Java 25](https://adoptium.net/temurin/releases/?version=25).
2. Download `anders.jar` from the Assets section of an
   [Anders release](https://github.com/anderschow/ip/releases).
3. Put it in a folder where you want to keep your tasks and open a terminal there.
4. Check Java, then launch the app:

   ```text
   java -version
   java -jar anders.jar
   ```

The first command should report version 25. Try `todo read chapter 1`, then
`list` and `mark 1`. **Commands** in the window explains the command formats.

Always launch from the same folder: tasks are saved in `data/anders.txt`
relative to that folder. See the [user guide](docs/README.md) for platform-specific
terminal instructions and all features.

If no release has an `anders.jar` asset yet, a packaged release has not been
published. You can build it from source using the developer instructions below.

## Build from source (developers)

Download or clone this repository, then open a terminal in its root folder.
Use **JDK 25** and set `JAVA_HOME` to its installation if Gradle uses another JDK.
The first build needs internet access to download Gradle and the libraries.

| Action | Windows PowerShell | macOS / Linux |
| --- | --- | --- |
| Run from source | `.\gradlew.bat run` | `./gradlew run` |
| Run tests and Checkstyle | `.\gradlew.bat check` | `./gradlew check` |
| Create the release JAR | `.\gradlew.bat clean shadowJar` | `./gradlew clean shadowJar` |

If macOS or Linux reports `Permission denied` for the wrapper, first run
`chmod +x gradlew`. In IntelliJ, open the folder as a Gradle project, select
JDK 25 as the project and Gradle JVM, and run `anders.Launcher`.

The release output is **`build/libs/anders.jar`**. The build selects host
dependencies for development and bundles all supported platforms for release.
Intel and Apple Silicon native libraries are kept separately and selected by
the launcher. The console entry point remains `anders.Anders`.

## Verify the release JAR

Build the application and its separate test helper:

```powershell
.\gradlew.bat check shadowJar smokeAgentJar
python test/run_jar_smoke.py
```

On macOS/Linux, replace `.\gradlew.bat` with `./gradlew`. On a headless Linux
machine, prefix the Gradle check and Python smoke-test commands with
`xvfb-run --auto-servernum`.

The smoke test requires Python 3 and an ordinary JDK 25 **without bundled JavaFX**.
It copies the release JAR into a temporary folder, launches the actual
`java -jar` entry point, enters commands through its GUI, and relaunches it to
check saved tasks. It leaves your own task file untouched. Its logs and the JAR's
SHA-256 checksum are saved in `build/reports/jar-smoke/`.
`anders-smoke-agent.jar` is only a test helper; do not distribute it to users.

GitHub Actions runs JUnit and Checkstyle on Windows, Linux, Intel Mac, and Apple
Silicon Mac with ordinary JDK 25. It builds the release JAR once on Windows and
runs the same JAR through the smoke test on all four targets. Check both the
`build` and `jar-smoke` jobs after pushing these changes.

The [UI test plan](test/ui-test-plan.md) also describes manual release checks.
Run the local console UI skill, if installed, in an isolated project copy because
its fixtures replace `data/anders.txt`:

```text
python .codex/skills/test-ui/scripts/run_ui_tests.py
```

JUnit saves GUI previews in `build/reports/gui/`.

## Publish a release

1. Wait for all build and packaged-JAR smoke-test jobs to pass for the release commit.
2. Download the `anders-release` artifact from that successful Actions run and
   extract `anders.jar`.
3. Test that JAR manually from an empty folder; ask classmates on other operating
   systems to try the same file.
4. Attach that tested `anders.jar` to a GitHub release. Upload the application
   asset, not just GitHub's automatically generated source-code archives.

Follow the course's submission instructions and confirm the course dashboard's
Git Standard indicator is green. GitHub build results do not replace that check.
