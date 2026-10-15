# NutriSoy

NutriSoy is a Java desktop task manager with **Soya**, a sassy but supportive
chatbot. Keep track of todos, deadlines, and events; mark tasks complete, search
your list, and organise tasks with tags. Tasks are saved locally between sessions.

Built by [Vincent Peh (Eskalade)](https://github.com/Eskalade) for the NUS
CS2103/T Individual Project.

## Getting started

Use **JDK 25**. From the repository directory:

```bash
./gradlew run
```

On Windows, use `gradlew.bat run`. Gradle downloads the configured JavaFX 25.0.1
dependencies for the build platform. On this project's macOS development setup:

```bash
sdk use java 25.0.3.fx-zulu
./gradlew run
```

Try `todo read a book`, then `list`. Enter commands in the text field and press
Enter or click **Do it**. See the [product website](https://Eskalade.github.io/ip/)
or [User Guide source](docs/README.md) for every command,
examples, validation rules, and storage recovery instructions.

## Build and test

```bash
./gradlew clean build
```

This runs JUnit tests, Checkstyle, and packaging. The executable Shadow JAR is
`build/libs/nutrisoy.jar`; the smaller versioned JAR is not the standalone bundle.
Run the bundle with Java 25:

```bash
java -jar build/libs/nutrisoy.jar
```

The release JAR bundles JavaFX for Windows x64, Linux x64, and macOS on Intel
and Apple Silicon. Use Java 25 matching a supported architecture and a graphical
desktop environment. Linux needs the system GTK 3 libraries used by JavaFX.
Windows/Linux ARM runtimes are not supported by this bundle. Cross-platform
packaging does not replace smoke testing on each target OS.
The repository name remains `ip`.

The [testing guide](docs/testing.md) describes automated coverage and the manual
GUI/platform checks. JUnit results are generated in
`build/reports/tests/test/index.html`.

## Data and recovery

NutriSoy uses `data/nutrisoy.txt` relative to the directory from which you launch
it. Use the same working directory each time to see the same list. Missing data
starts an empty list; the directory and file are created when you add a task.

Back up this file before editing it or moving the application. If saved data is
invalid or unreadable, NutriSoy displays a warning and disables task changes to
protect the original file. Repair the file and restart; valid recovered tasks
remain viewable with `list` and `find`.

## Acknowledgements

- The [NUS CS2103 iP starter](https://github.com/NUS-CS2103-AY2627-S1/ip)
  and [SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html)
  provide the project foundation and JavaFX/FXML conversation pattern.
- [OpenJFX](https://openjfx.io/) supplies JavaFX controls and FXML.
  [JUnit 5](https://junit.org/junit5/) supplies automated testing.
  [Gradle](https://gradle.org/), the
  [JavaFX Gradle plugin](https://github.com/openjfx/javafx-gradle-plugin),
  [Shadow](https://github.com/GradleUp/shadow), and
  [Checkstyle](https://checkstyle.org/) support building, packaging, and checks.
- **AI-assisted work:** Vincent Peh used OpenAI ChatGPT/Codex extensively for
  GUI improvements, Soya's personality and wording, persistence corrections,
  error handling, JUnit tests, documentation, and Git workflow assistance.
  The assistant generated and modified code, explained design choices, and ran
  automated checks. Vincent remains responsible for reviewing, understanding,
  and verifying the submitted work. Automated checks do not establish that all
  GUI/platform behavior has been manually tested.
- **Avatars and screenshot:** Vincent used OpenAI's built-in image-generation
  tool through ChatGPT/Codex to generate `src/main/resources/images/soya-carton.png`
  for Soya. It was generated without a reference image, real brand names, or
  logos. The You avatar is a JavaFX label styled with CSS, created with
  ChatGPT/Codex assistance. The User Guide screenshot uses sample task data.
  The generation prompt
  is recorded in [the image notes](docs/avatar.md).
  The retired bot image was sourced from the
  [FairPrice NutriSoy product listing](https://www.fairprice.com.sg/product/f-n-nutrisoy-high-calcium-fresh-soya-milk-reduced-sugar-475ml-13053097);
  it and the user image of unknown origin were removed because reuse permission
  was not established.

No new third-party library was introduced during submission finalisation.
Confirm that the existing libraries satisfy the course's approval requirements.
