# C-Tagging Specification

## Overview

Allow users to attach one or more tags to tasks and retrieve tasks
using one or more tags.

## Creating tagged tasks

Tags are written using `#tag` syntax and may appear at the end of a
task command.

Examples:

    todo laundry #errands

    todo study CS2103T #school #programming

    deadline submit CS2103T Quiz /by 2026-09-10 #school #urgent

    event team meeting /from 2026-09-15 1000 /to 2026-09-15 1100 #school

Tags may only appear at the end of the task command.

Examples:

    todo study CS2103T #school
    todo learn C# #programming

The `#` in `C#` is part of the description and is not treated as a tag.

## Tag rules

- A tag begins with `#`.
- A tag contains one or more letters, digits, `_`, or `-`.
- Tags may be repeated in input, but duplicates are stored only once.
- Tag matching is case-insensitive.
- Tags are stored internally without the leading `#`.
- Tags are displayed using a leading `#` and normalised to lowercase.

Examples of valid tags:

    #school
    #cs2103t
    #high-priority
    #week_5

Invalid tag forms:

    #
    #hello world

## Task representation

Tags are stored separately from the task description.

For example:

    description = "study CS2103T"
    tags = ["school", "urgent"]

Tags are displayed as part of the task's normal string representation.

For example:

    [ ] study CS2103T #school #urgent

## Searching by tag

Add the command:

    tag #school

The command lists every task containing the specified tag.

Multiple tags may be supplied:

    tag #school #urgent

When multiple tags are supplied, a task must contain all specified
tags to be included in the results.

For example:

    Task A: #school #urgent
    Task B: #school
    Task C: #urgent
    Task D: #school #urgent #exam

For:

    tag #school #urgent

only Task A and Task D are returned.

Tag matching is case-insensitive.

Examples:

    tag #school
    tag #School
    tag #SCHOOL

all refer to the same tag.

Duplicate search tags are treated as one tag:

    tag #school #school

is equivalent to:

    tag #school

The original task numbers are preserved in tag search results.

If no tasks match:

    Nothing has all of those tags.

## Storage

Tags are persisted when tasks are saved.

Existing task records that do not contain a tag field remain valid and
load with an empty tag list.

New task records include the task's tags in the storage representation.

## Compatibility

Existing commands and existing untagged task data must continue to work
unchanged.

Descriptions may contain `#` characters when they are not valid tag
tokens.

For example:

    todo learn C# programming

remains a normal untagged task.

## Acceptance criteria

1. Todo, deadline, and event commands accept one or more tags.
2. Tags are stored separately from task descriptions.
3. Tags are displayed with tasks.
4. Multiple tags are supported.
5. Duplicate tags on a task are stored once.
6. Tags are normalised to lowercase when stored and displayed.
7. Tag matching is case-insensitive.
8. `tag #name` lists tasks carrying that tag.
9. Multiple tags may be supplied to the `tag` command.
10. A task is returned only when it contains all requested tags.
11. Duplicate search tags are treated as one tag.
12. Original task numbers are preserved in tag search results.
13. A useful response is produced when no tasks match.
14. Tags survive saving and reloading.
15. Existing untagged storage records remain loadable.
16. Existing commands retain their previous behaviour.
17. Descriptions containing non-tag `#` characters remain valid.
18. New tag behaviour has JUnit coverage.
19. Existing tests continue to pass.
20. Checkstyle and the full Gradle build continue to pass.
