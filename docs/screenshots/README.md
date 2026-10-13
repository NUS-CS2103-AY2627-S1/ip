# Command screenshots

These are unedited captures of Wangsa's own desktop interface, taken on macOS
with Java 25 on 2026-09-18. They contain disposable demonstration tasks, not
personal task data. No external artwork or generated mockups were used.

The application was the published [v0.6 JAR](https://github.com/Manutd1234/ip/releases/tag/v0.6):

```text
8459662741ecf2c7e9a85a5d3294688b7a8a3f33397389998985920951acdd34
```

A temporary macOS app wrapper made that unchanged JAR accessible to the capture
tool. The wrapper is not a release artifact. Its data was isolated from the
author's normal task list.

## Reproduce the examples

Start with an empty task list and enter these commands in order. Each image is
named after the command whose result it shows:

```text
todo read a book
deadline submit report /by 2026-09-20
event project meeting /from Monday 2pm /to Monday 4pm
list
sort
find book
mark 2
unmark 2
delete 2
help
```

Sorting happens before the search and update screenshots, so `read a book` is
task 2 in those images. The help image shows the beginning of a scrollable reply.
The original overview image at `../Ui.png` is retained separately.

The author requested these screenshots; OpenAI Codex assisted with entering
the demo commands, capturing the app, and documenting the results. See the
[project credits](../CREDITS.md) for the application's sources and tools.
