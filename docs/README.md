# Bibi User Guide

Bibi is a command-line task chatbot. It supports ToDos, deadlines, and events, and retains date/time information exactly as you enter it.

## Add tasks

Use one of these commands:

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
```

The date and time values are treated as text. For example, `deadline do homework /by no idea :-p` is valid.

## List tasks

```text
list
```

Task types are displayed as `[T]` (ToDo), `[D]` (deadline), and `[E]` (event). Completion state is `[ ]` for incomplete and `[X]` for complete.

## Change completion state

```text
mark 2
unmark 2
```

Task numbers come from the list output.

## Exit Bibi

```text
bye
```
