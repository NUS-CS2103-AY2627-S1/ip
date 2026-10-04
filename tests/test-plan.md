# C-Tagging Test Plan

## Scope

Verify optional task tags, the `tag` and `untag` commands, tag-aware search,
display ordering, persistence, and compatibility with existing save files.

## Parser and validation

| Case | Input | Expected result |
| --- | --- | --- |
| Valid suffix | `todo play game #Weekend #fun` | Creates `play game` with `#fun #weekend`. |
| Valid deadline suffix | `deadline return book /by 2019-10-15 #school` | Preserves the deadline and adds `#school`. |
| Valid event suffix | `event party /from 2pm /to 4pm #fun` | Preserves both event times and adds `#fun`. |
| Invalid syntax | `todo play game #invalid!` | Rejects the whole command. |
| Misplaced tag | `todo play #fun later` | Rejects the whole command. |
| Duplicate tag | `todo play #fun #FUN` | Rejects the whole command. |
| More than three tags | `todo play #one #two #three #four` | Rejects the whole command. |

## Commands and search

| Case | Input | Expected result |
| --- | --- | --- |
| Add multiple tags | `tag 1 #fun #school` | Adds both tags in one operation. |
| Remove multiple tags | `untag 1 #fun #school` | Removes both tags in one operation. |
| Atomic add failure | `tag 1 #school #fun` when `#fun` exists | Leaves the task unchanged. |
| Atomic remove failure | `untag 1 #fun #missing` | Leaves all existing tags unchanged. |
| Tag search | `find week` for a task tagged `#weekend` | Returns the matching task. |
| Display ordering | Any task with `#z` and `#a` | Displays `#a #z`. |

## Storage and compatibility

- Save and reload tagged todo, deadline, and event records.
- Load legacy records without a tag field as untagged tasks.
- Skip malformed tagged records without discarding valid records in the same file.
- Keep the old format when saving untagged tasks.
