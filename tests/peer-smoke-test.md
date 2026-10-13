# Manual test checklist

Extract the download ZIP into an empty folder and open its start script, or copy
the JAR there and run `java -jar Wangsa.jar`. Check Java 25 with `java -version`.
No API key is needed.

Click the command box at the bottom. Enter these commands in order, one at a time.
Use Enter for the todo and SEND for the deadline to test both ways of submitting.

| Command or action | Expected result |
| --- | --- |
| `todo read a book` | A todo is added. |
| `deadline return book /by 2026-09-20` | A deadline is added. |
| `event lunch /from 12pm /to 1pm` | An event is added with both times. |
| `list` | Three tasks appear, numbered 1–3. |
| `find book` | The todo and deadline appear with their original numbers. |
| `mark 1`, then `unmark 1` | The todo becomes complete, then incomplete. |
| `deadline ST2334 by 2026-09-30` | A missing `/by` error appears; no task is added. |
| `deadline ST2334 /by 2026-09-31` | A date error appears because September has only 30 days; no task is added. |
| `deadline test /by 2026-02-30` | A date error appears; no task is added. |
| `mark 99` | A number error appears; tasks stay unchanged. |
| `sort`, then `list` | The deadline comes first, then the todo and event. |
| `delete 2` | The todo is removed, leaving the deadline and event. |
| `help` | Four point-form command groups with examples appear; the reply opens at its beginning. |
| Press Up, then Down in the input | Up recalls `help`; Down returns to an empty input. |
| `@ai How do I add a task?` without a key | Offline help appears. |
| Resize the window and scroll | Messages wrap; Due/From/To align with their task title. Symbols, header, and input stay aligned. Help cards stack on narrow windows. |
| With a long conversation, place the pointer over the chat and scroll up before touching the scrollbar | Earlier messages appear immediately with the mouse wheel or trackpad. Record any need to drag the scrollbar first. |
| `bye`, then restart the same JAR from another working folder | The deadline and event are restored from the text file beside the JAR. |

Try a few commands from the [user guide](../docs/README.md) as well.
For any problem, record the exact command, expected result, and actual result.
The valid September example is `deadline ST2334 /by 2026-09-30`.
Do not add it during the sequence above unless you also adjust the expected task counts.

## Cases adapted to Wangsa

This sequence adapts the supplied test-report example to Wangsa's documented
commands. Start in a separate empty folder; do not continue from the checklist
above, because the task numbers and counts would differ.

The expected results below were checked by Codex through the released v0.6
JAR's terminal interface on 2026-09-18, using Java 25.0.4 and disposable data.
All 14 command checks matched these results. The GUI shares the parser and task
service, but this terminal check does not certify Windows clicking, rendering,
or scrolling. It is separate from the author's report of successful peer testing.

| # | Command or action | Expected result |
| --- | --- | --- |
| 1 | `todo buy ingredients` | Adds the todo; one task is stored. |
| 2 | `deadline submit report /by 2026-09-23` | Adds the deadline for 23 September 2026; two tasks are stored. |
| 3 | Type `sort` followed by one trailing space | Succeeds. The deadline becomes task 1 and the todo becomes task 2. Surrounding whitespace is accepted. |
| 4 | `todo cook \| food` | Adds a todo whose description preserves the literal pipe; three tasks are stored. |
| 5 | `todo set [table]` | Adds a todo whose description preserves the brackets; four tasks are stored. |
| 6 | `list` | Shows `submit report`, `buy ingredients`, `cook \| food`, and `set [table]`, numbered 1–4 in that order. |
| 7 | `sort` | Succeeds and retains that order: deadlines first, then undated tasks in their existing order. |
| 8 | `event camp /from 23/9/2026 /to 25/9/2026` | Adds the event as task 5. Event start/end values are free-form text; Wangsa does not validate or reorder these dates. |
| 9 | `find cook` | Shows only `cook \| food`, with its full-list number 3. |
| 10 | `mark 1` | Marks `submit report` as done. |
| 11 | `unmark 1` | Marks `submit report` as incomplete again. |
| 12 | `mark 0` | Reports that the task number must be between 1 and 5; tasks remain unchanged. |
| 13 | `mark 100` | Reports the same range error because this sequence has only five tasks; tasks remain unchanged. |
| 14 | `delete 1` | Removes `submit report`; the remaining four tasks are renumbered 1–4. |

### Syntax checks from the supplied example

These commands have different expected results in Wangsa. Codex also checked
all three against the same released JAR; each was correctly rejected without
changing the four remaining tasks.

| Command or action | Expected result |
| --- | --- |
| `deadline submit report /by 23/9/2026` | Rejects the date format. Deadlines require `YYYY-MM-DD`, such as `2026-09-23`. |
| `sort /by date` followed by a trailing space | Rejects the unsupported arguments. The trailing space is not the cause. |
| `sort /by date` without a trailing space | Also rejects the unsupported arguments. Wangsa's sort command is just `sort`. |

After this sequence, Codex also checked `help`, `@ai How do I add a task?`
without an API key, and `bye`. Offline command help and normal exit worked.
Restarting the same JAR from a different working directory restored all four
remaining tasks, including the pipe, brackets, and event values.

Codex also ran the existing graphical release smoke runner against this exact
downloaded JAR on macOS with desktop access. Its normal, reload, and blocked-data
scenarios passed, including layout alignment, long text, help, and resizing.
The runner submits commands programmatically; it does not test physical
mouse-wheel/trackpad scrolling or replace the Windows peer's observations.

## Windows peer test record

- Tester: [@lingsongc](https://github.com/lingsongc).
- Report confirmed: 2026-09-18. The supplied screenshot shows messages at
  01:06–01:07; it does not display the calendar date of the test.
- OS and processor: Microsoft Windows 11 Pro, version 10.0.26200, 64-bit OS;
  processor details not supplied.
- Java version: 25.0.4.1 (2026-08-18 LTS), as reported by the tester.
- Release or commit: v0.6 / `c1f5719f8187895d82140c6450227959831ce8e2`.
- JAR SHA-256: `8459662741ecf2c7e9a85a5d3294688b7a8a3f33397389998985920951acdd34`.
  The supplied checksum matches the published v0.6 JAR exactly.
- Result: **PASS (peer-reported).** The author confirms that @lingsongc tested
  the released v0.6 JAR on Windows and that it is "all good and works".
  The screenshot corroborates the Windows version, Java version, and JAR
  checksum; the successful test outcome is the author's separate confirmation.
- Scope: This is an overall peer smoke-test result. The detailed command and
  graphical checks above were independently performed by Codex and are not
  attributed to the peer as a verbatim checklist submission.
- Earlier observation: The supplied example included scrolling working only
  after dragging the scrollbar. Its applicability to Wangsa was not established.
  The latest author confirmation reports the app working; no scrolling code
  change or independently verified scrolling fix is claimed here.

The example's slash-formatted deadline and `sort /by date` are not valid Wangsa
commands. Use the adapted cases above when describing this project's behavior.

## Additional issue #471 tester credits

The following students also reported Windows smoke-test results on the public
[release-testing request](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/471):

- [@Papangkorn-Pann](https://github.com/Papangkorn-Pann): Windows 11 Home Single
  Language on AMD64 with Java 25.0.4.1; reported a successful launch and normal
  overall behavior, with a suggestion about grouping `event` beside task-creation commands.
- [@Dancodes2](https://github.com/Dancodes2): Windows 11 Pro with OpenJDK 25.0.4.1;
  reported normal GUI behavior and identified the low-severity CLI `help` punctuation
  encoding issue. The source fix renders terminal help with ASCII punctuation.
- [@LINGSIHAN](https://github.com/LINGSIHAN): Windows 11 with Java 25.0.4.1;
  reported that the documented commands, help, and persistence behavior worked.

These are tester-reported observations for the published v0.6 JAR. They are credited
separately from the v0.7 release verification below.

## v0.7 issue #471 follow-up

Version 0.7 includes the ASCII-safe terminal help fix and preserved dependency
notices. Before publication, its exact shared JAR is tested from an empty folder
by the release workflow on Windows, Linux, Intel Mac, and Apple Silicon Mac. The
same artifact's archive contents and terminal behavior are also checked locally
on macOS with Java 25. Together, these checks cover GUI startup and commands,
CLI interoperability, saving and reloading, blocked storage, offline AI help,
dependency notices, and optional ZIP contents.

The [v0.7 release page](https://github.com/Manutd1234/ip/releases/tag/v0.7)
records the exact successful workflow run and SHA-256 digest. This is automated
cross-platform verification, not a claim that the v0.6 peer testers manually
retested v0.7. Their original contribution remains credited above.
