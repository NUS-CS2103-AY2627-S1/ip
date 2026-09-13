# Assertion rationale

The assertions in this increment check programmer-controlled assumptions. They do not validate
user commands or saved-file contents because those inputs can legitimately be invalid and must
continue to produce Alfred's existing error messages even when assertions are disabled.

Gradle enables assertions for tests and for `./gradlew run` through the `-ea` JVM option in
`build.gradle`. When running Alfred directly from IntelliJ, add `-ea` to the Run Configuration's
VM options so the same assertions are checked.

## Task-list invariants

- `TaskList.add` asserts that the task is non-null. Every task originates from the parser or the
  storage loader, both of which should construct a real task. A null entry would otherwise cause
  a later failure away from the code that introduced the defect.
- `TaskList.get` and `TaskList.remove` assert that their indices identify existing tasks. The
  parser converts and validates a user's task number before creating each numbered command. An
  invalid index at this point therefore indicates that code has bypassed or broken that contract.

## Storage type invariant

- `Storage.serialise` asserts that a task's `TaskType` and its concrete class agree: `TODO` uses
  `Todo`, `DEADLINE` uses `Deadline`, and `EVENT` uses `Event`. Serialization casts deadline and
  event tasks to access their date fields. A mismatch is an internal model defect, not a user or
  file-input error, so it should fail at the serialization boundary with a clear assertion message.
