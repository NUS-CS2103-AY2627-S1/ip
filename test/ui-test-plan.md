# UI Test Plan

## Test environment

- Build command: `javac -d out src/main/java/*.java src/main/java/packages/exception/list/*.java` (compiles all Java files to `out/` directory)
- Launch command: `java -cp out DestroyerOfWorlds` (runs from project root with class path set to `out/`)
- Comparison rule: Exact console-output match, including prompts and line breaks, unless a test case states otherwise.

## Test cases

### TC-02: Reject duplicate todo tasks

**Aim:** Verify that adding the same todo twice does not add a second task.

**Initial state:** The task data file does not contain `buy milk`.

**Inputs:**

```text
todo buy milk
todo buy milk
```

**Expected output for the first input:**

```text
Added: [T][ ] buy milk
```

**Expected output for the second input:**

```text
Error: task already exists
```

**Expected `list` result:** Only one `buy milk` task is present.

### TC-01: List existing tasks

**Aim:** Verify that the `list` command displays all tasks with correct formatting (1-based numbering, task type, completion status, and details).

**Inputs:**

```text
list
```

**Expected output:**

```text

Hello I'm DESTROYEROFWORLDS
Prepare to meet you DOOM!
____________________________________
What do you want from me Nerd!?!?!, Can't you see I'm busy:
        ______________________________________________
        1. [D][ ] adasd (by: 12/03/2025 16:00)
        2. [E][ ] asd (from: 12/03/2000 12:00 to: 12/04/2000 15:00)
        ______________________________________________
```

