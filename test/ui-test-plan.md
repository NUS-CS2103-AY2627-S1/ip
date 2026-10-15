# LuckyNoSlacky UI test plan

## Test Case: Forgiving whitespace and trailing commentary

- Aim: Verify that harmless whitespace, punctuation, and plain trailing
  commentary are accepted, while marker-like slash text is rejected.

### Input

```text
  deadline   report /by 26 Aug 2026 12pm please
snooze 1 /by  1   hour see a/b results
snooze 1 /by 1 hour /later
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah:
    [⏳][❗] report (by: Wed Aug 26 2026, 12.00pm)
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Nah here's your snooze you lazy bum, don't slack too much hor!
    [⏳][❗] report (by: Wed Aug 26 2026, 1.00pm)
  ____________________________________________________________
  ____________________________________________________________
  Eh HELLO you know how to type command one anot?
  Lai lai let me teach you: `snooze <taskNumber> [/by <duration>] or <taskNumber> [/to <end date/time>]`
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test execution information

- Program: Java 25 with the compiled application classes and CSV runtime dependencies
- Main class: `luckynoslacky.LuckyNoSlacky`
- Working directory: project root
- Automated command: `./gradlew clitest` (also included by `./gradlew check`)
- Each case is represented by a Java fixture in
  `src/test/java/luckynoslacky/luckyui/CliTestCaseFixtures.java` and links to
  its corresponding test-plan section.
- Data isolation: reset `data/luckyNoSlacky.csv` before each test case
- Automated cases run in isolated temporary working directories, so the
  repository's data file is not modified.
- Deterministic clock: run with
  `-Dluckynoslacky.fixedNow=2026-08-25T10:00:00Z` so relative dates are stable
- Recommended execution: run the bundled UI runner against the compiled CLI
  main class with `--reset-file data/luckyNoSlacky.csv`. This resets persisted
  state before every case without opening the released JavaFX interface.
- CLI tests do not impose an artificial timeout; each case must terminate with
  `bye` or end-of-file.
- Date output comparison normalizes line endings and localized day, month, and
  meridiem names while preserving all other formatting.
- Storage failure cases are covered by unit tests using prepared data files
- Compact `HHMM` fallback and date-dependent resolution are covered by
  `DateTimeParserTest` with a fixed clock
- Comparison: exact output, ignoring line endings, incidental trailing
  whitespace, and one final newline
- Failure policy: stop immediately after the first failed test case
- Startup load failures are covered deterministically by
  `LuckyNoSlackyTest`; the CLI continues in degraded empty-list mode after
  displaying the loading warning, and normal saves remain available.
- Task-capacity overflow is covered deterministically by
  `LuckyNoSlackyTest`; the CLI displays the task-limit message and continues
  accepting commands after the list reaches its maximum size.
- Invalid non-blank `luckynoslacky.fixedNow` values are covered by
  `LuckyNoSlackyTest`; the CLI displays the configuration warning and returns
  cleanly without entering the command loop.
- End-of-file is a supported clean termination path; the CLI processes all
  available input and displays the goodbye message exactly once.

## Test Case: Start and exit

- Aim: Verify that the chatbot displays its banner and greeting, then exits when the user enters `bye`.

### Input

```text
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: End-of-file exit

- Aim: Verify that the CLI processes an invalid command before input ends and
  displays one goodbye message when EOF is reached without `bye`.

### Input

```text
unknown command
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  What talking you? I only understand todo, deadline, event, list, mark, unmark, delete, find, snooze, resched, or bye, ok?
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Natural-language snooze durations

- Aim: Verify article, number-word, filler-word, and abbreviated half-unit
  duration forms.

### Input

```text
deadline report /by 26 Aug 2026 12pm
snooze 1 /by half an hr
snooze 1 /by 1 more week
list
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [⏳][❗] report (by: Wed Aug 26 2026, 12.00pm)
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Nah here's your snooze you lazy bum, don't slack too much hor!
    [⏳][❗] report (by: Wed Aug 26 2026, 12.30pm)
  ____________________________________________________________
  ____________________________________________________________
  Nah here's your snooze you lazy bum, don't slack too much hor!
    [⏳][❗] report (by: Wed Sep 02 2026, 12.30pm)
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[⏳][❗] report (by: Wed Sep 02 2026, 12.30pm)
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Decimal and combined snooze durations

- Aim: Verify decimal fixed-length durations, abbreviations, and combined
  calendar and clock durations update the correct task ending times.

### Input

```text
deadline report /by 26 Aug 2026 12pm
event meeting /from 26 Aug 2026 2pm /to 3pm
snooze 1 /by 1.5h
snooze 2 /by 1hr 30mins
snooze 2 /by 1mo 2ds 3hrs
list
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [⏳][❗] report (by: Wed Aug 26 2026, 12.00pm)
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📆][❗] meeting (from: Wed Aug 26 2026, 2.00pm to: Wed Aug 26 2026, 3.00pm)
  Now you got 2 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Nah here's your snooze you lazy bum, don't slack too much hor!
    [⏳][❗] report (by: Wed Aug 26 2026, 1.30pm)
  ____________________________________________________________
  ____________________________________________________________
  Nah here's your snooze you lazy bum, don't slack too much hor!
    [📆][❗] meeting (from: Wed Aug 26 2026, 2.00pm to: Wed Aug 26 2026, 4.30pm)
  ____________________________________________________________
  ____________________________________________________________
  Nah here's your snooze you lazy bum, don't slack too much hor!
    [📆][❗] meeting (from: Wed Aug 26 2026, 2.00pm to: Mon Sep 28 2026, 7.30pm)
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[⏳][❗] report (by: Wed Aug 26 2026, 1.30pm)
  2.[📆][❗] meeting (from: Wed Aug 26 2026, 2.00pm to: Mon Sep 28 2026, 7.30pm)
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Invalid decimal and combined snooze durations

- Aim: Verify decimal calendar units, duplicate units, non-canonical order,
  abbreviations, extra markers, negative components, and arithmetic overflow
  are rejected.

### Input

```text
deadline report /by 26 Aug 2026 12pm
snooze 1 /by 1.5 months
snooze 1 /by 1 hour 2 hours
snooze 1 /by 2 days 1 month
snooze 1 /by 1m
snooze 1 /by 1h30min
snooze 1 /by 1 hour /to tomorrow
snooze 1 /by -30 minutes
snooze 1 /by 1000000000 years
list
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [⏳][❗] report (by: Wed Aug 26 2026, 12.00pm)
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Paiseh bro... i cannot settle decimal values for years and months yet...
  ____________________________________________________________
  ____________________________________________________________
  Eh can you be more specific anot, what do you mean by "1 hour 2 hours" sia?
  ____________________________________________________________
  ____________________________________________________________
  Eh can you be more specific anot, what do you mean by "2 days 1 month" sia?
  ____________________________________________________________
  ____________________________________________________________
  Eh can you be more specific anot, what do you mean by "1m" sia?
  ____________________________________________________________
  ____________________________________________________________
  Eh can you be more specific anot, what do you mean by "1h30min" sia?
  ____________________________________________________________
  ____________________________________________________________
  Eh HELLO you know how to type command one anot? 
  Lai lai let me teach you: `snooze <taskNumber> [/by <duration>] or <taskNumber> [/to <end date/time>]`
  ____________________________________________________________
  ____________________________________________________________
  Siao ah time where got negative one
  ____________________________________________________________
  ____________________________________________________________
  Siao ah delay so long, by that time your great grandson also die already la! Can be more reasonable anot!
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[⏳][❗] report (by: Wed Aug 26 2026, 12.00pm)
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Snooze timed tasks

- Aim: Verify default, duration-based, and explicit-ending-time snoozes, while
  rejecting a ToDo and preserving the event start time.

### Input

```text
deadline report /by 26 Aug 2026 12pm
event meeting /from 26 Aug 2026 2pm /to 3pm
snooze 1
snooze 2 /by 2 hours
snooze 2 /to 28 Aug 2026 5pm
list
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [⏳][❗] report (by: Wed Aug 26 2026, 12.00pm)
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📆][❗] meeting (from: Wed Aug 26 2026, 2.00pm to: Wed Aug 26 2026, 3.00pm)
  Now you got 2 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Nah here's your snooze you lazy bum, don't slack too much hor!
    [⏳][❗] report (by: Wed Aug 26 2026, 1.00pm)
  ____________________________________________________________
  ____________________________________________________________
  Nah here's your snooze you lazy bum, don't slack too much hor!
    [📆][❗] meeting (from: Wed Aug 26 2026, 2.00pm to: Wed Aug 26 2026, 5.00pm)
  ____________________________________________________________
  ____________________________________________________________
  Nah here's your snooze you lazy bum, don't slack too much hor!
    [📆][❗] meeting (from: Wed Aug 26 2026, 2.00pm to: Fri Aug 28 2026, 5.00pm)
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[⏳][❗] report (by: Wed Aug 26 2026, 1.00pm)
  2.[📆][❗] meeting (from: Wed Aug 26 2026, 2.00pm to: Fri Aug 28 2026, 5.00pm)
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Reschedule timed tasks

- Aim: Verify deadline rescheduling, partial event rescheduling, reversed
  event marker order, and the final task list after the changes.

### Input

```text
deadline report /by 26 Aug 2026 12pm
event meeting /from 26 Aug 2026 2pm /to 3pm
resched 1 /to 27 Aug 2026 5pm
resched 2 /to 4pm
resched 2 /from 26 Aug 2026 3pm
resched 2 /to 5pm /from 26 Aug 2026 4pm
list
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [⏳][❗] report (by: Wed Aug 26 2026, 12.00pm)
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📆][❗] meeting (from: Wed Aug 26 2026, 2.00pm to: Wed Aug 26 2026, 3.00pm)
  Now you got 2 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Nah here's your resched you lazy bum, don't slack too much hor!
    [⏳][❗] report (by: Thu Aug 27 2026, 5.00pm)
  ____________________________________________________________
  ____________________________________________________________
  Nah here's your resched you lazy bum, don't slack too much hor!
    [📆][❗] meeting (from: Wed Aug 26 2026, 2.00pm to: Wed Aug 26 2026, 4.00pm)
  ____________________________________________________________
  ____________________________________________________________
  Nah here's your resched you lazy bum, don't slack too much hor!
    [📆][❗] meeting (from: Wed Aug 26 2026, 3.00pm to: Wed Aug 26 2026, 4.00pm)
  ____________________________________________________________
  ____________________________________________________________
  Nah here's your resched you lazy bum, don't slack too much hor!
    [📆][❗] meeting (from: Wed Aug 26 2026, 4.00pm to: Wed Aug 26 2026, 5.00pm)
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[⏳][❗] report (by: Thu Aug 27 2026, 5.00pm)
  2.[📆][❗] meeting (from: Wed Aug 26 2026, 4.00pm to: Wed Aug 26 2026, 5.00pm)
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Invalid snooze and reschedule inputs

- Aim: Verify dedicated ToDo rejection, negative and unsupported durations,
  task-type-specific formats, missing event markers, and additional slash
  rejection.

### Input

```text
todo read book
deadline return book /by 26 Aug 2026 12pm
event project meeting /from 26 Aug 2026 2pm /to 3pm
snooze 1
snooze 2 /by -1 hour
snooze 2 /by 1.5 months
resched 2 /from tomorrow /to tomorrow
resched 3
snooze 2 /by 1 hour /to tomorrow
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📌][❗] read book
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [⏳][❗] return book (by: Wed Aug 26 2026, 12.00pm)
  Now you got 2 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📆][❗] project meeting (from: Wed Aug 26 2026, 2.00pm to: Wed Aug 26 2026, 3.00pm)
  Now you got 3 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Eh mr blur sotong this task don't even have time for you to snooze la
  ____________________________________________________________
  ____________________________________________________________
  Siao ah time where got negative one
  ____________________________________________________________
  ____________________________________________________________
  Paiseh bro... i cannot settle decimal values for years and months yet...
  ____________________________________________________________
  ____________________________________________________________
  Eh HELLO you know how to type command one anot? 
  Lai lai let me teach you: `resched <taskNumber> /to <date/time>`
  ____________________________________________________________
  ____________________________________________________________
  Eh HELLO you know how to type command one anot? 
  Lai lai let me teach you: `resched <taskNumber> /from <start date/time> /to <end date/time>`
  ____________________________________________________________
  ____________________________________________________________
  Eh HELLO you know how to type command one anot? 
  Lai lai let me teach you: `snooze <taskNumber> [/by <duration>] or <taskNumber> [/to <end date/time>]`
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Standardized relative-date terminology

- Aim: Verify current-week, following-week, next-next, coming-weekday, and
  relative day/month terminology, including a past event start.

### Input

```text
event this monday /from this monday /to this monday
event this sunday /from this sunday 2pm /to 3pm
event next wednesday /from next wednesday 2pm /to 3pm
event next next wednesday /from next next wednesday 2pm /to 3pm
event the following wednesday /from the following wednesday 2pm /to 3pm
event this coming wednesday /from this coming wednesday 2pm /to 3pm
event the coming tuesday /from the coming tuesday 2pm /to 3pm
list
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📆][❗] this monday (from: Mon Aug 24 2026, 12.00am to: Mon Aug 24 2026, 11.59pm)
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📆][❗] this sunday (from: Sun Aug 30 2026, 2.00pm to: Sun Aug 30 2026, 3.00pm)
  Now you got 2 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📆][❗] next wednesday (from: Wed Sep 02 2026, 2.00pm to: Wed Sep 02 2026, 3.00pm)
  Now you got 3 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📆][❗] next next wednesday (from: Wed Sep 09 2026, 2.00pm to: Wed Sep 09 2026, 3.00pm)
  Now you got 4 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📆][❗] the following wednesday (from: Wed Sep 09 2026, 2.00pm to: Wed Sep 09 2026, 3.00pm)
  Now you got 5 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📆][❗] this coming wednesday (from: Wed Aug 26 2026, 2.00pm to: Wed Aug 26 2026, 3.00pm)
  Now you got 6 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📆][❗] the coming tuesday (from: Tue Sep 01 2026, 2.00pm to: Tue Sep 01 2026, 3.00pm)
  Now you got 7 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📆][❗] this monday (from: Mon Aug 24 2026, 12.00am to: Mon Aug 24 2026, 11.59pm)
  2.[📆][❗] this sunday (from: Sun Aug 30 2026, 2.00pm to: Sun Aug 30 2026, 3.00pm)
  3.[📆][❗] next wednesday (from: Wed Sep 02 2026, 2.00pm to: Wed Sep 02 2026, 3.00pm)
  4.[📆][❗] next next wednesday (from: Wed Sep 09 2026, 2.00pm to: Wed Sep 09 2026, 3.00pm)
  5.[📆][❗] the following wednesday (from: Wed Sep 09 2026, 2.00pm to: Wed Sep 09 2026, 3.00pm)
  6.[📆][❗] this coming wednesday (from: Wed Aug 26 2026, 2.00pm to: Wed Aug 26 2026, 3.00pm)
  7.[📆][❗] the coming tuesday (from: Tue Sep 01 2026, 2.00pm to: Tue Sep 01 2026, 3.00pm)
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Find tasks by date

- Aim: Verify that `find [<description>] [/on <date>]` returns deadlines and events on the
  queried date, excludes ToDos, preserves original task numbers, and reports
  when no tasks match. Also verify that text before `/on` filters by
  description.

### Input

```text
todo read book
deadline return book /by 26 Aug 2026 11:59pm
event project meeting /from 25 Aug 2026 2pm /to 27 Aug 2026 4pm
find /on 26 Aug 2026
find book /on 26 Aug 2026
find /on 28 Aug 2026
find book
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📌][❗] read book
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [⏳][❗] return book (by: Wed Aug 26 2026, 11.59pm)
  Now you got 2 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📆][❗] project meeting (from: Tue Aug 25 2026, 2.00pm to: Thu Aug 27 2026, 4.00pm)
  Now you got 3 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do on: Aug 26 2026
  2.[⏳][❗] return book (by: Wed Aug 26 2026, 11.59pm)
  3.[📆][❗] project meeting (from: Tue Aug 25 2026, 2.00pm to: Thu Aug 27 2026, 4.00pm)
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do on: Aug 26 2026
  2.[⏳][❗] return book (by: Wed Aug 26 2026, 11.59pm)
  ____________________________________________________________
  ____________________________________________________________
  Chill lah bro got nothing yet lah!
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][❗] read book
  2.[⏳][❗] return book (by: Wed Aug 26 2026, 11.59pm)
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Invalid find commands

- Aim: Verify that a missing find query or search date, and invalid date text,
  produce validation errors without terminating the chatbot.

### Input

```text
find
find /on
find /on 32 Aug 2026
find /on definitely-not-a-date
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Eh HELLO you know how to type command one anot? 
  Lai lai let me teach you: `find [<description>] [/on <date>]`
  ____________________________________________________________
  ____________________________________________________________
  Eh HELLO you know how to type command one anot? 
  Lai lai let me teach you: `find [<description>] [/on <date>]`
  ____________________________________________________________
  ____________________________________________________________
  Eh mr smart alec you tell me your calendar and clock got tell you time like this one meh?
  ____________________________________________________________
  ____________________________________________________________
  Eh mr smart alec you tell me your calendar and clock got tell you time like this one meh?
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Named relative dates in find

- Aim: Verify that `today`, `tomorrow`/`tmr`, and `yesterday`/`ytd` resolve
  relative to the current date and that the queried date appears in each find
  header.

### Input

```text
deadline today task /by today
event yesterday event /from yesterday /to tomorrow
find /on today
find /on tmr
find /on ytd
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [⏳][❗] today task (by: Tue Aug 25 2026, 11.59pm)
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📆][❗] yesterday event (from: Mon Aug 24 2026, 12.00am to: Wed Aug 26 2026, 11.59pm)
  Now you got 2 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do on: Aug 25 2026
  1.[⏳][❗] today task (by: Tue Aug 25 2026, 11.59pm)
  2.[📆][❗] yesterday event (from: Mon Aug 24 2026, 12.00am to: Wed Aug 26 2026, 11.59pm)
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do on: Aug 26 2026
  2.[📆][❗] yesterday event (from: Mon Aug 24 2026, 12.00am to: Wed Aug 26 2026, 11.59pm)
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do on: Aug 24 2026
  2.[📆][❗] yesterday event (from: Mon Aug 24 2026, 12.00am to: Wed Aug 26 2026, 11.59pm)
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Task-number input matrix

- Aim: Verify missing, non-numeric, negative, zero, out-of-range, and extra task-number inputs, while confirming valid status changes still update the task.

### Input

```text
todo read book
mark
mark abc
mark 0
mark 2
mark 1 extra
list
mark 1
unmark
unmark abc
unmark -1
unmark 2
unmark 1 extra
list
unmark 1
list
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📌][❗] read book
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Eh which task you talking about har? Can say clearly anot.
  ____________________________________________________________
  ____________________________________________________________
  You siao ah how to spin this task from thin air?
  ____________________________________________________________
  ____________________________________________________________
  You siao ah how to spin this task from thin air?
  ____________________________________________________________
  ____________________________________________________________
  You siao ah how to spin this task from thin air?
  ____________________________________________________________
  ____________________________________________________________
  Eh which task you talking about har? Can say clearly anot.
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][❗] read book
  ____________________________________________________________
  ____________________________________________________________
  Swee lah you're done with this task:
    [📌][✅] read book
  ____________________________________________________________
  ____________________________________________________________
  Eh which task you talking about har? Can say clearly anot.
  ____________________________________________________________
  ____________________________________________________________
  You siao ah how to spin this task from thin air?
  ____________________________________________________________
  ____________________________________________________________
  You siao ah how to spin this task from thin air?
  ____________________________________________________________
  ____________________________________________________________
  You siao ah how to spin this task from thin air?
  ____________________________________________________________
  ____________________________________________________________
  Eh which task you talking about har? Can say clearly anot.
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][✅] read book
  ____________________________________________________________
  ____________________________________________________________
  Eh salah you're not done with this task ah, must remember to do ah!
    [📌][❗] read book
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][❗] read book
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Command casing, whitespace, and argument boundaries

- Aim: Verify handling of blank input, case-insensitive commands, surrounding whitespace, extra arguments, and invalid task-type formats.

### Input

```text
   
TODO   read book   
LIST extra
list
DEADLINE return book /by 2030-12-08 23:59
deadline return book /by
list
EVENT meeting /from 2030-12-09 14:00 /to 2030-12-09 16:00
event meeting /to 2030-12-09 16:00 /from 2030-12-09 14:00
list
bye now
BYE
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Eh you mute issit?? Just say what you want lah!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📌][❗] read book
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Why you so losor! Leave the list command to do its own thing lah
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][❗] read book
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [⏳][❗] return book (by: Sun Dec 08 2030, 11.59pm)
  Now you got 2 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Eh HELLO you know how to type command one anot? 
  Lai lai let me teach you: `deadline <description> /by <date/time>.`
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][❗] read book
  2.[⏳][❗] return book (by: Sun Dec 08 2030, 11.59pm)
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📆][❗] meeting (from: Mon Dec 09 2030, 2.00pm to: Mon Dec 09 2030, 4.00pm)
  Now you got 3 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Eh HELLO you know how to type command one anot? 
  Lai lai let me teach you: `event <description> /from <start date/time> /to <end date/time>.`
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][❗] read book
  2.[⏳][❗] return book (by: Sun Dec 08 2030, 11.59pm)
  3.[📆][❗] meeting (from: Mon Dec 09 2030, 2.00pm to: Mon Dec 09 2030, 4.00pm)
  ____________________________________________________________
  ____________________________________________________________
  Why you so losor! Leave the bye command to do its own thing lah
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Add and list all task types

- Aim: Verify that ToDos, deadlines, and events are added and displayed with the correct type markers and date/time text.

### Input

```text
todo borrow book
deadline return book /by 2030-12-08 23:59
event project meeting /from 2030-12-09 14:00 /to 2030-12-09 16:00
list
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📌][❗] borrow book
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [⏳][❗] return book (by: Sun Dec 08 2030, 11.59pm)
  Now you got 2 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📆][❗] project meeting (from: Mon Dec 09 2030, 2.00pm to: Mon Dec 09 2030, 4.00pm)
  Now you got 3 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][❗] borrow book
  2.[⏳][❗] return book (by: Sun Dec 08 2030, 11.59pm)
  3.[📆][❗] project meeting (from: Mon Dec 09 2030, 2.00pm to: Mon Dec 09 2030, 4.00pm)
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Mark and unmark a task

- Aim: Verify that a task can be marked done, returned to not done, and listed with the final status.

### Input

```text
todo read book
mark 1
unmark 1
list
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📌][❗] read book
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Swee lah you're done with this task:
    [📌][✅] read book
  ____________________________________________________________
  ____________________________________________________________
  Eh salah you're not done with this task ah, must remember to do ah!
    [📌][❗] read book
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][❗] read book
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Invalid commands and arguments

- Aim: Verify that unknown commands and malformed task commands produce helpful errors without terminating the session.

### Input

```text
unknown
deadline return book
event project /from Mon
mark 1
todo
bye now
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  What talking you? I only understand todo, deadline, event, list, mark, unmark, delete, find, snooze, resched, or bye, ok?
  ____________________________________________________________
  ____________________________________________________________
  Eh HELLO you know how to type command one anot? 
  Lai lai let me teach you: `deadline <description> /by <date/time>.`
  ____________________________________________________________
  ____________________________________________________________
  Eh HELLO you know how to type command one anot? 
  Lai lai let me teach you: `event <description> /from <start date/time> /to <end date/time>.`
  ____________________________________________________________
  ____________________________________________________________
  You siao ah how to spin this task from thin air?
  ____________________________________________________________
  ____________________________________________________________
  You don't tell me what to do how I know what to do???
  ____________________________________________________________
  ____________________________________________________________
  Why you so losor! Leave the bye command to do its own thing lah
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Repeated mark and unmark commands

- Aim: Verify that repeating `mark` keeps a task done and repeating `unmark` keeps it not done, instead of toggling it unexpectedly.

### Input

```text
todo read book
mark 1
mark 1
unmark 1
unmark 1
list
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📌][❗] read book
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Swee lah you're done with this task:
    [📌][✅] read book
  ____________________________________________________________
  ____________________________________________________________
  Swee lah you're done with this task:
    [📌][✅] read book
  ____________________________________________________________
  ____________________________________________________________
  Eh salah you're not done with this task ah, must remember to do ah!
    [📌][❗] read book
  ____________________________________________________________
  ____________________________________________________________
  Eh salah you're not done with this task ah, must remember to do ah!
    [📌][❗] read book
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][❗] read book
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Interleaved task creation edge cases

- Aim: Verify that invalid ToDo, Deadline, and Event commands do not add tasks, while valid commands before and after them are preserved.

### Input

```text
todo read book
todo
list
deadline return book /by 2030-12-08 23:59
deadline return book
list
event project meeting /from 2030-12-09 14:00 /to 2030-12-09 16:00
event project meeting /from 2030-12-09 14:00
list
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📌][❗] read book
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  You don't tell me what to do how I know what to do???
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][❗] read book
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [⏳][❗] return book (by: Sun Dec 08 2030, 11.59pm)
  Now you got 2 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Eh HELLO you know how to type command one anot? 
  Lai lai let me teach you: `deadline <description> /by <date/time>.`
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][❗] read book
  2.[⏳][❗] return book (by: Sun Dec 08 2030, 11.59pm)
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📆][❗] project meeting (from: Mon Dec 09 2030, 2.00pm to: Mon Dec 09 2030, 4.00pm)
  Now you got 3 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Eh HELLO you know how to type command one anot? 
  Lai lai let me teach you: `event <description> /from <start date/time> /to <end date/time>.`
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][❗] read book
  2.[⏳][❗] return book (by: Sun Dec 08 2030, 11.59pm)
  3.[📆][❗] project meeting (from: Mon Dec 09 2030, 2.00pm to: Mon Dec 09 2030, 4.00pm)
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Free-form task text and date/time strings

- Aim: Verify that ordinary task descriptions are retained, supported date/time values are normalized consistently, and marker-like slashes are rejected.

### Input

```text
todo /by /from /to !@#
list
deadline do homework /by 2030-12-08 09:00
event project meeting /from 2030-12-09 14:00 /to 2030-12-09 16:00
list
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Eh HELLO you know how to type command one anot? 
  Lai lai let me teach you: `todo <description>`
  ____________________________________________________________
  ____________________________________________________________
  Chill lah bro got nothing yet lah!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [⏳][❗] do homework (by: Sun Dec 08 2030, 9.00am)
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📆][❗] project meeting (from: Mon Dec 09 2030, 2.00pm to: Mon Dec 09 2030, 4.00pm)
  Now you got 2 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[⏳][❗] do homework (by: Sun Dec 08 2030, 9.00am)
  2.[📆][❗] project meeting (from: Mon Dec 09 2030, 2.00pm to: Mon Dec 09 2030, 4.00pm)
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Interleaved deletion and invalid deletion commands

- Aim: Verify that valid deletion removes the requested task and renumbers later tasks, while invalid deletion commands leave the task list unchanged.

### Input

```text
todo first task
todo second task
todo third task
mark 2
delete 4
list
delete 2
list
delete
list
delete 1 extra
list
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📌][❗] first task
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📌][❗] second task
  Now you got 2 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📌][❗] third task
  Now you got 3 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Swee lah you're done with this task:
    [📌][✅] second task
  ____________________________________________________________
  ____________________________________________________________
  You siao ah how to spin this task from thin air?
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][❗] first task
  2.[📌][✅] second task
  3.[📌][❗] third task
  ____________________________________________________________
  ____________________________________________________________
  Solid man can don't care about this one already:
    [📌][✅] second task
  But you still got 2 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][❗] first task
  2.[📌][❗] third task
  ____________________________________________________________
  ____________________________________________________________
  Eh which task you talking about har? Can say clearly anot.
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][❗] first task
  2.[📌][❗] third task
  ____________________________________________________________
  ____________________________________________________________
  Eh which task you talking about har? Can say clearly anot.
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][❗] first task
  2.[📌][❗] third task
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Interleaved task status edge cases

- Aim: Verify that invalid mark and unmark commands do not change a task's status when valid status changes occur between them.

### Input

```text
todo read book
mark 2
list
mark 1
unmark 2
list
unmark 1
mark
list
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📌][❗] read book
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  You siao ah how to spin this task from thin air?
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][❗] read book
  ____________________________________________________________
  ____________________________________________________________
  Swee lah you're done with this task:
    [📌][✅] read book
  ____________________________________________________________
  ____________________________________________________________
  You siao ah how to spin this task from thin air?
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][✅] read book
  ____________________________________________________________
  ____________________________________________________________
  Eh salah you're not done with this task ah, must remember to do ah!
    [📌][❗] read book
  ____________________________________________________________
  ____________________________________________________________
  Eh which task you talking about har? Can say clearly anot.
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][❗] read book
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Relative date phrases and invalid date/time values

- Aim: Verify that relative day, month, weekday, and year phrases are accepted by the date/time parser, while malformed times receive a clear error and do not add tasks.

### Input

```text
deadline bad /by the 15th 99:99
deadline bad /by next 15th 99:99
deadline bad /by next next Monday 99:99
deadline bad /by next next month 99:99
deadline bad /by the following month 99:99
deadline bad /by next year 99:99
deadline bad /by the following year 99:99
event bad /from the 15th 99:99 /to next year 99:99
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Eh mr smart alec you tell me your calendar and clock got tell you time like this one meh?
  ____________________________________________________________
  ____________________________________________________________
  Eh mr smart alec you tell me your calendar and clock got tell you time like this one meh?
  ____________________________________________________________
  ____________________________________________________________
  Eh mr smart alec you tell me your calendar and clock got tell you time like this one meh?
  ____________________________________________________________
  ____________________________________________________________
  Eh mr smart alec you tell me your calendar and clock got tell you time like this one meh?
  ____________________________________________________________
  ____________________________________________________________
  Eh mr smart alec you tell me your calendar and clock got tell you time like this one meh?
  ____________________________________________________________
  ____________________________________________________________
  Eh mr smart alec you tell me your calendar and clock got tell you time like this one meh?
  ____________________________________________________________
  ____________________________________________________________
  Eh mr smart alec you tell me your calendar and clock got tell you time like this one meh?
  ____________________________________________________________
  ____________________________________________________________
  Eh mr smart alec you tell me your calendar and clock got tell you time like this one meh?
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Year interpretation and role-specific time validation

- Aim: Verify that four-digit values are interpreted as years, past deadlines are rejected, and past event starts are allowed when the event end follows the start.

### Input

```text
deadline past deadline /by 25 Aug 2025
event past meeting /from 25 Aug 2025 /to 26 Aug 2025
list
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  you think you time travelling issit? check your date and time properly hor!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📆][❗] past meeting (from: Mon Aug 25 2025, 12.00am to: Tue Aug 26 2025, 11.59pm)
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📆][❗] past meeting (from: Mon Aug 25 2025, 12.00am to: Tue Aug 26 2025, 11.59pm)
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Event end-time reference resolution

- Aim: Verify that a time-only event end uses the event start date when it has not passed, and the next date when the event crosses midnight.

### Input

```text
event afternoon meeting /from 25 Aug 2026 2pm /to 4pm
event overnight meeting /from 25 Aug 2026 11pm /to 1am
list
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📆][❗] afternoon meeting (from: Tue Aug 25 2026, 2.00pm to: Tue Aug 25 2026, 4.00pm)
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📆][❗] overnight meeting (from: Tue Aug 25 2026, 11.00pm to: Wed Aug 26 2026, 1.00am)
  Now you got 2 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📆][❗] afternoon meeting (from: Tue Aug 25 2026, 2.00pm to: Tue Aug 25 2026, 4.00pm)
  2.[📆][❗] overnight meeting (from: Tue Aug 25 2026, 11.00pm to: Wed Aug 26 2026, 1.00am)
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```

## Test Case: Marker-like slash validation

- Aim: Verify that ordinary slashes and slash-separated dates remain valid while unsupported marker-like slashes are rejected consistently.

### Input

```text
todo read/book
todo read / book
todo read /book
deadline slash date /by 2026/08/26
snooze 3 /by 2 hours /please
list /now
bye /now
list
bye
```

### Expected output

```text
  ____________________________________________________________
     .--"""""--.
   /  /^\   /^\  \
  |  .---------.  |
  |  | | | | | |  |
   \ '---------' /
     '-._____.-'
    [NO SLACKING]
  LuckyNoSlacky is here to help!
  ____________________________________________________________
  Limpeh is LuckyNoSlacky, and I will confirm make sure you're lucky and not slacky!
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📌][❗] read/book
  Now you got 1 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [📌][❗] read / book
  Now you got 2 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Eh HELLO you know how to type command one anot? 
  Lai lai let me teach you: `todo <description>`
  ____________________________________________________________
  ____________________________________________________________
  Got one more thing to remember ah: 
    [⏳][❗] slash date (by: Wed Aug 26 2026, 11.59pm)
  Now you got 3 tasks to settle.
  ____________________________________________________________
  ____________________________________________________________
  Eh HELLO you know how to type command one anot? 
  Lai lai let me teach you: `snooze <taskNumber> [/by <duration>] or <taskNumber> [/to <end date/time>]`
  ____________________________________________________________
  ____________________________________________________________
  Why you so losor! Leave the list command to do its own thing lah
  ____________________________________________________________
  ____________________________________________________________
  Why you so losor! Leave the bye command to do its own thing lah
  ____________________________________________________________
  ____________________________________________________________
  Nah, all these things you need to do:
  1.[📌][❗] read/book
  2.[📌][❗] read / book
  3.[⏳][❗] slash date (by: Wed Aug 26 2026, 11.59pm)
  ____________________________________________________________
  ____________________________________________________________
  Huh so fast zao ah, rest well ah!
  ____________________________________________________________
```
