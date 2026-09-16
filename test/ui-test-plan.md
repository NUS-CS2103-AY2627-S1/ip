# UI Test Plan


## Test configuration

- Build command: `rm -rf out && javac -d out $(find src/main/java -name '*.java')`
- Default launch command: `java -cp out rambo.Rambo`
- Timeout per test case: 10 seconds
- Output includes standard output and standard error.
- Cases run in order in a fresh process and stop at the first failure.
- `ordered` matching requires each listed literal to appear after the previous one; unrelated output is allowed between literals.

## Test cases

### UI-01 — Reject an unknown main-menu command

- **Aim:** Check that an unsupported command does not crash or exit the UI.
- **Match mode:** `ordered`
- **Expected exit status:** `0`
- **Input:**

```text
x
q
```

- **Expected output:**

```text
Enter your option:
Rambo: That option doesn't exist, my friend! Try again!
Enter your option:
Stand down and recharge. Rambo out!
```

### UI-02 — Survive blank main-menu input

- **Aim:** Try to break menu parsing with an empty line; the UI should reject it and remain usable.
- **Match mode:** `ordered`
- **Expected exit status:** `0`
- **Input:**

```text

q
```

- **Expected output:**

```text
Enter your option:
Rambo: That option doesn't exist, my friend! Try again!
Enter your option:
Stand down and recharge. Rambo out!
```

### UI-03 — Reject nonnumeric task type

- **Aim:** Check malformed numeric input without losing the main-menu session.
- **Match mode:** `ordered`
- **Expected exit status:** `0`
- **Input:**

```text
2
not-a-number
q
```

- **Expected output:**

```text
Choose the type of task you want to add:
Rambo: Give me a valid task type number!
Enter your option:
Stand down and recharge. Rambo out!
```

### UI-04 — Reject blank task name

- **Aim:** Check required-field validation and recovery after a blank task name.
- **Match mode:** `ordered`
- **Expected exit status:** `0`
- **Input:**

```text
2
1

q
```

- **Expected output:**

```text
Enter your task name:
Rambo: Task name cannot be blank!
Enter your option:
Stand down and recharge. Rambo out!
```

### UI-05 — Reject task indexes at both boundaries

- **Aim:** Try zero, a negative index, and an index beyond an empty list without crashing.
- **Match mode:** `ordered`
- **Expected exit status:** `0`
- **Input:**

```text
4
0
4
-1
4
1
q
```

- **Expected output:**

```text
Enter the index of the task you want to toggle status of:
Rambo: I cannot find this task! Give a valid index!
Enter the index of the task you want to toggle status of:
Rambo: I cannot find this task! Give a valid index!
Enter the index of the task you want to toggle status of:
Rambo: I cannot find this task! Give a valid index!
Stand down and recharge. Rambo out!
```

### UI-06 — Preserve command-like and non-ASCII echo text

- **Aim:** Check that text resembling commands and Unicode text are echoed literally and do not change UI state.
- **Match mode:** `ordered`
- **Expected exit status:** `0`
- **Input:**

```text
1
q; rm -rf /
你好 👋
/exit
q
```

- **Expected output:**

```text
You: Rambo: q; rm -rf /
You: Rambo: 你好 👋
You: Back to home!
Stand down and recharge. Rambo out!
```

### UI-07 — Keep task state across operations

- **Aim:** Exercise add, list, toggle, and list as one stateful sequence.
- **Match mode:** `ordered`
- **Expected exit status:** `0`
- **Input:**

```text
2
1
buy milk
3
4
1
3
q
```

- **Expected output:**

```text
Your task has been added!
1: [T][] buy milk
Enter the index of the task you want to toggle status of:
1: [T][X] buy milk
Stand down and recharge. Rambo out!
```

### UI-08 — Handle end-of-input at the main menu

- **Aim:** Try to break the UI by closing standard input instead of entering `q`; it should terminate cleanly rather than print a stack trace.
- **Match mode:** `ordered`
- **Expected exit status:** `0`
- **Input:** empty file (immediate EOF)
- **Expected output:**

```text
Enter your option:
Stand down and recharge. Rambo out!
```

### UI-09 — Delete a task and retain the remaining task

- **Aim:** Add two tasks, delete the first, and verify that the remaining task is listed at index 1.
- **Match mode:** `ordered`
- **Expected exit status:** `0`
- **Input:**

```text
2
1
buy milk
2
1
read book
5
1
3
q
```

- **Expected output:**

```text
Your task has been added!
Your task has been added!
Enter the index of the task you want to remove:
1: [T][] read book
Stand down and recharge. Rambo out!
```

### UI-10 — Search task descriptions

- **Aim:** Check that the menu advertises search and that an inline, case-insensitive search displays matching tasks with their original numbers.
- **Match mode:** `ordered`
- **Expected exit status:** `0`
- **Input:**

```text
2
1
buy milk
2
1
read book
2
1
milk tea
3 MILK
q
```

- **Expected output:**

```text
3) List Tasks (use: 3 <keyword> to search)
1: [T][] buy milk
3: [T][] milk tea
Stand down and recharge. Rambo out!
```

### UI-11 — Report a task search with no matches

- **Aim:** Check that searching for a term absent from every task produces a clear result rather than an empty task list.
- **Match mode:** `ordered`
- **Expected exit status:** `0`
- **Input:**

```text
2
1
buy milk
3 homework
q
```

- **Expected output:**

```text
No tasks found matching "homework".
Stand down and recharge. Rambo out!
```

### UI-12 — Add and display tasks with dates

- **Aim:** Check that deadline and event tasks accept ISO dates and display them in the user-friendly date format.
- **Match mode:** `ordered`
- **Expected exit status:** `0`
- **Input:**

```text
2
2
submit report
2026-09-15
2
3
project meeting
2026-09-20
2026-09-22
3
q
```

- **Expected output:**

```text
Your task has been added!
Your task has been added!
1: [D][] submit report (by: Sep 15 2026)
2: [E][] project meeting (from: Sep 20 2026 to: Sep 22 2026)
Stand down and recharge. Rambo out!
```

### UI-13 — Reject an invalid deadline date

- **Aim:** Check that a malformed deadline date is rejected and the main-menu session remains usable.
- **Match mode:** `ordered`
- **Expected exit status:** `0`
- **Input:**

```text
2
2
submit report
15-09-2026
q
```

- **Expected output:**

```text
Enter your deadline:
Rambo: Please make sure your date is the following format (YYYY-MM-DD)
Enter your option:
Stand down and recharge. Rambo out!
```

### UI-14 — Reject an invalid event date

- **Aim:** Check that an invalid calendar date in an event range is rejected without terminating the session.
- **Match mode:** `ordered`
- **Expected exit status:** `0`
- **Input:**

```text
2
3
project meeting
2026-09-20
2026-02-30
q
```

- **Expected output:**

```text
Enter your from date:
Enter your to date:
Rambo: Please make sure your date is the following format (YYYY-MM-DD)
Enter your option:
Stand down and recharge. Rambo out!
```

## Results

- 2026-08-17 — **FAIL (environment)**
  - Java: OpenJDK 26.0.2 (required: Java 25)
  - Cases run: none; stopped before building as required by the test procedure

- 2026-08-17 — **FAIL at UI-02**
  - Java: OpenJDK 26.0.2
  - Build: PASS
  - UI-01: PASS
  - UI-02: FAIL — exit status 1; blank input caused `StringIndexOutOfBoundsException`
  - UI-03–UI-08: not run (fail-fast)

- 2026-08-17 — **FAIL at UI-06**
  - Java: OpenJDK 26.0.2
  - Build: PASS
  - UI-01–UI-05: PASS
  - UI-06: FAIL — the first echo input was consumed without being displayed
  - UI-07–UI-08: not run (fail-fast)

- 2026-08-17 — **PASS**
  - Java: OpenJDK 26.0.2
  - Build: PASS
  - UI-01–UI-08: PASS

- 2026-08-17 — **PASS**
  - Java: OpenJDK 26.0.2
  - Build: PASS
  - UI-01–UI-09: PASS

- 2026-08-24 — **PASS**
  - Java version check: skipped at user request
  - Build: PASS
  - UI-01–UI-09: PASS

- 2026-08-24 — **PASS**
  - Java version check: skipped at user request
  - Build: PASS
  - UI-01–UI-09: PASS
  - Scope: centralized `RamboException` changes

- 2026-08-26 — **PASS**
  - Java version check: skipped at user request; compiled with `--release 25`
  - Build: PASS
  - UI-01–UI-11: PASS
  - Scope: package reorganization under `rambo`

- 2026-08-26 — **PASS**
  - Java version check: skipped at user request; compiled with `--release 25`
  - Build: PASS
  - UI-01–UI-14: PASS
  - Scope: deadline and event date coverage


## Rambo GUI appearance

Launch with Java 25 using `./gradlew run`.

- Confirm the window title says `Rambo | Mission control` and the charcoal header shows a star and Rambo's name.
- Confirm the charcoal chat background, olive user bubbles, and readable light text in bot bubbles.
- Resize to the minimum window size; check that the header and input stay visible and messages wrap.
- Send `todo pack supplies`, `done 1`, `priority 1 1`, and `delete 1`; check the mission-themed replies.
- Try `help` and an invalid command; command instructions and error details should remain clear.
- Check the Send button's hover style and visible keyboard focus on the input and button.
