# Wangsa iP submission

Wangsa is a Java 25 task manager with JavaFX desktop and terminal interfaces.
It supports todos, deadlines, events, search, completion changes, deletion, and
deadline sorting. Changes save automatically. Optional AI explains commands
and cannot access or modify saved tasks.

The submitted release is **v0.8**, incorporating the code-quality follow-up to
[issue 4](https://github.com/Manutd1234/ip/issues/4). Window layout and command
handling have separate responsibilities. Saved text retains escaped line breaks,
and invalid fields receive clearer errors. Application writes stay inside the
launch folder, with a reusable JavaFX native-library cache.

## Try the release

1. Install Java 25.
2. Download the single `Wangsa.jar` asset from the
   [v0.8 release](https://github.com/Manutd1234/ip/releases/tag/v0.8).
3. Place it in a writable folder with your existing `data` folder, open a
   terminal there, and run:

```shell
java -jar Wangsa.jar
```

Enter `todo read a book`, then `list`. For a deadline, use
`deadline ST2334 /by 2026-09-30`.

[User guide](https://manutd1234.github.io/ip/) ·
[Credits](https://manutd1234.github.io/ip/CREDITS.html)

## Verification

The source passes **113 JUnit tests**, Checkstyle, Javadoc, and a clean Java 25
build. The release workflow checks the same standalone JAR on Windows, Linux,
Intel Mac, and Apple Silicon Mac. The v0.8 release page records the completed
verification run and the uploaded JAR's SHA-256 checksum.

Packaged checks cover commands, saving and reopening, invalid data protection,
offline help, Enter/SEND, history, shortcuts, resizing, and long text. Additional
local checks confirmed save-file compatibility with the exact v0.7 JAR in both
directions and exercised 20,000 malformed inputs without an unexpected crash.

Human Windows reports from the credited students apply to v0.6. The v0.8
cross-platform results are automated and are not a new human peer-test sign-off.
The [manual test record](https://github.com/Manutd1234/ip/blob/master/tests/peer-smoke-test.md)
keeps those earlier results separate. An additional manual desktop-control check
during the October audit could not attach and is not counted as passed.

## Implementation and credits

Task subclasses share a common `Task` parent. Parser, service, storage, view, and
application classes have separate responsibilities. Failed saves restore the
previous in-memory state. Invalid saved files are reported instead of silently
overwritten. Event start/end values are free-form text; reminders and editing
task descriptions are not supported.

OpenAI Codex with GPT-6 Astra assisted substantial parts of implementation,
refactoring, layout, tests, packaging, and documentation. Source, tutorial,
library, and peer-testing acknowledgements are linked above. Interface symbols
are drawn in JavaFX code, with no external artwork or audio.
