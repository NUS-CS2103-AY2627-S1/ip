# Megatron User Guide

Megatron is a local task manager controlled through simple text commands. It
speaks like an intimidating evil Transformer and stores tasks in
`data/megatron.txt`.

![Megatron GUI](Ui.png)

## Getting started

Launch the GUI from the project root:

```bash
./gradlew run
```

Enter a command in the text box and press **Enter** or click **⚙ Send**.

## Managing tasks

### Add tasks

```text
todo Prepare quarterly presentation
deadline Submit project report /by 2026-10-15 1700
event Team strategy meeting /from 2026-10-10 1400 /to 2026-10-10 1600
```

### View and search tasks

```text
list
find project
```

### Update tasks

```text
mark 1
unmark 1
snooze 2 2026-10-16 1700
delete 3
```

Task numbers refer to the numbered entries shown by `list`. Only deadline and
event tasks can be snoozed.

### Exit

```text
bye
```

Megatron handles blank or malformed commands, impossible dates, invalid event
times, duplicate tasks, and missing or malformed saved data gracefully.
