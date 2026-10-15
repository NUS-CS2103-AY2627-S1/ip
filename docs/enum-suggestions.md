# Enum suggestions

## Recommended: `CommandType`

The application's supported commands form a fixed set, so they are a natural fit for an enum:

```java
public enum CommandType {
    TODO,
    DEADLINE,
    EVENT,
    LIST,
    MARK,
    UNMARK,
    DELETE,
    BYE,
    UNKNOWN
}
```

Using `CommandType` avoids repeatedly treating command names as unrelated string values. It also gives command-processing methods a restricted, meaningful type, while `UNKNOWN` represents unsupported input.

## Alternatives considered

- `TaskType { TODO, DEADLINE, EVENT }` could contain each task's display icon. However, the `Todo`, `Deadline`, and `Event` subclasses already represent these types through polymorphism, so an enum would duplicate that information.
- `TaskStatus { DONE, NOT_DONE }` could replace `Task.isDone`. A boolean is simpler and clearer while completion has exactly two states, so an enum is unnecessary here.

For the current application, `CommandType` provides useful type safety without overengineering the task model.
