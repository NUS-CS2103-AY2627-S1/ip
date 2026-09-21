# Melodie

```text
 __  __      _           _ _
|  \/  | ___| | ___   __| (_) ___
| |\/| |/ _ \ |/ _ \ / _` | |/ _ \
| |  | |  __/ | (_) | (_| | |  __/
|_|  |_|\___|_|\___/ \__,_|_|\___|

          ♪  ♫  ♪
```

Melodie is a desktop task manager with a musical-conductor personality. It
helps users manage todos, deadlines, and events through a conversational
JavaFX interface.

## Features

- Add, list, update, find, complete, reopen, and delete tasks.
- Store todos, deadlines, and events between sessions.
- Accept numeric dates and natural date words such as `today`, `tomorrow`, and
  weekdays.
- Provide command hints, command-history navigation, and visually distinct
  error messages.

See the [Melodie User Guide](docs/README.md) for the complete command reference.

## Requirements

- Java 25

## Running Melodie

From the project root, run:

```bash
./gradlew run
```

On Windows, use `gradlew.bat run` instead.

## Building and running the JAR

Create the cross-platform fat JAR with:

```bash
./gradlew clean shadowJar
```

The generated file is `build/libs/Melodie.jar`. Copy it into an empty folder
and run it with Java 25:

```bash
java -jar Melodie.jar
```

## Testing

Run the automated tests and code-quality checks with:

```bash
./gradlew clean check
```

## Credits

- This project was developed from the
  [NUS CS2103/T individual project starter repository](https://github.com/NUS-CS2103-AY2627-S1/ip).
- Cat user-avatar image: `Kitty` WhatsApp sticker pack by Zuckerschnute.
- Dog Melodie-avatar image: `guat` WhatsApp sticker pack by Viko & Co.
- Mount Fuji background: original photograph by Nathaniel Lim.
- OpenAI ChatGPT and Codex were used by Nathaniel Lim throughout the project
  to assist with brainstorming, implementation, debugging, refactoring,
  testing, and documentation across multiple increments. All AI-generated
  suggestions and content were reviewed, tested where applicable, and adapted
  before inclusion.

The third-party sticker artwork remains credited to its respective creators.
