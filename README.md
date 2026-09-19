# Lizzy

Lizzy is a Java 25 desktop task manager for todos, deadlines, events, and
tasks with flexible completion periods. Its JavaFX conversation interface
offers concise command guidance, persistent storage, schedule lookup, and a
warm, lightly witty personality.

<p align="center">
  <img src="docs/Ui.png" alt="Lizzy's JavaFX conversation interface"
       width="540">
</p>

## Highlights

- Manage todos, deadlines, one-day or timed events, and flexible completion
  periods from one conversation.
- Search task descriptions with case-insensitive partial matching, or view
  everything scheduled on a chosen date.
- Recover gracefully from invalid commands, impossible dates and times,
  duplicate tasks, and missing or unreadable data files.
- Keep tasks between sessions with automatic local storage.
- Use the same focused command workflow in the JavaFX or console interface.

## User Guide

Read the published [Lizzy User Guide](https://yanrong-wang.github.io/ip/) for
setup instructions, every command, examples, and error guidance. Its
[Markdown source](docs/README.md) is maintained in this repository.

## Development setup

1. Install JDK 25 and open this repository in IntelliJ IDEA.
2. Set the project SDK to JDK 25 and the language level to **SDK default**.
3. Run `./gradlew run` to start the graphical interface.

The console interface remains available through `lizzy.Lizzy#main`.

## Build and test

Create the cross-platform fat JAR with:

```sh
./gradlew clean shadowJar
```

The output is `build/libs/lizzy.jar`. Run the full automated checks with:

```sh
./gradlew check
```

## Acknowledgements

### AI assistance

Yanrong Wang used OpenAI Codex with GPT-5.6 Terra and GPT-5.6 Sol
extensively as a learning aid during development. It provided explanations,
design feedback, and code suggestions for selected Java, JavaFX, testing,
documentation, Git, and user-interface work. This included task parsing and
validation, error handling, automated tests, the JavaFX conversation
interface, the User Guide, and product screenshots. The author reviewed,
adapted, integrated, and tested the resulting changes.

OpenAI image generation was also used to create the original Lizzy and Jane
line-art portraits from user-directed visual references. The generated
portraits were reviewed and refined for use in the interface.

### Reused and adapted work

- This repository began from the
  [SE-EDU Duke starter project](https://github.com/se-edu/duke). Its initial
  JavaFX chatbot structure and `DialogBox` approach were adapted from the
  [SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html).
  The original contributors remain listed in
  [CONTRIBUTORS.md](CONTRIBUTORS.md). Course-provided material is credited
  here for transparency.
- The bundled
  [Source Serif 4](https://github.com/adobe-fonts/source-serif) font is
  Copyright 2014–2023 Adobe and is distributed under the SIL Open Font
  License 1.1; its license is included at
  `src/main/resources/fonts/LICENSE.md`.

The bundled font and generated portraits are used subject to their respective
licence and service terms.
