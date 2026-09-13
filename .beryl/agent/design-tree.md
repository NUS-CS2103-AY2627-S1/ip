# Design Tree

## Current Design Concept

The project is a Java personal-assistant chatbot. Its sole confirmed purpose is
to help one person keep track of various things; detailed commands, tracked
categories, storage, and interaction rules remain open.

## Open Decisions

| Decision | Options | Current Lean | Why |
| --- | --- | --- | --- |
| How should Athena's Archive schedule reviews? | Deterministic mastery queue, time-based spaced repetition | Deterministic mastery queue | It gives useful repeat practice without clock or scheduling complexity. |

## Settled Decisions

| Decision | Choice | Date | ADR |
| --- | --- | --- | --- |
| Java 25 is required for build and run tasks. | Java 25 | 2026-08-20 | n/a |
| Tracked information is held only for the current session. | A dynamically sized in-memory `ArrayList<Task>` | 2026-08-20 | n/a |
| Task types share completion behavior while owning their distinct details. | Abstract `Task` base class with `Todo`, `Deadline`, and `Event` subclasses | 2026-08-20 | n/a |
| User command errors are recoverable. | `OdysseusException` is caught once per command; invalid commands do not mutate `TaskList` | 2026-08-20 | n/a |
| Task states and existing-task actions are closed sets. | `TaskStatus` and `TaskAction` enums | 2026-08-20 | n/a |
| The chatbot has a native conversation frontend. | JavaFX FXML views styled by one local CSS stylesheet | 2026-09-02 | n/a |
| The console and JavaFX frontend share command behavior. | `Odysseus#getResponse(String)` owns command execution and reply text | 2026-09-02 | n/a |
| The conversation distinguishes commands, replies, and corrections. | The JavaFX adapter uses typed `ConversationResponse` metadata for error styling; traveler commands are compact and right aligned, while Odysseus replies use the available reading width. | 2026-09-13 | n/a |
| Odyssey-focused recall is user-authored and persistent. | Athena's Archive stores prompt-and-answer learning cards in a separate local sidecar file. | 2026-09-08 | [ADR 0003](adr/0003-add-athenas-archive.md) |
| First-release card selection favors weak cards predictably. | Select lowest mastery, then fewest reviews, then insertion order. | 2026-09-08 | n/a |

## Pressure Points

* The available project information is intentionally minimal; obtain product
  requirements before implementing behavior.

## Recording Rule (Design Tree vs ADR)

Add or update this file when:

* A decision is still evolving.
* You are comparing options before implementation.
* The choice may still change after one or two implementation iterations.

Create an ADR when:

* The decision changes module boundaries, persistence shape, adapter contracts, security model, naming conventions used across contexts, or test strategy.
* Future contributors are likely to revisit the choice without clear repo history.
