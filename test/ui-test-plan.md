# Text UI test plan

This file records how the chatbot's text user interface is tested, and every
test case that is run against it. It is the single source of truth for the
`test-ui` skill: the runner reads the settings and the test cases below, so
adding a test case here is all that is needed to have it run.

## Program under test

- **Main class:** `bob.Bob`
- **Source directory:** `src/main/java`
- **Data file:** `data/bob.txt`

The chatbot now has two front ends, and this plan tests the console one. The
main class above is therefore `bob.Bob`, which still holds the whole
conversation at a console, and not the `bob.Launcher` that `./gradlew run` and
the packaged jar start — that one opens a window, which prints nothing for a
test case to compare against.

Both front ends run every command through the same method, so what the chatbot
*says* is the same in either; only where it is shown differs. That is what makes
this plan worth as much as it was before the window existed: a change that
breaks an answer breaks it here too. What this plan cannot see is anything about
the window itself — the speech bubbles, the input box, the scrolling — and the
methods the window talks through, which print nothing and so are covered by
`BobTest` instead.

The program is compiled fresh into a temporary directory before the session
starts, and is started again for each test case, so no test case can be
affected by tasks left over from an earlier one.

Each test case also runs in a working directory of its own. The program keeps
its task list in the data file named above, relative to the directory it is
started in, so a directory per test case is what stops one test case from
loading the tasks another one saved — and it keeps a test run from writing
anything into the repository.

The test cases that have the program print the path of the data file (TC22,
TC38, TC40 and TC43) expect it written with `/` between the folder and the file name. That
is how the path prints on macOS and Linux; on Windows the same path prints as
`data\bob.txt`, because the program builds it from its parts and lets the
operating system supply the separator. On Windows, expect those test cases to
report a difference in the lines naming the file only.

## The save file

Because the task list now outlives a run of the program, a test case may say
what is on the disk on either side of the run:

- a **Data file before** block is written to `data/bob.txt` before the program
  starts, standing for tasks saved in an earlier session;
- a **Data file after** block is compared with `data/bob.txt` once the program
  has finished, and the test case fails if it does not match. The block may be
  the single line `(no file)`, which says the program should have left no save
  file at all.

A program that finds a damaged save file also copies it to `data/bob.txt.bak`.
The runner does not compare that copy, so what it holds is checked by the JUnit
tests in `StorageTest` instead.

A save file the program is not allowed to read, which it refuses to save over, has
no test case here either: the runner can write a Data file before but cannot take
away its permissions. `StorageTest` checks that case instead.

For the same reason no test case here makes a save fail, so what `bye` does after
one — trying the save again, and holding back quitting once if it still fails — is
checked in `BobTest`, through the methods a window uses.

A test case with neither block starts with no save file — the ordinary first
run — and nothing is checked about what it saves. That is the case for TC1 to
TC16 below, all of which were written before the chatbot saved anything, and
for TC23 to TC31 apart from TC27.

## How to run the tests

From the repository root, with Java 25 active (`sdk use java 25.0.3.fx-zulu`):

```bash
python3 .claude/skills/test-ui/scripts/run-ui-tests.py
```

Add `--only TC3` to run a single test case while working on it.

## How output is compared

Each test case feeds its **Input** lines to the program on standard input, as
if the user had typed them, and compares everything the program prints with
the test case's **Expected output**.

The comparison is exact, except that trailing spaces at the end of a line and
blank lines at the very end of the output are ignored. Those are invisible on
screen, so a difference in them is not a real difference in what the user sees.
This matters here because the ASCII-art banner lines end in spaces.

The session stops at the first failing test case and reports the expected and
actual output side by side, so the failure stays visible instead of scrolling
away behind later test cases.

## Test cases

### TC1 - Greeting and farewell

**Aim:** Check that the chatbot greets the user on startup and says goodbye
when the user types `bye`, with nothing in between.

**Input**

```text
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC2 - Add one task of each type and list them

**Aim:** Check that `todo`, `deadline` and `event` each store a task of the right
kind, confirm it with its own display form and the running task count, and
that `list` prints them in the order they were added, numbered from 1.

**Input**

```text
todo read book
deadline return book /by 2026-12-02
event project meeting /from 2026-08-06 1400 /to 2026-08-06 1600
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] return book (by: Dec 02 2026)
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)
     Now yu have 3 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][ ] read book
     2.[D][ ] return book (by: Dec 02 2026)
     3.[E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC3 - Mark a task as done and undo it

**Aim:** Check that `mark 1` sets the task's status box to `[X]` and `unmark 1`
sets it back to `[ ]`, that each is confirmed with the right wording, and that
the change is visible in a following `list`. The type box is unaffected.

**Input**

```text
todo read book
list
mark 1
list
unmark 1
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Kanpai! Dis one finish:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Bi-do! Dis one not finish yet:
       [T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC4 - List when nothing has been added

**Aim:** Check that `list` on an empty task store says so instead of printing
an empty numbered list.

**Input**

```text
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     No tasks yet! Bob bored...
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC5 - Bad arguments to mark and unmark, and an unrecognised line

**Aim:** Check that a task number that does not exist, a task number that is not a
number, and a `mark`/`unmark` with no number at all are each answered with their
own explanation instead of crashing or being lumped together. Also checks that a
line which is not one of the known commands is reported as such: now that every
task is added with `todo`, `deadline` or `event`, a bare line is no longer stored
as a task the way it used to be.

**Input**

```text
todo read book
mark 5
mark two
unmark
borrow book
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I don't have a task numbered 5.
     Your list runs from 1 to 1; type list to see it.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     "two" isn't a task number.
     I need the number shown next to the task in list, for example: mark 2
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     Which task should I unmark?
     Give me its number from list, for example: unmark 2
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     Sorry, I don't know what "borrow book" means.
     Try one of: todo, deadline, event, list, on, before, after, next, find, mark, unmark, delete, edit, bye
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC6 - Input ends without bye

**Aim:** Check that the chatbot still says goodbye and exits normally when the
input runs out before a `bye` line, which is what happens when input is piped
in from a file rather than typed.

**Input**

```text
todo read book
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC7 - Missing description or date parts

**Aim:** Check that an add command with a missing description, a missing `/by`, or a
missing `/to` is answered with an explanation of the part that is missing and an
example of the right form, instead of storing a half-filled task or crashing. The
`list` at the end checks that nothing was stored by the refused commands.

**Input**

```text
todo
deadline return book
deadline /by Sunday
event project meeting /from Mon 2pm
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     A todo needs a description — tell me what to do.
     Like dis: todo borrow book
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     A deadline needs a due date, written after /by.
     Like dis: deadline return book /by 2026-12-02
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     A deadline needs a description, written before /by.
     Like dis: deadline return book /by 2026-12-02
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     An event needs an end time, written after /to at the end.
     Like dis: event project meeting /from 2026-12-02 1800 /to 2026-12-02 2000
    ____________________________________________________________

    ____________________________________________________________
     No tasks yet! Bob bored...
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC8 - Dates are understood rather than just repeated

**Aim:** Check that a date written as `yyyy-mm-dd` is stored as a real date and
shown back in a friendlier form than it was typed in, that a time of day is kept
when one is given and left off when it is not, and that text which is not a date
at all is now refused with an explanation. That last case is the behaviour this
increment deliberately gives up: `no idea :-p` used to be stored and shown back
unchanged, because a due date was only ever a string.

**Input**

```text
deadline return book /by 2026-12-02 1800
event orientation week /from 2026-10-04 /to 2026-10-11
deadline do homework /by no idea :-p
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] return book (by: Dec 02 2026 18:00)
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [E][ ] orientation week (from: Oct 04 2026 to: Oct 11 2026)
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I don't understand "no idea :-p" as a date.
     Write the day as yyyy-mm-dd, and add a 24-hour time if the hour matters.
     Like dis: 2026-12-02 or 2026-12-02 1800
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[D][ ] return book (by: Dec 02 2026 18:00)
     2.[E][ ] orientation week (from: Oct 04 2026 to: Oct 11 2026)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC9 - The remaining ways an add command can be incomplete

**Aim:** Check the malformed add commands that TC7 does not cover — a `/by` with
nothing after it, an event with no description, and an event whose `/from` is
missing while `/to` is present — each get the explanation that fits them, rather
than one catch-all message. Also checks that `todolist` is treated as an unknown
command and not as a `todo` whose description is `list`, and that `mark` on an
empty list says the list is empty rather than quoting a range of task numbers
that does not exist.

**Input**

```text
deadline return book /by
event /from Mon /to Tue
event party /to 4pm
todolist
mark 0
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     You wrote /by but not when it is due.
     Like dis: deadline return book /by 2026-12-02
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     An event needs a description, written before /from.
     Like dis: event project meeting /from 2026-12-02 1800 /to 2026-12-02 2000
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     An event needs a start time, written after /from.
     Like dis: event project meeting /from 2026-12-02 1800 /to 2026-12-02 2000
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     Sorry, I don't know what "todolist" means.
     Try one of: todo, deadline, event, list, on, before, after, next, find, mark, unmark, delete, edit, bye
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     There is nothing to mark yet — your list is empty.
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC10 - Empty and blank input lines

**Aim:** Check that pressing Enter on its own, or typing only spaces, is answered
with a prompt to type something rather than with "I don't know what "" means",
and that the conversation carries on normally afterwards.

**Input**

```text

   
todo read book
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     You didn't type anything.
     Tell me about a task, or type list to see the ones I already have.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     You didn't type anything.
     Tell me about a task, or type list to see the ones I already have.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC11 - Refused commands leave the list untouched

**Aim:** Check that errors do not quietly change what the chatbot has stored.
Good and bad commands are interleaved, and the running task count in each
confirmation, together with the `list` at the end, shows that every refused
command added nothing and marked nothing. In particular the failed `mark 3`
must not affect the task that `mark 2` later marks. The closing `unmark 1` is
answered with the note that there is nothing to change, which is itself the
evidence that nothing refused along the way marked task 1.

**Input**

```text
todo read book
todo
deadline return book /by 2026-12-02
deadline /by
mark 3
mark 2
event project meeting /from Mon 2pm
unmark 1
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     A todo needs a description — tell me what to do.
     Like dis: todo borrow book
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] return book (by: Dec 02 2026)
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     A deadline needs a description, written before /by.
     Like dis: deadline return book /by 2026-12-02
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I don't have a task numbered 3.
     Your list runs from 1 to 2; type list to see it.
    ____________________________________________________________

    ____________________________________________________________
     Kanpai! Dis one finish:
       [D][X] return book (by: Dec 02 2026)
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     An event needs an end time, written after /to at the end.
     Like dis: event project meeting /from 2026-12-02 1800 /to 2026-12-02 2000
    ____________________________________________________________

    ____________________________________________________________
     Dis one not finish yet! Nothing to change:
       [T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][ ] read book
     2.[D][X] return book (by: Dec 02 2026)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC12 - Delete a task from the middle of the list

**Aim:** Check the worked example from the requirements: `delete 3` removes the
third task, confirms it by showing the task itself, and reports the number of
tasks left. The `list` afterwards checks that the tasks below the deleted one
move up, so the numbering stays an unbroken run from 1 with no gap where the
deleted task used to be. Showing the removed task in the confirmation matters
precisely because of that renumbering: after a deletion, the number the user
typed no longer refers to the same task.

**Input**

```text
todo read book
deadline return book /by 2026-06-06
event project meeting /from 2026-08-06 1400 /to 2026-08-06 1600
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
list
delete 3
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] return book (by: Jun 06 2026)
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)
     Now yu have 3 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] join sports club
     Now yu have 4 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] borrow book
     Now yu have 5 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Kanpai! Dis one finish:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Kanpai! Dis one finish:
       [D][X] return book (by: Jun 06 2026)
    ____________________________________________________________

    ____________________________________________________________
     Kanpai! Dis one finish:
       [T][X] join sports club
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2026)
     3.[E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)
     4.[T][X] join sports club
     5.[T][ ] borrow book
    ____________________________________________________________

    ____________________________________________________________
     Bee-do! Bob throw away dis:
       [E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)
     Now yu have 4 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2026)
     3.[T][X] join sports club
     4.[T][ ] borrow book
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC13 - Bad arguments to delete

**Aim:** Check that `delete` rejects the same four mistakes as `mark` and
`unmark` — a task number given on an empty list, no number at all, something
that is not a number, and a number outside the list — each with the explanation
that fits it and naming `delete` rather than another command. The `list` at the
end checks that no refused `delete` removed anything.

**Input**

```text
delete 1
todo read book
delete
delete two
delete 0
delete 2
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     There is nothing to delete yet — your list is empty.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     Which task should I delete?
     Give me its number from list, for example: delete 2
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     "two" isn't a task number.
     I need the number shown next to the task in list, for example: delete 2
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I don't have a task numbered 0.
     Your list runs from 1 to 1; type list to see it.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I don't have a task numbered 2.
     Your list runs from 1 to 1; type list to see it.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC14 - Deleting the first and last tasks, emptying the list, and adding again

**Aim:** Check the ends of the list, which the middle deletion in TC12 does not
reach: deleting the first task and deleting the last one. Then check that a task
added after deletions is counted against how many tasks there actually are, not
against how many have ever been added — the count would keep climbing if it were
kept as a separate running total instead of being read from the list itself.
Finally, check that deleting the last remaining task leaves the list genuinely
empty, so `list` says so rather than printing nothing.

**Input**

```text
todo read book
todo return book
todo borrow book
delete 1
delete 2
list
todo join sports club
list
delete 1
delete 1
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] return book
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] borrow book
     Now yu have 3 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Bee-do! Bob throw away dis:
       [T][ ] read book
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Bee-do! Bob throw away dis:
       [T][ ] borrow book
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][ ] return book
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] join sports club
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][ ] return book
     2.[T][ ] join sports club
    ____________________________________________________________

    ____________________________________________________________
     Bee-do! Bob throw away dis:
       [T][ ] return book
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Bee-do! Bob throw away dis:
       [T][ ] join sports club
     Now yu have 0 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     No tasks yet! Bob bored...
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC15 - A deleted task is gone for good, and marking follows the new numbering

**Aim:** Check that the numbers `mark` and `delete` take are read against the
list as it stands *now*, not as it stood before a deletion. After `delete 1`,
`mark 1` must mark what used to be task 2, and the highest number that was valid
before the deletion must no longer be. Also checks that a deleted task does not
come back: nothing in a later `list` shows it.

**Input**

```text
todo read book
deadline return book /by 2026-12-02
event project meeting /from 2026-08-06 1400 /to 2026-08-06 1600
delete 1
mark 1
mark 3
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] return book (by: Dec 02 2026)
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)
     Now yu have 3 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Bee-do! Bob throw away dis:
       [T][ ] read book
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Kanpai! Dis one finish:
       [D][X] return book (by: Dec 02 2026)
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I don't have a task numbered 3.
     Your list runs from 1 to 2; type list to see it.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[D][X] return book (by: Dec 02 2026)
     2.[E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC16 - Commands that take no arguments reject trailing text

**Aim:** Check that `list` and `bye`, which stand alone, are recognised only when
typed exactly. `bye now` must not end the conversation and `list foo` must not
list anything; each is answered by saying the command takes nothing after it.
The commands after them prove the conversation carried on, which is the visible
consequence of `bye now` not being taken as `bye`. Also checks that command words
are case-sensitive, so `BYE` is not `bye`, though the answer suggests the
lowercase word. This is the counterpart to the `todolist` case in TC9: there, a
command that *does* take arguments must not match a longer word; here, a command
that takes none must not match a longer line.

**Input**

```text
bye now
list foo
BYE
todo read book
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     bye takes nothing after it.
     Like dis: bye
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     list takes nothing after it.
     Like dis: list
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     Sorry, I don't know what "BYE" means.
     Try one of: todo, deadline, event, list, on, before, after, next, find, mark, unmark, delete, edit, bye
     Commands are lowercase. Did yu mean bye?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC17 - Every change to the list is written to the save file

**Aim:** Check that the tasks are on the disk as soon as they are entered, rather
than only when the chatbot is shut down properly. The save file is checked after
a run that adds one task of each kind and marks one of them, so it also checks
that each kind is written with its own fields and that a `mark` is saved too.
The console output is unchanged by saving: the user is told nothing about a save
that worked, because there is nothing they need to do about it.

**Input**

```text
todo read book
deadline return book /by 2026-06-06
event project meeting /from 2026-08-06 1400 /to 2026-08-06 1600
mark 1
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] return book (by: Jun 06 2026)
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)
     Now yu have 3 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Kanpai! Dis one finish:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
T | 1 | read book
D | 0 | return book | 2026-06-06
E | 0 | project meeting | 2026-08-06 1400 | 2026-08-06 1600
```

### TC18 - Tasks saved earlier are there again at startup

**Aim:** Check the other half of the round trip: a save file written by an earlier
session is read back into the list, with each task's kind, done status and dates
as they were saved. The user is told that tasks were picked up, since a list that
was not empty to begin with would otherwise be a surprise. The save file is
checked afterwards as well, to confirm that merely reading and listing tasks does
not rewrite it.

**Data file before**

```text
T | 1 | read book
D | 0 | return book | 2026-06-06
E | 0 | project meeting | 2026-08-06 1400 | 2026-08-06 1600
```

**Input**

```text
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Bob found 3 tasks from before.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][X] read book
     2.[D][ ] return book (by: Jun 06 2026)
     3.[E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
T | 1 | read book
D | 0 | return book | 2026-06-06
E | 0 | project meeting | 2026-08-06 1400 | 2026-08-06 1600
```

### TC19 - Deleting and unmarking are saved, and an emptied list empties the file

**Aim:** Check that the save file follows the list down as well as up. A task
deleted from a loaded list is gone from the file, an `unmark` is written back
just as a `mark` is, and deleting the last task leaves an empty file rather than
a file still holding the task that was deleted. Leaving the old contents behind
would be the failure that matters here: the task would come back at the next
startup.

**Data file before**

```text
T | 1 | read book
D | 0 | return book | 2026-06-06
```

**Input**

```text
unmark 1
delete 2
delete 1
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Bob found 2 tasks from before.
    ____________________________________________________________

    ____________________________________________________________
     Bi-do! Dis one not finish yet:
       [T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Bee-do! Bob throw away dis:
       [D][ ] return book (by: Jun 06 2026)
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Bee-do! Bob throw away dis:
       [T][ ] read book
     Now yu have 0 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     No tasks yet! Bob bored...
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
```

### TC20 - A session that changes nothing writes no save file

**Aim:** Check that starting the chatbot and quitting without adding anything
does not create a save file. A first run should leave the disk as it found it,
and neither a `list` that finds nothing nor a `mark` or an `edit` refused for want
of a task is a change to be written. This is also what lets every test case above
it start from a genuinely empty state.

**Input**

```text
list
mark 1
edit 1 /desc read book
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     No tasks yet! Bob bored...
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     There is nothing to mark yet — your list is empty.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     There is nothing to edit yet — your list is empty.
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
(no file)
```

### TC21 - Bars and backslashes in a task survive the round trip

**Aim:** Check the characters that mean something in the save file itself. A
vertical bar separates the fields of a saved task and a backslash marks the
character after it as ordinary, so a task containing either would split a saved
line in the wrong places if it were written as typed. Both are written as escape
sequences and read back as the characters the user typed: the tasks loaded from
the file below show the bar and the backslash again in `list`, and the task added
during the run is written back escaped in the same way.

**Data file before**

```text
T | 0 | tidy up \| then rest
D | 1 | back up C:\\work | 2026-12-02
```

**Input**

```text
list
todo a \ b | c
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Bob found 2 tasks from before.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][ ] tidy up | then rest
     2.[D][X] back up C:\work (by: Dec 02 2026)
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] a \ b | c
     Now yu have 3 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
T | 0 | tidy up \| then rest
D | 1 | back up C:\\work | 2026-12-02
T | 0 | a \\ b \| c
```

### TC22 - Lines that cannot be read are reported and skipped

**Aim:** Check that a damaged save file costs the user only the damaged lines.
Each way a line can be wrong — a kind of task the chatbot does not know, too few
fields for the kind it claims to be, a status field that is neither `1` nor `0`,
an empty description, and a date that is not a date — is reported on its own,
naming the line so the user can find it, and the good tasks around it are still
loaded. The last of those is new: a deadline due `someday` was a readable line
before this increment, and is not one now that dates are understood. Its message
is the short one written for someone repairing the file, not the three lines of
advice the chatbot gives someone typing a command. A blank line is not damage and
is passed over in silence. The warning that the skipped lines will be dropped is the
point of reporting them at all: the next command that changes the list rewrites
the whole file without them. So the file is first copied, as it was, to
`data/bob.txt.bak`, and the report ends by saying where the copy is.

**Data file before**

```text
T | 1 | read book
X | 0 | who knows
D | 0 | return book
T | 2 | maybe done
T | 0 |
D | 0 | pay bills | someday

E | 0 | party | 2026-12-06 2000 | 2026-12-06 2300
```

**Input**

```text
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Bob found 2 tasks from before.
     Line 2 of data/bob.txt isn't a task I can read: "X" is not a kind of task I know (I know T, D and E).
     Line 3 of data/bob.txt isn't a task I can read: a saved deadline has 4 fields, but this line has 3.
     Line 4 of data/bob.txt isn't a task I can read: "2" doesn't say whether the task is done (it should be 1 or 0).
     Line 5 of data/bob.txt isn't a task I can read: the description is empty.
     Line 6 of data/bob.txt isn't a task I can read: the due date "someday" isn't a date (dates are saved as 2026-12-02, or 2026-12-02 1800 with a time).
     I've left those 5 lines out of your list.
     They'll be dropped from data/bob.txt the next time the list changes, but the copy still has them.
     The file as it was is kept in data/bob.txt.bak, so nothing in it is lost.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][X] read book
     2.[E][ ] party (from: Dec 06 2026 20:00 to: Dec 06 2026 23:00)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
T | 1 | read book
X | 0 | who knows
D | 0 | return book
T | 2 | maybe done
T | 0 |
D | 0 | pay bills | someday

E | 0 | party | 2026-12-06 2000 | 2026-12-06 2300
```

### TC23 - Dates that look like dates but are not

**Aim:** Check the four ways a date can be wrong without being obvious nonsense:
a day that never happened, a time of day that does not exist, a date written the
way half the world writes it but the chatbot does not read, and a date with an
extra word tacked on to it. The first two are in the accepted form, so each is
told what is wrong with the day or the time it names rather than asked again for
the form. The other two are quoted back in full, so a user who typed two dates on
one line can see which of them was not understood. The last command shows that
the chatbot still accepts a well-formed date after all of that, and the `list`
shows that none of the refused commands stored anything.

**Input**

```text
deadline pay fees /by 2026-02-30
deadline pay fees /by 2026-12-02 2500
deadline pay fees /by 2/12/2026
deadline pay fees /by 2026-12-02 1800 sharp
deadline pay fees /by 2026-12-02
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     2026-02-30 isn't a real day: February 2026 has 28 days.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     2500 isn't a real time: hours go up to 23 and minutes up to 59.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I don't understand "2/12/2026" as a date.
     Write the day as yyyy-mm-dd, and add a 24-hour time if the hour matters.
     Like dis: 2026-12-02 or 2026-12-02 1800
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I don't understand "2026-12-02 1800 sharp" as a date.
     Write the day as yyyy-mm-dd, and add a 24-hour time if the hour matters.
     Like dis: 2026-12-02 or 2026-12-02 1800
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] pay fees (by: Dec 02 2026)
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[D][ ] pay fees (by: Dec 02 2026)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC24 - Tasks falling on a specific day

**Aim:** Check that `on` picks out the tasks falling on one day and leaves the
rest alone. A deadline falls on the day it is due, whether or not it carries a
time; an event falls on every day it is running, so a day in the middle of a
week-long event finds it, which is the case a comparison against the start alone
would miss; and a todo, having no date, is never listed. A day nothing falls on
says so rather than printing an empty listing. The numbers shown are the tasks'
numbers in the full list, not 1, 2, 3 counting the matches — the `mark 3` proves
it, by marking the task that the listing above it numbered 3, and the `on` after
it shows that task's box now ticked.

**Input**

```text
todo read book
deadline return book /by 2026-12-02
deadline submit report /by 2026-12-02 0900
event orientation week /from 2026-10-04 /to 2026-10-11
on 2026-12-02
on 2026-10-06
on 2026-11-01
mark 3
on 2026-12-02
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] return book (by: Dec 02 2026)
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] submit report (by: Dec 02 2026 09:00)
     Now yu have 3 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [E][ ] orientation week (from: Oct 04 2026 to: Oct 11 2026)
     Now yu have 4 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Luk! On Dec 02 2026 yu have:
     2.[D][ ] return book (by: Dec 02 2026)
     3.[D][ ] submit report (by: Dec 02 2026 09:00)
    ____________________________________________________________

    ____________________________________________________________
     Luk! On Oct 06 2026 yu have:
     4.[E][ ] orientation week (from: Oct 04 2026 to: Oct 11 2026)
    ____________________________________________________________

    ____________________________________________________________
     Nothing on Nov 01 2026. Bob go play!
    ____________________________________________________________

    ____________________________________________________________
     Kanpai! Dis one finish:
       [D][X] submit report (by: Dec 02 2026 09:00)
    ____________________________________________________________

    ____________________________________________________________
     Luk! On Dec 02 2026 yu have:
     2.[D][ ] return book (by: Dec 02 2026)
     3.[D][X] submit report (by: Dec 02 2026 09:00)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC25 - Tasks falling before a specific day

**Aim:** Check that `before` lists what comes earlier than a day, and that the
day named is itself excluded — `before 2026-12-02` must not show the deadline due
on the 2nd, so that `before` and `on` for one day never show the same *deadline*
twice. An event is placed by the day it starts, so a multi-day event that has
already begun is listed by both, which is what `on` is for: the day asked about is
one the event is running on, and it also comes after the day the event started.
Checked from both sides: the same task does appear once the day asked about is
moved past it. As with `on`, a todo has no date and so is never listed, and a day
with nothing before it says so.

**Input**

```text
deadline return book /by 2026-12-02
event party /from 2026-12-01 2000 /to 2026-12-01 2300
todo read book
before 2026-12-02
before 2026-12-01
before 2027-01-01
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] return book (by: Dec 02 2026)
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [E][ ] party (from: Dec 01 2026 20:00 to: Dec 01 2026 23:00)
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 3 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Bob look before Dec 02 2026. Yu have:
     2.[E][ ] party (from: Dec 01 2026 20:00 to: Dec 01 2026 23:00)
    ____________________________________________________________

    ____________________________________________________________
     Nothing before Dec 01 2026. Yay!
    ____________________________________________________________

    ____________________________________________________________
     Bob look before Jan 01 2027. Yu have:
     1.[D][ ] return book (by: Dec 02 2026)
     2.[E][ ] party (from: Dec 01 2026 20:00 to: Dec 01 2026 23:00)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC26 - The most urgent tasks, soonest first

**Aim:** Check that `next` reorders the dated tasks by date rather than showing
them in the order they were added — the tasks below are deliberately entered in
the wrong order, so a listing that merely filtered would come out differently.
Two of them fall on the same day, one with a time and one without, and the one
without comes first. Asking for more tasks than there are shows all of them and
says how many that is, rather than complaining, and asking for one uses the
singular. The three deletions then take every dated task away, so the last `next`
checks the case where nothing has a date. Throughout, the numbers shown are the
tasks' numbers in the full list, so they run out of order here — that is the
point of them, since `1` in a `next` listing would otherwise mean a different
task from `1` in `list`.

**Input**

```text
deadline submit report /by 2026-12-02 0900
event orientation week /from 2026-10-04 /to 2026-10-11
deadline return book /by 2026-12-02
todo read book
next 2
next 99
next 1
delete 2
delete 1
delete 1
next 3
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] submit report (by: Dec 02 2026 09:00)
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [E][ ] orientation week (from: Oct 04 2026 to: Oct 11 2026)
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] return book (by: Dec 02 2026)
     Now yu have 3 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 4 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Bee-do bee-do! 2 most urgent, soonest first:
     2.[E][ ] orientation week (from: Oct 04 2026 to: Oct 11 2026)
     3.[D][ ] return book (by: Dec 02 2026)
    ____________________________________________________________

    ____________________________________________________________
     Bee-do bee-do! 3 most urgent, soonest first:
     2.[E][ ] orientation week (from: Oct 04 2026 to: Oct 11 2026)
     3.[D][ ] return book (by: Dec 02 2026)
     1.[D][ ] submit report (by: Dec 02 2026 09:00)
    ____________________________________________________________

    ____________________________________________________________
     Bee-do bee-do! Most urgent:
     2.[E][ ] orientation week (from: Oct 04 2026 to: Oct 11 2026)
    ____________________________________________________________

    ____________________________________________________________
     Bee-do! Bob throw away dis:
       [E][ ] orientation week (from: Oct 04 2026 to: Oct 11 2026)
     Now yu have 3 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Bee-do! Bob throw away dis:
       [D][ ] submit report (by: Dec 02 2026 09:00)
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Bee-do! Bob throw away dis:
       [D][ ] return book (by: Dec 02 2026)
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     No dates on tasks. No bee-do!
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC27 - Bad arguments to on, before, after and next

**Aim:** Check that each way of misusing the four listing commands is answered with
the explanation that fits it. `on`, `before` and `after` reject a missing day and
text that is not a day, each naming the command that was typed in its example. They
also reject a day with a time tacked on to it: these commands answer questions about
whole days, and quietly ignoring the `1800` would hide that the question asked was
not the question answered. `next` rejects a missing count, a count that is not a
number, and counts of zero and below, which ask for nothing at all. A count below
one is refused for being below one however long it is written: `-99999999999` was
told it was not a number of tasks, which it plainly is, while `-2` was told it was
too small. A count written with a `+` is read as the number it is and answered, so
`next +5` on an empty list is told there are no dates rather than refused. The
`list` at the end shows that no refused command stored anything, and the absent save
file shows that these four commands write nothing to the disk — they only look at
the list, so there is never anything to save.

**Input**

```text
on
on someday
on 2026-12-02 1800
before
before 2/12/2026
after
after someday
next
next lots
next 0
next -2
next -99999999999
next +5
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     Which day should I look at?
     Like dis: on 2026-12-02
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I don't understand "someday" as a day.
     Write the day as yyyy-mm-dd, with no time after it.
     Like dis: 2026-12-02
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I don't understand "2026-12-02 1800" as a day.
     Write the day as yyyy-mm-dd, with no time after it.
     Like dis: 2026-12-02
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     Which day should I look at?
     Like dis: before 2026-12-02
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I don't understand "2/12/2026" as a day.
     Write the day as yyyy-mm-dd, with no time after it.
     Like dis: 2026-12-02
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     Which day should I look at?
     Like dis: after 2026-12-02
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I don't understand "someday" as a day.
     Write the day as yyyy-mm-dd, with no time after it.
     Like dis: 2026-12-02
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     How many tasks should I show?
     Like dis: next 3
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     "lots" isn't a number of tasks.
     Like dis: next 3
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I can show you one task or more, but not 0.
     Like dis: next 3
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I can show you one task or more, but not -2.
     Like dis: next 3
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I can show you one task or more, but not -99999999999.
     Like dis: next 3
    ____________________________________________________________

    ____________________________________________________________
     No dates on tasks. No bee-do!
    ____________________________________________________________

    ____________________________________________________________
     No tasks yet! Bob bored...
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
(no file)
```

### TC28 - Tasks falling after a specific day

**Aim:** Check that `after` lists what comes later than a day, and that the day
named is itself excluded — `after 2026-12-02` must not show the deadline due on
the 2nd, so `before`, `on` and `after` for one day never show the same deadline
twice. Checked from both sides: the same task does appear once the day asked about
is moved back before it. Also checks the one case where `after` and `on` disagree,
which is the point of the design decision behind it: a task is placed by the one
date it is pinned to, and an event is pinned to its start, so an event already
running on the day asked about is not listed as still to come. The `on 2026-10-06`
straight afterwards shows that same event, so the two answers can be read against
each other. As with the other two listings, a todo has no date and so is never
listed, and a day with nothing after it says so.

**Input**

```text
deadline return book /by 2026-12-02
event orientation week /from 2026-10-04 /to 2026-10-11
todo read book
after 2026-12-02
after 2026-12-01
after 2026-10-06
on 2026-10-06
after 2026-10-03
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] return book (by: Dec 02 2026)
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [E][ ] orientation week (from: Oct 04 2026 to: Oct 11 2026)
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 3 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Nothing after Dec 02 2026. Yay!
    ____________________________________________________________

    ____________________________________________________________
     Bob look after Dec 01 2026. Yu have:
     1.[D][ ] return book (by: Dec 02 2026)
    ____________________________________________________________

    ____________________________________________________________
     Bob look after Oct 06 2026. Yu have:
     1.[D][ ] return book (by: Dec 02 2026)
    ____________________________________________________________

    ____________________________________________________________
     Luk! On Oct 06 2026 yu have:
     2.[E][ ] orientation week (from: Oct 04 2026 to: Oct 11 2026)
    ____________________________________________________________

    ____________________________________________________________
     Bob look after Oct 03 2026. Yu have:
     1.[D][ ] return book (by: Dec 02 2026)
     2.[E][ ] orientation week (from: Oct 04 2026 to: Oct 11 2026)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC29 - An event cannot end before it starts, or at the same moment

**Aim:** Check that a pair of dates which are each perfectly readable, but which
put the end of an event before its start, is refused rather than stored. Both
dates are shown back in the message, so a user who typed them the wrong way round
can see which the chatbot read as which. Checked on two scales: a start and an end
days apart, and a start and an end on the same day whose times are the wrong way
round, which is the case a comparison of days alone would let through. An event
starting and ending at the same moment is refused too, and the user is pointed to a
deadline, which is what records a single moment. An event starting and ending on
the same day with no times is a one-day event, and is accepted. Those two are the
boundary the check is written against. The `list` at the end shows that only the
well-formed events were stored.

**Input**

```text
event conference /from 2026-12-05 /to 2026-12-02
event workshop /from 2026-12-05 1800 /to 2026-12-05 0900
event standup /from 2026-12-05 0900 /to 2026-12-05 0900
event open day /from 2026-12-05 /to 2026-12-05
event trip /from 2026-12-02 /to 2026-12-05
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     An event can't end before it starts.
     It would run from Dec 05 2026 to Dec 02 2026.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     An event can't end before it starts.
     It would run from Dec 05 2026 18:00 to Dec 05 2026 09:00.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     An event can't start and end at the same moment. For a single moment, use a deadline.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [E][ ] open day (from: Dec 05 2026 to: Dec 05 2026)
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [E][ ] trip (from: Dec 02 2026 to: Dec 05 2026)
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[E][ ] open day (from: Dec 05 2026 to: Dec 05 2026)
     2.[E][ ] trip (from: Dec 02 2026 to: Dec 05 2026)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC30 - Input that ends without bye

**Aim:** Check that the chatbot still signs off when the input simply runs out
instead of ending with `bye`, which is what happens when commands are piped in
from a file. The farewell is printed by the `bye` command itself, so this test
case covers the other way the conversation can end — the one where no command
prints it.

**Input**

```text
todo read book
list
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC31 - Finding tasks by a keyword

**Aim:** Check that `find` picks out the tasks whose description mentions a word
and leaves the rest alone. All three kinds of task are searched, since every task
has a description — unlike `on`, `before` and `after`, this listing does not pass
over a todo. The search ignores case, so `find BOOK` finds the same tasks as
`find book`, and it matches part of a word, so `find clu` finds both `book club`
and `sports club` — that is what makes typing half a word worth doing. As with
the other shortened listings, the numbers shown are the tasks' numbers in the
full list rather than 1, 2, 3 counting the matches, which is why the marked
tasks below keep the numbers `list` would give them. A keyword nothing mentions
says so and quotes the keyword back, rather than printing an empty listing, and
a `find` with nothing after it asks for a word instead of matching every task.

**Input**

```text
todo read book
deadline return book /by 2026-06-06
event book club /from 2026-07-01 1900 /to 2026-07-01 2000
todo join sports club
mark 1
mark 2
find book
find BOOK
find clu
find swimming
find
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] return book (by: Jun 06 2026)
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [E][ ] book club (from: Jul 01 2026 19:00 to: Jul 01 2026 20:00)
     Now yu have 3 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] join sports club
     Now yu have 4 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Kanpai! Dis one finish:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Kanpai! Dis one finish:
       [D][X] return book (by: Jun 06 2026)
    ____________________________________________________________

    ____________________________________________________________
     Bob found dem:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2026)
     3.[E][ ] book club (from: Jul 01 2026 19:00 to: Jul 01 2026 20:00)
    ____________________________________________________________

    ____________________________________________________________
     Bob found dem:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2026)
     3.[E][ ] book club (from: Jul 01 2026 19:00 to: Jul 01 2026 20:00)
    ____________________________________________________________

    ____________________________________________________________
     Bob found dem:
     3.[E][ ] book club (from: Jul 01 2026 19:00 to: Jul 01 2026 20:00)
     4.[T][ ] join sports club
    ____________________________________________________________

    ____________________________________________________________
     Bob no find "swimming". Bi-do!
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     What should I look for?
     Give me a word from the task, for example: find book
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

### TC32 - Editing each kind of task in place

**Aim:** Check that `edit` changes only the details typed and leaves everything
else about a task alone. Each kind of task is edited through the markers it has:
a todo's description, a deadline's due date, and an event's end on its own and
then both of its ends at once. A deadline given a new day without a time loses the
time it had, because a new date replaces the old one whole. The markers may come
in any order, so the second event edit types `/to` before `/from`, and the last
edit changes a description and a date together. Each confirmation shows the task
before and after, the first task's done box stays ticked, and the `list` at the
end shows every task still under the number it started with. The save file after
the run shows each edit written in the usual format, on the line of the task it
changed.

**Data file before**

```text
T | 1 | read book
D | 0 | return book | 2026-12-02 1800
E | 0 | project meeting | 2026-08-06 1400 | 2026-08-06 1600
```

**Input**

```text
edit 1 /desc read library book
edit 2 /by 2026-12-05
edit 3 /to 2026-08-06 1800
edit 3 /to 2026-08-07 1200 /from 2026-08-07 1000
edit 2 /desc return library book /by 2026-12-06 0900
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Bob found 3 tasks from before.
    ____________________________________________________________

    ____________________________________________________________
     Tadaa! Dis was:
       [T][X] read book
     Now is:
       [T][X] read library book
    ____________________________________________________________

    ____________________________________________________________
     Tadaa! Dis was:
       [D][ ] return book (by: Dec 02 2026 18:00)
     Now is:
       [D][ ] return book (by: Dec 05 2026)
    ____________________________________________________________

    ____________________________________________________________
     Tadaa! Dis was:
       [E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)
     Now is:
       [E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 18:00)
    ____________________________________________________________

    ____________________________________________________________
     Tadaa! Dis was:
       [E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 18:00)
     Now is:
       [E][ ] project meeting (from: Aug 07 2026 10:00 to: Aug 07 2026 12:00)
    ____________________________________________________________

    ____________________________________________________________
     Tadaa! Dis was:
       [D][ ] return book (by: Dec 05 2026)
     Now is:
       [D][ ] return library book (by: Dec 06 2026 09:00)
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][X] read library book
     2.[D][ ] return library book (by: Dec 06 2026 09:00)
     3.[E][ ] project meeting (from: Aug 07 2026 10:00 to: Aug 07 2026 12:00)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
T | 1 | read library book
D | 0 | return library book | 2026-12-06 0900
E | 0 | project meeting | 2026-08-07 1000 | 2026-08-07 1200
```

### TC33 - Edits that cannot be carried out leave every task as it was

**Aim:** Check that each way an `edit` can go wrong is answered with the
explanation that fits it, and that none of them changes a task. A missing task
number, one that is not a number, and one that names no task are refused as they
are for `mark` and `delete`. An `edit` with no marker after the number is refused
with an example, and so is one that starts with plain text, as a description would
be typed after `todo`. A marker with nothing after it and a new date that is not a
date are refused before any task is looked at. A date the task does not have is
refused by the kind of task: a todo has no dates, a deadline has no start or end,
and an event has no due date. Last, a new start that would put an event's start
after its end is refused, and the message shows the dates the change would have
left, including the end that was not typed. The `list` and the save file at the
end show the three tasks exactly as they were added.

**Input**

```text
todo read book
deadline return book /by 2026-12-02
event project meeting /from 2026-08-06 1400 /to 2026-08-06 1600
edit
edit two /desc read book
edit 5 /desc read book
edit 2
edit 2 return book /by 2026-12-05
edit 1 /desc
edit 2 /by
edit 2 /by someday
edit 1 /by 2026-12-02
edit 2 /from 2026-12-02 1800
edit 3 /by 2026-12-02
edit 3 /from 2026-08-06 1700
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] return book (by: Dec 02 2026)
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)
     Now yu have 3 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     Which task should I edit?
     Give me its number from list, for example: edit 2
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     "two" isn't a task number.
     I need the number shown next to the task in list, for example: edit 2
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I don't have a task numbered 5.
     Your list runs from 1 to 3; type list to see it.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     What should I change about task 2?
     Use /desc, /by, /from or /to.
     Like dis: edit 2 /desc read book
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     What should I change about task 2?
     Use /desc, /by, /from or /to.
     Like dis: edit 2 /desc read book
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     You wrote /desc but nothing after it.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     You wrote /by but nothing after it.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I don't understand "someday" as a date.
     Write the day as yyyy-mm-dd, and add a 24-hour time if the hour matters.
     Like dis: 2026-12-02 or 2026-12-02 1800
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     A todo has no dates to change, only its description.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     A deadline has a due date, not a start or an end.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     An event has a start and an end, not a due date.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     An event can't end before it starts.
     It would run from Aug 06 2026 17:00 to Aug 06 2026 16:00.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][ ] read book
     2.[D][ ] return book (by: Dec 02 2026)
     3.[E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
T | 0 | read book
D | 0 | return book | 2026-12-02
E | 0 | project meeting | 2026-08-06 1400 | 2026-08-06 1600
```

### TC34 - Command words in the wrong case or after unusual spaces

**Aim:** Check how a line is read when its command word is not typed as plain
lowercase followed by a space. `Todo read book` and `MARK 1` are still refused,
since commands are lowercase, but each answer ends by suggesting the lowercase
word. The third input line has a tab rather than a space after `todo`, and is
added like any other todo. The fourth has no-break spaces (U+00A0, as text pasted
from a web page often does) before `deadline`, after it, and between `return` and
`book`; they are read as ordinary spaces, so the deadline is added and saved with
an ordinary space in its description. Both characters are invisible in the input
block, so edit those two lines with care. The `list` and the save file show that
only those two tasks were added.

**Input**

```text
Todo read book
todo	read book
 deadline return book /by 2026-12-02
MARK 1
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     Sorry, I don't know what "Todo read book" means.
     Try one of: todo, deadline, event, list, on, before, after, next, find, mark, unmark, delete, edit, bye
     Commands are lowercase. Did yu mean todo?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] return book (by: Dec 02 2026)
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     Sorry, I don't know what "MARK 1" means.
     Try one of: todo, deadline, event, list, on, before, after, next, find, mark, unmark, delete, edit, bye
     Commands are lowercase. Did yu mean mark?
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][ ] read book
     2.[D][ ] return book (by: Dec 02 2026)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
T | 0 | read book
D | 0 | return book | 2026-12-02
```

### TC35 - Task numbers that are too big or more than one

**Aim:** Check the task numbers that are numbers but cannot be carried out as
one. `mark 99999999999` is too big for any list and is refused as too big,
rather than as not being a number, which it is; `edit` reads its task number
the same way and is refused alike. `delete 1 2` names two tasks, and is refused
with an example that deletes the first of them. A `next` asking for more tasks
than a number can hold is not refused: like any count larger than the list, it
shows every dated task. The `list` and the save file at the end show that
nothing was marked, edited or deleted.

**Input**

```text
todo read book
deadline return book /by 2026-12-02
event project meeting /from 2026-08-06 1400 /to 2026-08-06 1600
mark 99999999999
delete 1 2
edit 99999999999 /desc read novel
next 99999999999
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] return book (by: Dec 02 2026)
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)
     Now yu have 3 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     99999999999 is too big to be a task number.
     I need the number shown next to the task in list, for example: mark 2
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I can only delete one task at a time.
     Like dis: delete 1
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     99999999999 is too big to be a task number.
     I need the number shown next to the task in list, for example: edit 2
    ____________________________________________________________

    ____________________________________________________________
     Bee-do bee-do! 2 most urgent, soonest first:
     3.[E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)
     2.[D][ ] return book (by: Dec 02 2026)
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][ ] read book
     2.[D][ ] return book (by: Dec 02 2026)
     3.[E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
T | 0 | read book
D | 0 | return book | 2026-12-02
E | 0 | project meeting | 2026-08-06 1400 | 2026-08-06 1600
```

### TC36 - Markers inside words, repeated, misplaced or in capitals

**Aim:** Check that a marker counts only as a whole word, and that each way a
marker can be misused is refused with the explanation that fits it. `/byzantine`
is part of the first deadline's description rather than its `/by`, and `/tokyo`
is kept whole in a new description rather than read as `/to`. A marker written
twice is refused, in an add command and in an edit alike. A deadline given
`/from`, and an event given `/by`, are told which markers they take. `/desc` in an
add command is refused as belonging to `edit`. A todo with a date marker is
pointed to `deadline` for `/by` and to `event` for `/from` and `/to`. An event
with `/to` before `/from` is asked to swap them, and a marker in capitals is
answered with the lowercase marker. The `list` and the save file show that only
the first deadline was added, and only the one edit made.

**Input**

```text
deadline study /byzantine /by 2026-12-02
deadline return book /by 2026-12-02 /by 2026-12-03
deadline return book /by 2026-12-02 /from 2026-12-01
event project meeting /from 2026-08-06 1400 /to 2026-08-06 1600 /by 2026-08-05
deadline /desc return book /by 2026-12-02
todo read book /by 2026-12-02
todo party /from 2026-12-05 /to 2026-12-06
event project meeting /to 2026-08-06 1600 /from 2026-08-06 1400
deadline return book /BY 2026-12-02
edit 1 /desc fly /tokyo
edit 1 /desc a /desc b
edit 1 /DESC read book
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] study /byzantine (by: Dec 02 2026)
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     You wrote /by twice. Give just one.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     A deadline only takes /by, not /from.
     Like dis: deadline return book /by 2026-12-02
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     An event only takes /from and /to, not /by.
     Like dis: event project meeting /from 2026-12-02 1800 /to 2026-12-02 2000
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     /desc is only used with edit. Write the description straight after deadline.
     Like dis: deadline return book /by 2026-12-02
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     A todo has no dates. Did yu mean deadline?
     Like dis: deadline return book /by 2026-12-02
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     A todo has no dates. Did yu mean event?
     Like dis: event project meeting /from 2026-12-02 1800 /to 2026-12-02 2000
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     Put /from before /to.
     Like dis: event project meeting /from 2026-12-02 1800 /to 2026-12-02 2000
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     Markers are lowercase. Did yu mean /by?
    ____________________________________________________________

    ____________________________________________________________
     Tadaa! Dis was:
       [D][ ] study /byzantine (by: Dec 02 2026)
     Now is:
       [D][ ] fly /tokyo (by: Dec 02 2026)
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     You wrote /desc twice. Give just one.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     Markers are lowercase. Did yu mean /desc?
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[D][ ] fly /tokyo (by: Dec 02 2026)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
D | 0 | fly /tokyo | 2026-12-02
```

### TC37 - Days and times that are written correctly but do not exist

**Aim:** Check that a day or a time in the accepted form, but naming one that
does not exist, is refused with what is wrong with it. `2400` is refused rather
than quietly read as midnight at the start of the same day, and `1260` is
refused for its minutes although it is below `2359`. A month past 12, and the
30th of February in a leap year, are refused with the month or its length. A part
written as `00` is refused as the thing that does not exist, whether it is the
month or the day: `2026-12-00` says there is no day 0, rather than that December
has 31 days, which is true and would send the user looking for a month long
enough to hold a day 0. The check is the same wherever a date is read: in an
event's end, and in the day asked about by `on` and `before`. The last deadline
is on the leap day at the last minute of the day, which does exist, and the
`list` and the save file show it is the only task added.

**Input**

```text
deadline pay fees /by 2026-12-02 2400
deadline pay fees /by 2026-12-02 1260
deadline pay fees /by 2026-13-01
deadline pay fees /by 2026-12-00
deadline pay fees /by 2028-02-30
event exam week /from 2026-04-27 /to 2026-04-31
on 2026-02-29
before 2026-00-10
deadline pay fees /by 2028-02-29 2359
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     2400 isn't a real time: hours go up to 23 and minutes up to 59.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     1260 isn't a real time: hours go up to 23 and minutes up to 59.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     2026-13-01 isn't a real day: there is no month 13.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     2026-12-00 isn't a real day: there is no day 0.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     2028-02-30 isn't a real day: February 2028 has 29 days.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     2026-04-31 isn't a real day: April 2026 has 30 days.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     2026-02-29 isn't a real day: February 2026 has 28 days.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     2026-00-10 isn't a real day: there is no month 0.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] pay fees (by: Feb 29 2028 23:59)
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[D][ ] pay fees (by: Feb 29 2028 23:59)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
D | 0 | pay fees | 2028-02-29 2359
```

### TC38 - Duplicate tasks

**Aim:** Check that a task the list already holds is refused rather than added a
second time, and that an edit which would make one task the same as another is
refused too. Tasks are matched loosely: capitals and the spaces between words are
ignored, and so is whether a task is done, so `todo READ   book` matches the saved
`read book`, and a new deadline matches one already marked done. The task already
there is named by its number and shown as it is. The same words with a different
date, or on a different kind of task, make a different task and are added: a
deadline due at `0000` is not one due on the day with no time, and an event ending
an hour later is not the same event. An edit is not compared with the task it
changes, so recapitalizing task 4 is carried out. The save file starts with the
same todo on lines 1 and 3, which only a hand-edited file can hold: both are loaded,
and the load report names them as tasks 1 and 3 in the list, then as lines 1 and 3
of the file. The `list` and the save file at the end
show that nothing refused was added or saved.

**Data file before**

```text
T | 0 | read book
D | 1 | return book | 2026-12-02
T | 1 | Read  Book
E | 0 | project meeting | 2026-08-06 1400 | 2026-08-06 1600
```

**Input**

```text
todo READ   book
deadline Return Book /by 2026-12-02
deadline return book /by 2026-12-02 0000
event project meeting /from 2026-08-06 1400 /to 2026-08-06 1700
todo return book
edit 6 /to 2026-08-06 1600
edit 7 /desc READ BOOK
edit 4 /desc Project Meeting
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Bob found 4 tasks from before.
     Tasks 1 and 3 in your list (lines 1 and 3 of data/bob.txt) are the same task. I've kept both — delete the one you don't need.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     Yu already have dis as task 1: [T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     Yu already have dis as task 2: [D][X] return book (by: Dec 02 2026)
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] return book (by: Dec 02 2026 00:00)
     Now yu have 5 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 17:00)
     Now yu have 6 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] return book
     Now yu have 7 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     Dat change would make it da same as task 4:
       [E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     Dat change would make it da same as task 1:
       [T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Tadaa! Dis was:
       [E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)
     Now is:
       [E][ ] Project Meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][ ] read book
     2.[D][X] return book (by: Dec 02 2026)
     3.[T][X] Read  Book
     4.[E][ ] Project Meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)
     5.[D][ ] return book (by: Dec 02 2026 00:00)
     6.[E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 17:00)
     7.[T][ ] return book
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
T | 0 | read book
D | 1 | return book | 2026-12-02
T | 1 | Read  Book
E | 0 | Project Meeting | 2026-08-06 1400 | 2026-08-06 1600
D | 0 | return book | 2026-12-02 0000
E | 0 | project meeting | 2026-08-06 1400 | 2026-08-06 1700
T | 0 | return book
```

### TC39 - Marking a task that already has that status

**Aim:** Check that `mark` on a task already done, and `unmark` on a task not done,
are answered as ordinary replies rather than as complaints: there is no
`MINION EMERGENCY!` line, since the task ends up as the user asked. The task is
still shown, with a note that there was nothing to change, so a user who typed the
wrong number can see which task it was. Nothing is saved. The save file is written
without the spaces around each bar that a save puts there, so the Data file after
being byte for byte the Data file before shows that the file was never rewritten.

**Data file before**

```text
T|1|read book
D|0|return book|2026-12-02
```

**Input**

```text
mark 1
unmark 2
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Bob found 2 tasks from before.
    ____________________________________________________________

    ____________________________________________________________
     Dis one already finish! Nothing to change:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Dis one not finish yet! Nothing to change:
       [D][ ] return book (by: Dec 02 2026)
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][X] read book
     2.[D][ ] return book (by: Dec 02 2026)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
T|1|read book
D|0|return book|2026-12-02
```

### TC40 - A damaged save file is copied before it can be overwritten

**Aim:** Check that a save file with lines the chatbot cannot read is copied, as it
was, to `data/bob.txt.bak` when the chatbot starts, and that the load report says
where the copy is rather than warning that the skipped lines will be lost. The
damaged lines are the two events that break the rule an event typed at the chatbot
meets — one ending before it starts, and one starting and ending at the same
moment — so this also checks that a hand-edited file cannot load either of them.
The `todo` afterwards changes the list, and the Data file after shows that
`data/bob.txt` was then rewritten without the skipped lines, leaving the copy as
the only place they survive. What the copy holds is checked in `StorageTest`,
since the runner compares only `data/bob.txt`.

**Data file before**

```text
T | 0 | read book
E | 0 | conference | 2026-12-05 | 2026-12-02
E | 0 | standup | 2026-12-05 0900 | 2026-12-05 0900
```

**Input**

```text
todo pay bills
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Bob found 1 task from before.
     Line 2 of data/bob.txt isn't a task I can read: the event ends before it starts.
     Line 3 of data/bob.txt isn't a task I can read: the event starts and ends at the same moment.
     I've left those 2 lines out of your list.
     They'll be dropped from data/bob.txt the next time the list changes, but the copy still has them.
     The file as it was is kept in data/bob.txt.bak, so nothing in it is lost.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] pay bills
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][ ] read book
     2.[T][ ] pay bills
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
T | 0 | read book
T | 0 | pay bills
```

### TC41 - A save file that starts with a byte order mark

**Aim:** Check that a save file some editors would write, Notepad among them, is
read like any other. Such an editor puts an invisible character, the byte order
mark, at the very start of a file it saves as UTF-8. It sits at the start of the
first line of the Data file before below, where it cannot be seen, and before this
increment it made that line unreadable, so the first task was reported and left
out. Now both tasks load with nothing to report. The `mark 1` rewrites the file,
and the Data file after shows the mark is not written back. An editor that strips
the invisible character from this plan would turn the test into an ordinary load,
so check for it if this test case ever passes when it should not.

**Data file before**

```text
﻿T | 0 | read book
D | 0 | return book | 2026-12-02
```

**Input**

```text
mark 1
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Bob found 2 tasks from before.
    ____________________________________________________________

    ____________________________________________________________
     Kanpai! Dis one finish:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][X] read book
     2.[D][ ] return book (by: Dec 02 2026)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
T | 1 | read book
D | 0 | return book | 2026-12-02
```

### TC42 - Backslashes typed into the save file by hand

**Aim:** Check that a backslash written into the save file by hand, where it does
not start one of the two escape sequences a save writes (`\\` and `\|`), is read
as an ordinary backslash. Before this fix every backslash was taken as an escape,
so the folder path below loaded as `copy C:tempnotes` with nothing reported, and
the next save wrote the loss into the file. The second task mixes such a backslash
with an escaped bar. `list` shows both paths as written, and the `mark 1` rewrites
the file, whose Data file after shows each lone backslash now saved escaped.

**Data file before**

```text
T | 0 | copy C:\temp\notes
D | 0 | back up D:\work \| old | 2026-12-02
```

**Input**

```text
list
mark 1
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Bob found 2 tasks from before.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][ ] copy C:\temp\notes
     2.[D][ ] back up D:\work | old (by: Dec 02 2026)
    ____________________________________________________________

    ____________________________________________________________
     Kanpai! Dis one finish:
       [T][X] copy C:\temp\notes
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
T | 1 | copy C:\\temp\\notes
D | 0 | back up D:\\work \| old | 2026-12-02
```

### TC43 - The same task on two lines, named by the numbers delete takes

**Aim:** Check that the load report for two lines holding the same task names them
by their numbers in the list, which are the numbers `delete` takes, and only then by
their lines in the file. A blank line in the file below makes the two counts differ:
`read book` and `Read Book` are on lines 1 and 4 but are tasks 1 and 3. Before this
fix the report named only lines 1 and 4 and told the user to delete one, and
`delete 4` would have removed `pay bills` instead. The `delete 3` removes the second
copy, and the `list` and the Data file after show that the other three tasks are
untouched.

**Data file before**

```text
T | 0 | read book

T | 0 | return book
T | 1 | Read Book
T | 0 | pay bills
```

**Input**

```text
delete 3
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Bob found 4 tasks from before.
     Tasks 1 and 3 in your list (lines 1 and 4 of data/bob.txt) are the same task. I've kept both — delete the one you don't need.
    ____________________________________________________________

    ____________________________________________________________
     Bee-do! Bob throw away dis:
       [T][X] Read Book
     Now yu have 3 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][ ] read book
     2.[T][ ] return book
     3.[T][ ] pay bills
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
T | 0 | read book
T | 0 | return book
T | 0 | pay bills
```

### TC44 - Finished tasks are never the most urgent

**Aim:** Check that `next` leaves out tasks that are already done, however soon
their dates. Before this fix the first `next 2` listed the finished deadline from
2020 as the most urgent task, ahead of the one open deadline, and the second one
listed it again in place of the unmarked event. Now the first `next 2` shows only
the open deadline, and says `Most urgent` in the
singular because one is all there is. Unmarking the event brings it back into the
listing, in date order after the deadline. Once every dated task is marked done
again, `next` says so, rather than claiming that no task has a date: the todo is
never listed either way, since it has no date. The Data file after shows the marks
were saved as usual.

**Input**

```text
deadline tax 2020 /by 2020-04-30
event camp /from 2026-06-01 /to 2026-06-03
deadline tax 2026 /by 2026-04-30
todo read book
mark 1
mark 2
next 2
unmark 2
next 2
mark 2
mark 3
next 2
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] tax 2020 (by: Apr 30 2020)
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [E][ ] camp (from: Jun 01 2026 to: Jun 03 2026)
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] tax 2026 (by: Apr 30 2026)
     Now yu have 3 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] read book
     Now yu have 4 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Kanpai! Dis one finish:
       [D][X] tax 2020 (by: Apr 30 2020)
    ____________________________________________________________

    ____________________________________________________________
     Kanpai! Dis one finish:
       [E][X] camp (from: Jun 01 2026 to: Jun 03 2026)
    ____________________________________________________________

    ____________________________________________________________
     Bee-do bee-do! Most urgent:
     3.[D][ ] tax 2026 (by: Apr 30 2026)
    ____________________________________________________________

    ____________________________________________________________
     Bi-do! Dis one not finish yet:
       [E][ ] camp (from: Jun 01 2026 to: Jun 03 2026)
    ____________________________________________________________

    ____________________________________________________________
     Bee-do bee-do! 2 most urgent, soonest first:
     3.[D][ ] tax 2026 (by: Apr 30 2026)
     2.[E][ ] camp (from: Jun 01 2026 to: Jun 03 2026)
    ____________________________________________________________

    ____________________________________________________________
     Kanpai! Dis one finish:
       [E][X] camp (from: Jun 01 2026 to: Jun 03 2026)
    ____________________________________________________________

    ____________________________________________________________
     Kanpai! Dis one finish:
       [D][X] tax 2026 (by: Apr 30 2026)
    ____________________________________________________________

    ____________________________________________________________
     All dated tasks finish! No bee-do!
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
D | 1 | tax 2020 | 2020-04-30
E | 1 | camp | 2026-06-01 | 2026-06-03
D | 1 | tax 2026 | 2026-04-30
T | 0 | read book
```

### TC45 - find ignores extra spaces, as the duplicate check does

**Aim:** Check that `find` matches a task however many spaces were typed between
its words, which is what the duplicate check already does. The task below is added
with two spaces in `sports  club`. Before this fix `find sports club` found nothing,
while adding `todo join sports club` was refused for matching that very task, so the
user was told they already had a task they could not find. Now both `find` lines
find it: the second also checks that capitals and spaces are ignored together, and
that spaces around the keyword itself are ignored. The refused `todo` shows the
duplicate check is unchanged, and the `list` and the Data file after show the task
is still stored with the two spaces the user typed.

**Input**

```text
todo join sports  club
find sports club
find SPORTS   CLUB
todo join sports club
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [T][ ] join sports  club
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Bob found dem:
     1.[T][ ] join sports  club
    ____________________________________________________________

    ____________________________________________________________
     Bob found dem:
     1.[T][ ] join sports  club
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     Yu already have dis as task 1: [T][ ] join sports  club
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[T][ ] join sports  club
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
T | 0 | join sports  club
```

### TC46 - An event's end day with no time means the end of that day

**Aim:** Check what a `/to` day written without a time means. It is the end of that
day, so `party` running from six in the evening of the 2nd to the 2nd is accepted;
before this fix it was refused as ending before it started, because the end counted
as midnight at the start of the day. A `/from` day with no time still means the start
of its day, which is why `blip`, asked to end at `0000` of the day it starts, is
refused as starting and ending at the same moment. The third event checks that an end
day still lands before a start on a later day. The fourth shows the two rules
together, the whole of the 2nd up to nine on the 3rd. The `list` and the Data file
after show each accepted event stored with the dates as they were typed.

**Input**

```text
event party /from 2026-12-02 1800 /to 2026-12-02
event blip /from 2026-12-02 /to 2026-12-02 0000
event backwards /from 2026-12-06 0900 /to 2026-12-05
event shop /from 2026-12-02 /to 2026-12-03 0900
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [E][ ] party (from: Dec 02 2026 18:00 to: Dec 02 2026)
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     An event can't start and end at the same moment. For a single moment, use a deadline.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     An event can't end before it starts.
     It would run from Dec 06 2026 09:00 to Dec 05 2026.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [E][ ] shop (from: Dec 02 2026 to: Dec 03 2026 09:00)
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[E][ ] party (from: Dec 02 2026 18:00 to: Dec 02 2026)
     2.[E][ ] shop (from: Dec 02 2026 to: Dec 03 2026 09:00)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
E | 0 | party | 2026-12-02 1800 | 2026-12-02
E | 0 | shop | 2026-12-02 | 2026-12-03 0900
```

### TC47 - Only four-digit years are accepted

**Aim:** Check the years Bob takes, now that a year is four digits from `0001` to
`9999`. Before this fix the underlying date reader took the signed forms Java allows,
which Bob never mentions and cannot show back sensibly: `+10000-01-01` was a date
while `10000-01-01` was not, and `0000-06-01` and `-0001-06-01` were stored and then
shown a year later than they were typed, because the date format counted the year
within an era and never said which era it meant. Year 0 is now refused by name, since
it is the one out-of-range year that can be written in the accepted form, and the
three signed or overlong years are refused as dates Bob doesn't understand. The `on`
line checks that a day asked about is refused in the same way as a day given to a
deadline. The first and last years Bob does take are added, so the two ends of the
range are pinned down as well, and the Data file after shows only those two.

**Input**

```text
deadline ancient scroll /by 0000-06-01
deadline older scroll /by -0001-06-01
deadline far future /by +10000-01-01
deadline far future /by 10000-01-01
deadline first day /by 0001-01-01
deadline last day /by 9999-12-31
on 0000-06-01
list
bye
```

**Expected output**

```text
    ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     0000-06-01 isn't a real day: there is no year 0.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I don't understand "-0001-06-01" as a date.
     Write the day as yyyy-mm-dd, and add a 24-hour time if the hour matters.
     Like dis: 2026-12-02 or 2026-12-02 1800
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I don't understand "+10000-01-01" as a date.
     Write the day as yyyy-mm-dd, and add a 24-hour time if the hour matters.
     Like dis: 2026-12-02 or 2026-12-02 1800
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     I don't understand "10000-01-01" as a date.
     Write the day as yyyy-mm-dd, and add a 24-hour time if the hour matters.
     Like dis: 2026-12-02 or 2026-12-02 1800
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] first day (by: Jan 01 0001)
     Now yu have 1 task in da list.
    ____________________________________________________________

    ____________________________________________________________
     Okay! Bob add dis:
       [D][ ] last day (by: Dec 31 9999)
     Now yu have 2 tasks in da list.
    ____________________________________________________________

    ____________________________________________________________
     MINION EMERGENCY!
     0000-06-01 isn't a real day: there is no year 0.
    ____________________________________________________________

    ____________________________________________________________
     Luk at tu! Here da tasks:
     1.[D][ ] first day (by: Jan 01 0001)
     2.[D][ ] last day (by: Dec 31 9999)
    ____________________________________________________________

    ____________________________________________________________
     Poopaye! Bob go find banana, see yu soon!
    ____________________________________________________________
```

**Data file after**

```text
D | 0 | first day | 0001-01-01
D | 0 | last day | 9999-12-31
```
