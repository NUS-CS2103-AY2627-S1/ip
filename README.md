# Wangsa

Wangsa is a task manager with a desktop window and a terminal
interface. It tracks todos, deadlines, and events, and saves changes automatically.

[User guide](https://manutd1234.github.io/ip/) ·
[Download](https://github.com/Manutd1234/ip/releases/latest) ·
[Credits](docs/CREDITS.md)

## Current release

Wangsa v0.8 improves code readability and saved-data validation. Desktop layout
is separate from command execution. Escaped line breaks survive reloading, and
invalid saved fields produce clearer recovery messages.

Task saves and the JavaFX native-library cache stay inside the launch folder,
following the course's file-writing constraint. Launch from the folder containing
the JAR and your existing `data` folder to keep using the same tasks. The save-file
format is unchanged and remains compatible with v0.7.

The [v0.8 release](https://github.com/Manutd1234/ip/releases/tag/v0.8) records the
verification run and SHA-256 digest for its single standalone JAR. The release
workflow tests the same JAR on Windows, Linux, Intel Mac, and Apple Silicon Mac.
The Windows students credited below tested v0.6; automated release results are
recorded separately from those human reports.

Terminal help retains the ASCII-safe punctuation introduced in v0.7 for
[issue #471](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/471).

## Try it

Click the command box at the bottom of the window. Enter one command at a time,
then press **Enter** or click **SEND**:

```text
todo read a book
deadline ST2334 /by 2026-09-30
list
```

Keep the slash in `/by`. Dates must exist: September has 30 days, so
`2026-09-31` is not valid. Type `help` for more examples.

## Run from source

Use **Java 25**. From the project folder:

```shell
java -version
./gradlew run
```

For the terminal interface, use `./gradlew runCli`. On Windows, replace
`./gradlew` with `gradlew.bat`.

In IntelliJ IDEA, open the project folder, import the Gradle project, and set both
the project SDK and Gradle JVM to Java 25. Keep Java source files under
`src/main/java`.

## Build and test

```shell
./gradlew clean check shadowJar portableZip javadoc
python3 tests/release_smoke.py build/libs/Wangsa.jar
```

The first command runs the unit tests and style checks, generates Javadoc, and
builds `build/libs/Wangsa.jar` and the optional
`build/distributions/Wangsa.zip`. The ZIP is a developer convenience package;
the GitHub release contains one standalone JAR for course evaluation. The second
checks the packaged app in temporary folders, without changing your saved tasks.
It exercises SEND, Enter, history, and shortcut buttons programmatically.
Also check actual clicking and typing through the
[manual checklist](tests/peer-smoke-test.md). It needs a graphical desktop.
Use `python` on Windows or `xvfb-run -a python3` on headless Linux.

The `shadowJar` task is an alias for Wangsa's custom fat-JAR builder. The JAR
includes JavaFX and supports Windows x64, Linux x64, Intel Mac, and Apple Silicon.
Linux needs GTK 3. To try the JAR, copy it to an empty folder and run:

```shell
java -jar Wangsa.jar
```

See the [test plan](tests/test-plan.md) and [manual checklist](tests/peer-smoke-test.md).

## How the code is organised

The desktop and terminal interfaces share the same parser, task service, and
storage. `Task` subclasses represent todos, deadlines, and events. `Storage` saves
tasks in a plain text file, `data/wangsa.txt`, inside the launch folder. Follow
the documented launch steps to keep this beside the JAR; source runs use the
project folder. Packaged JavaFX libraries are cached under `.wangsa/javafx-natives`
in that same folder. No database or account is needed.

The [architecture guide](docs/ARCHITECTURE.md) explains the main classes.
The user guide lives in `docs/README.md`; GitHub Pages serves it from `master`
and `/docs`.

## Optional AI help

`help` works offline. `@ai QUESTION` can ask Groq to explain a command; it cannot
change tasks. Set `LLM_API_KEY` to a Groq key to enable it. The default in-app
model is `openai/gpt-oss-20b`; `LLM_MODEL` can override it.

See [AI setup](docs/README.md#optional-ai-setup) and
[privacy and limitations](docs/README.md#optional-ai-command-help-ai).

## AI assistance and credits

Manutd1234 used **OpenAI Codex with [GPT-6 Astra](https://developers.openai.com/api/docs/models/gpt-6-astra)**
to help write and refactor code, improve the GUI and error handling, add tests,
prepare the JAR, and write documentation. AI contributed to substantial parts of
the project, not just wording. The project author is responsible for the final work.
This development assistance is separate from the optional Groq feature in the app.

## Acknowledgements

Wangsa started from the [course iP template](https://github.com/NUS-CS2103-AY2627-S1/ip),
based on [SE-EDU Duke](https://github.com/se-edu/duke).

- [JavaFX](https://openjfx.io/) provides the desktop interface.
- [LangChain4j](https://github.com/langchain4j/langchain4j) connects optional command help to Groq.
- [SLF4J](https://www.slf4j.org/) handles dependency logging.
- LangChain4j also brings in [Jackson](https://github.com/FasterXML/jackson),
  [JSpecify](https://jspecify.dev/), and [JTokkit](https://github.com/knuddelsgmbh/jtokkit).
  These libraries are included in the JAR; they are not Wangsa-authored code.
- [JUnit](https://junit.org/), [Gradle](https://gradle.org/), and
  [Checkstyle](https://checkstyle.org/) support testing, builds, and style checks.
- [GitHub Pages](https://pages.github.com/), [Jekyll](https://jekyllrb.com/), and
  the [Primer theme](https://github.com/pages-themes/primer) publish the user guide.
- [@lingsongc](https://github.com/lingsongc), [@Papangkorn-Pann](https://github.com/Papangkorn-Pann),
  [@Dancodes2](https://github.com/Dancodes2), and [@LINGSIHAN](https://github.com/LINGSIHAN)
  provided Windows smoke testing of v0.6. Their reports and the follow-up CLI finding are
  recorded in the [test record](tests/peer-smoke-test.md) and
  [issue #471](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/471). The reported
  terminal-only punctuation issue is fixed in v0.7.

The interface uses simple symbols drawn in JavaFX, with no external artwork or audio.
The [credits page](docs/CREDITS.md) lists the code sources, tutorials, and AI assistance.
