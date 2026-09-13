# Architecture

## Bounded Contexts

| Context | Owns | Does Not Own | Public Entry Point |
| --- | --- | --- | --- |
| Personal assistant chatbot | Conversational interactions and the user's tracked information | Unspecified external integrations and persistence details | `src/main/java/Odysseus.java` |

## Boundary Rules

1. A context may import only another context's public entry point.
2. Internal files of another context are forbidden imports.
3. External APIs, SDKs, and persistence details must be accessed through adapters.
4. Domain logic must not depend directly on HTTP objects, ORM records, UI state, or vendor client types.

## Public Interface Rule

Each context exposes one explicit public entry point:

* Java: the application entry point currently starts at
  `src/main/java/odysseus/Odysseus.java`. Introduce a small explicit public API for each
  additional context when it is created.

## Forbidden Import Policy

Record concrete forbidden import patterns here once contexts exist:

* `[from] -> [to/internal/**]`
* `[from] -> [to/infrastructure/**]`

Keep this list small and high-signal. Add rules only after repeated boundary mistakes.

## Task Model

`Task` is the abstract model for completion state and shared rendering. `Todo`,
`Deadline`, and `Event` extend it to supply their type markers and, where
needed, their date/time text. `TaskStatus` owns the completion-state markers,
while `TaskAction` owns the fixed existing-task command keywords. The console
entry point stores and handles only the `Task` abstraction.

`TaskList` owns an `ArrayList<Task>`, task count, one-based task-number
validation, and deletion. `OdysseusException` represents user-correctable command failures;
the console entry point catches it per command so one mistake cannot end the
conversation or partially mutate the task list.

## Athena's Archive

`LearningDeck` owns user-authored `LearningCard` values, their one-based archive
numbers, topic filtering, and deterministic review selection. A card owns its
prompt, expected answer, topic, mastery, and review count. `Odysseus` keeps at
most one active card as conversation state; that temporary state is never
persisted.

`LearningStorage` is the archive's file adapter. It stores a versioned,
Base64-encoded sidecar file adjacent to the task log, so learning-card text
cannot corrupt the existing task-file format. The frontend continues to use
only `Odysseus#getResponse(String)` and must not access the deck or storage.

## JavaFX Presentation Adapter

`HelloWorld` loads the FXML view and injects the `Odysseus` public entry point
into `MainWindow`. `MainWindow` owns only JavaFX event handling and rendering;
it delegates every command to `Odysseus#getConversationResponse(String)` and
uses the response metadata only for presentation. `DialogBox` is a
reusable FXML control for one ship-log entry, while `odysseus.css` owns the
warm editorial visual system. The frontend must not access `TaskList`,
`Parser`, or `Storage` directly.
