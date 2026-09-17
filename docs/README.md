# Lebron User Guide

// Update the title above to match the actual product name

// Product screenshot goes here

// Product intro goes here

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

// A description of the expected outcome goes here

```
expected output
```

## Recurring tasks

Add `/every day`, `/every week`, or `/every month` to a `deadline` or `event`
command to make it repeat. `/every` must come after the command's date(s)
(after `/by` for a deadline, after `/to` for an event).

Example: `deadline pay rent /by 2025-01-01 /every month`
```
on the board: [D][ ] pay rent (by: Jan 01 2025) (every: month)
```

Marking a recurring task done doesn't check it off -- it rolls the date(s)
forward to the next occurrence instead, and stays unchecked:

Example: `mark 1`
```
Nice bucket! Since this one's a repeat play, I've already queued up the next rep:
  [D][ ] pay rent (by: Feb 01 2025) (every: month)
```

For a recurring event, both the start and end shift forward together, so
the event keeps the same duration.

## Feature ABC

// Feature details


## Feature XYZ

// Feature details