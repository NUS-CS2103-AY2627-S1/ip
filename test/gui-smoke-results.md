# Week 6 GUI smoke evidence

## Latest run: 18 September 2026 (avatar redraw)

Re-run after redrawing both avatars. The first replacement artwork was rejected
on review: its looping body placed a small accent circle low on the figure,
which read awkwardly at the 58px chat size. Both avatars were redrawn as a
simpler closed loop with dot eyes and a single tail curl, mirrored between the
two so the pair faces each other, keeping the same light palette. Windows,
Microsoft OpenJDK 25.0.4, JavaFX 17.0.7, against a clean
`gradlew.bat clean check shadowJar` build and a new temporary directory.

| Check | Result |
|---|---|
| Whitespace-only Send disabled | PASS |
| TextField action submits valid tasks and list | PASS |
| Invalid `bye extra` does not close | PASS |
| Error uses explicit `Please check:` text and contrasting color | PASS |
| Input focus restored after submission | PASS |
| Minimum layout, input visible and error readable | PASS |
| Wide layout, long text wraps | PASS |
| Accepted tasks present in temporary data file before exit | PASS |
| Valid bye ends the process without harness-forced exit | PASS |

Harness reported `PASS: blank input, submission, error cue, focus, layouts,
saved data; bye requested`, exit code 0. Snapshots regenerated and visually
inspected; `Ui.png` was copied to `docs/Ui.png` as the user guide screenshot.

Only presentation changed. No command syntax, response text, parsing or
persistence behavior was modified, so `test/ui-test-plan.md` needed no update
and all 20 recorded console cases passed unchanged. The error cue still
carries both the `Please check:` text and a contrasting background, so the
cue does not depend on color alone.

## Earlier run: 15 September 2026

15 September 2026, Windows, Microsoft OpenJDK 25.0.4, JavaFX 17.0.7.
Harness: `test/GuiSmoke.java`, using classes/resources from `build/libs/ari.jar`
and a new temporary working directory, never personal task data.

| Check | Result |
|---|---|
| Whitespace-only Send disabled | PASS |
| TextField action submits valid tasks and list | PASS |
| Invalid `bye extra` does not close | PASS |
| Error uses explicit `Please check:` text and contrasting color | PASS |
| Input focus restored after submission | PASS |
| 420 x 480 outer window, 404 x 441 scene | PASS; input visible, error readable |
| 900 x 720 outer window, 884 x 681 scene | PASS; long text wraps |
| Accepted tasks present in temporary data file before exit | PASS |
| Valid bye ends the process without harness-forced exit | PASS |

Snapshots: generated `build/gui-smoke/proof/Ui.png`, `minimum-error.png`,
`wide-long.png`. The first resizing attempt captured before a JavaFX pulse;
the harness now waits between phases and asserts actual scene dimensions.
The corrected snapshots were visually inspected.

An initial sandbox run could not read the JDK security configuration. The
successful run used normal host access. JavaFX emits unnamed-module, native
access and deprecated Unsafe warnings on Java 25; these did not stop startup,
interaction, persistence or exit. No dependency upgrade was made in this pass.

This is not a macOS/Linux GUI compatibility claim or an assistive-technology
audit. Real mouse interaction and teammate cross-platform checks remain useful
manual release checks; this harness exercises the TextField action handlers.

## Reproduce

Build the JAR, then compile the harness with Java 25:

```text
javac -cp build/libs/ari.jar -d build/gui-smoke test/GuiSmoke.java
```

From a NEW empty temporary working directory, run Java 25 with an absolute
classpath containing both `build/gui-smoke` and `build/libs/ari.jar`:

```text
java -ea -cp <classes-and-jar-classpath> ari.smoke.GuiSmoke <snapshot-directory>
```

Use `;` between classpath entries on Windows and `:` on Unix. The harness
refuses a working directory already containing `data`. It performs no remote
writes and exits through Ari's successful bye behavior.
