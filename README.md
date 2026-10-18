# Kiki

**A JavaFX task assistant with typed commands and local storage.** Add to-dos, deadlines and events; find and sort tasks; set priorities; and track completion from a desktop conversation interface.

[User guide](docs/README.md) · [Source](src/main/java/kiki) · [Tests](src/test)

<img src="docs/Ui.png" alt="Kiki desktop interface showing example tasks and command responses" width="520">

## Features

- To-dos, deadlines and events with validated command input.
- Completion tracking, deletion, search, date filters and sorting.
- Task priorities and a local text-file store.
- Optional `@ai` command help through an OpenAI-compatible provider; the assistant's help is read-only.

## Build and run

Requires **Java 25**. Use the included Gradle wrapper:

```bash
./gradlew clean shadowJar
java -jar build/libs/kiki.jar
```

Kiki stores tasks in `data/kiki.txt` relative to the working directory. See the [user guide](docs/README.md) for the command reference, examples and storage behaviour.

### Optional AI help

Core task management works without an API key. To use AI help, configure `LLM_API_KEY` in your local environment. `LLM_BASE_URL` and `LLM_MODEL` select the compatible provider and model. Do not commit credentials. Review the user guide before enabling an external provider.

## Code structure

| Path | Responsibility |
| --- | --- |
| [parser](src/main/java/kiki/parser) | Command parsing and input handling |
| [task](src/main/java/kiki/task) | Task types and task-list behaviour |
| [storage](src/main/java/kiki/storage) | Local persistence |
| [gui](src/main/java/kiki/gui) | JavaFX application and dialogue components |
| [ai](src/main/java/kiki/ai) | Optional command-help integration |
| [src/test](src/test) | JUnit tests |

The project separates parsing, task logic, persistence and the interface so each responsibility can be developed and checked independently. Gradle packages the desktop application into a runnable JAR.

## Checks available

```bash
./gradlew test
```

The repository includes unit tests, a [UI test plan](test/ui-test-plan.md) and a Gradle CI workflow. 

## Project context

Developed as an individual NUS CS2103T project using the [course starter repository](https://github.com/NUS-CS2103-AY2627-S1/ip) and SE-EDU conventions. Kiki is a local, single-user desktop application; optional AI help requires an external service.
