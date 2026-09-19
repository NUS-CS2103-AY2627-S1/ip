# BENJI

BENJI is a JavaFX task-management chatbot that helps you organise to-dos,
deadlines, and events.

## Run BENJI

BENJI requires Java 25. From the project root, run:

```bash
./gradlew run
```

## Build the JAR

Create a self-contained JAR file with:

```bash
./gradlew clean shadowJar
```

The generated file is `build/libs/benji.jar`. Run it using:

```bash
java -jar build/libs/benji.jar
```

## User Guide

See the [BENJI User Guide](docs/README.md) for supported commands, examples,
and common error messages.

## Acknowledgements

### AI assistance

BenjaminWong33 used OpenAI Codex / ChatGPT (GPT-5.6 Terra) extensively as a
learning aid during development. It provided explanations and code suggestions
for selected Java, JavaFX, testing, documentation, and GitHub Actions CI work,
including parts of `MainWindow.java`, `DialogBox.java`, and
`MainWindow.fxml`. The author reviewed, adapted, integrated, and tested the
resulting changes.

### Reused and adapted work

- The initial JavaFX chatbot structure and `DialogBox` approach were adapted
  from the [SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html).
  This is course material, for which acknowledgement is optional; it is included
  here for transparency.
- The robot avatar (`src/main/resources/images/benji.png`) is by
  [Vectorly on Canva](https://www.canva.com/graphics/MAG4Vg4a_rI-ai-robot/).
- The user avatar (`src/main/resources/images/user.png`) is from
  [PNGEgg](https://www.pngegg.com/en/png-nclaw).

The image assets are used subject to their respective source licences and terms
of use.
