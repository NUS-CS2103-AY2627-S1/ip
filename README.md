# ShrekAndDonkey

ShrekAndDonkey is a Shrek-themed task chatbot for managing to-dos, deadlines, and events through a JavaFX graphical interface.

## User Guide

See the complete [ShrekAndDonkey User Guide](docs/README.md) for setup instructions, supported commands, date formats, and common error behavior.

## Development

This project requires Java 25. Run the full automated test suite and generate the JaCoCo coverage report with:

```text
./gradlew test jacocoTestReport
```

Build the runnable JAR with:

```text
./gradlew shadowJar
```

The packaged application is created at `build/libs/shrekanddonkey.jar`.

## Acknowledgements

This project was developed with widespread AI assistance by [@josthan12](https://github.com/josthan12) using OpenAI Codex (ChatGPT) and Google Antigravity (Gemini).

These tools were used throughout the iP, including Levels 0–10 and the implemented extensions: A-MoreOOP, A-Packages, A-Gradle, A-JUnit, A-Jar, A-JavaDoc, A-CodingStandard, A-FullCommitMessage, A-Assertions, A-CodeQuality, A-BetterGui, A-BetterPersonality, and A-UserGuide.

AI assistance included planning, Java implementation and refactoring, debugging, Gradle configuration, JUnit tests, JaCoCo coverage checks, GUI styling, Javadoc and coding-standard reviews, feature specifications, user-guide documentation, and image generation. [@josthan12](https://github.com/josthan12) still tested, adapted, and integrated the generated work into this project.
