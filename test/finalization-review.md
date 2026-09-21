# Finalization review

Reviewed on 2026-09-13 using Zulu Java 25.0.3 on macOS ARM64.

## Design and correctness

- Command routing, change execution, persistence, and error formatting have
  separate responsibilities. Only commands that mutate tasks take snapshots.
- `TaskList` owns snapshot/restore behavior; rollback uses boolean completion
  states instead of interpreting UI strings. All mutations share one save boundary.
- Task constructors reject storage delimiters and line breaks even when callers
  bypass `Parser`. Existing storage records and command syntax remain compatible.
- File reads distinguish missing files from read failures. Corrupt records identify
  their path and line number; partial data is never returned. Load failures block writes.
- Failed writes preserve the previous task list. Temporary-file cleanup logs failures,
  and symbolic-link targets are protected from accidental replacement.
- Welcome text is shared, counts use singular/plural grammar, and the GUI ignores
  commands during shutdown. Long replies wrap at the minimum window size.
- The UI runner now creates isolated temporary working directories instead of
  removing and restoring the user's real `data/tasks.txt`.
- README launch/build instructions now describe the GUI and fat JAR accurately.
  Credits identify the starter template, SE-EDU GUI tutorial, and AI assistance.

## Verification

- `./gradlew clean javadoc build`: passed; 56 JUnit test cases, Checkstyle and Javadoc passed.
- Scripted UI suite: all 17 sessions passed with isolated task files.
- Real JavaFX smoke harness loaded the packaged FXML and checked Enter, Send,
  long-message sizing at 520x480, disabled exit controls, and commands after `bye`.
- Inspected the rendered GUI from that smoke test.
- Launched the fat JAR from a temporary folder using
  `java --limit-modules java.se,jdk.unsupported -jar sheppy.jar`.
  Excluding the JDK's JavaFX modules exposed the former Intel/ARM mismatch.
  The rebuilt JAR now starts using its own matching Mac libraries.
- The JAR includes separate Intel and ARM Mac native resources, plus the existing
  Windows/Linux x64 resources. Windows, Linux and Intel Mac execution is not verified here.
- JavaFX 17 emits compatibility/deprecation warnings on Java 25. The local GUI
  tests passed despite those warnings; they are not proof of other-platform compatibility.
- `git diff --check`: passed. No placeholder chatbot name remains in source/UI.

## Remaining release work

- Have teammates test the fat JAR with plain Java 25 on their operating systems.
- The User Guide is written in `docs/README.md`. Take the required full-window
  `docs/Ui.png` and verify the published GitHub Pages site.
- Review the changes, then commit/merge them and publish the final release asset
  when authorized. Existing assignment tags remain on their original commits.
- Event times remain free-form strings; chronological validation is not provided.
  Search results retain the existing local numbering; use `list` to obtain task
  numbers before marking or deleting tasks. Document this in the User Guide.
