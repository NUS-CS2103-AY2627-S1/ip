# Add high-value JUnit tests for `TaskList`

## Decision

`TaskList` is the next highest-value test target after `Parser` because it
owns the application's core mutable state. Its operations directly affect
adding, ordering, deleting, and marking tasks, all of which are visible to
users through multiple commands.

## Test coverage

`src/test/java/TaskListTest.java` covers every public `TaskList` operation and
the important state transitions:

- creating an empty list;
- adding tasks and preserving insertion order;
- defensive copying in the constructor;
- deleting and reindexing tasks; and
- marking tasks done and not done.

`Ui` is presentation plumbing and `Storage` uses a fixed filesystem path, so
those classes are deferred for separate, purpose-built tests. No production
files were modified.

## Suggested commit message

```text
Add JUnit tests for TaskList
```

