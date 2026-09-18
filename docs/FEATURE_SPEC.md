# Mochi MVP feature specification

## Product goal

Mochi helps a student capture and manage small commitments quickly from a
simple chat interface. The MVP prioritises reliable task storage and a small,
learnable command set over advanced scheduling.

## User stories

- As a student, I can add a todo, deadline, or event so that I do not forget
  an obligation.
- As a student, I can list, search, complete, reopen, and delete tasks so that
  I can maintain an accurate task list.
- As a student, I can close and reopen Mochi without losing tasks.
- As a student, I receive an actionable error message when my command is
  incomplete or invalid.

## Functional requirements

1. Support `todo DESCRIPTION`.
2. Support `deadline DESCRIPTION /by YYYY-MM-DD`.
3. Support `event DESCRIPTION /from START /to END`.
4. Support `list`, `find KEYWORD`, `mark NUMBER`, `unmark NUMBER`,
   `delete NUMBER`, `sort`, and `bye`.
5. Persist task type, description, details, order, and completion status.
6. Reject incomplete or invalid commands and malformed saved records without
   crashing.
7. Provide both command-line and JavaFX chat interfaces.

## Non-functional requirements

- Use Java 25 and Gradle.
- Keep user-facing messages concise and friendly.
- Preserve task order for equal descriptions when sorting.
- Cover parsing, storage, task-list operations, and end-to-end flows with
  automated tests.

## Out of scope for the MVP

Notifications, cloud synchronisation, accounts, recurring tasks, natural
language understanding, and multi-user collaboration can be considered later.
