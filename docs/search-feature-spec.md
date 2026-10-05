# Flexible Search Feature Specification

## Existing behavior

`find <keyword>` currently performs a case-sensitive substring search over task descriptions and displays matching tasks using result-local numbering.

The new behavior keeps the same command syntax but changes matching and numbering.

## User command

```text
find <query>
```

Examples:

```text
find book
find BOOK
find boo
find ook
find book club
find club book
find b
```

The command remains case-insensitive at the command level and continues accepting arbitrary non-empty query text.

### Invalid commands

```text
find
find <whitespace-only>
```

Exact output:

```text
OOPS! How do I even find nothing??
```

Whitespace-only queries are treated as empty. One-character queries are valid.

There are no new validation restrictions for non-empty queries.

## Matching rules

1. Search only the task description.
2. Matching is case-insensitive using locale-independent lowercasing.
3. Normalize leading/trailing whitespace and collapse repeated internal whitespace.
4. Split the query into words.
5. Every query word must match somewhere in the description.
6. Query words may match description words in any order.
7. A query word may match:
   - an entire description word;
   - a prefix of a description word;
   - any substring of a description word.
8. Punctuation remains significant during normalization.
9. Search does not inspect task type, completion status, deadline dates, or event dates.
10. Repeated query words do not impose additional requirements.

Examples:

| Query | Description | Match |
| --- | --- | --- |
| `BOOK` | `read book` | Yes |
| `boo` | `read book` | Yes |
| `ook` | `read book` | Yes |
| `club book` | `book club` | Yes |
| `book report` | `write report` | No |
| `2019` | deadline dated in 2019 | No |
| `b` | `buy milk` | Yes |

## Result ordering

Results use two matching tiers:

1. Exact-token matches first.
2. Partial-token matches second.
3. Within each tier, preserve the original task-list order.

For this specification, a task is an exact-token match when every query word matches a complete description word. Additional description words are allowed.

For example, with:

```text
1. notebook shopping
2. buy book
3. book club
4. book
```

the command:

```text
find book
```

produces:

```text
Here are the matching tasks in your list (numbers refer to your full task list):
2. [T][ ] buy book
3. [T][ ] book club
4. [T][ ] book
1. [T][ ] notebook shopping
```

`buy book`, `book club`, and `book` are exact-token matches. `notebook shopping` is a substring-only match.

## Display

Successful searches use this header:

```text
Here are the matching tasks in your list (numbers refer to your full task list):
```

Each result uses its original one-based task-list number, not its position within the search results.

This avoids ambiguity when results are reordered or when there are gaps:

```text
2. [T][ ] buy book
4. [D][ ] return book (by: Dec 02 2019 18:00:00)
```

Task formatting remains unchanged. No match highlighting, snippets, match scores, or result counts are added.

No results continue to use the existing message:

```text
No matching tasks found :(
```

## Complete example

Input:

```text
todo notebook shopping
todo buy book
event book club /from 2019-12-03 1400 /to 2019-12-03 1600
todo book
find BOOK
find club book
find 2019
find b
find
bye
```

Relevant output:

```text
Here are the matching tasks in your list (numbers refer to your full task list):
2. [T][ ] buy book
3. [E][ ] book club (from: Dec 03 2019 14:00:00 to: Dec 03 2019 16:00:00)
4. [T][ ] book
1. [T][ ] notebook shopping
Here are the matching tasks in your list (numbers refer to your full task list):
3. [E][ ] book club (from: Dec 03 2019 14:00:00 to: Dec 03 2019 16:00:00)
No matching tasks found :(
Here are the matching tasks in your list (numbers refer to your full task list):
1. [T][ ] notebook shopping
2. [T][ ] buy book
3. [E][ ] book club (from: Dec 03 2019 14:00:00 to: Dec 03 2019 16:00:00)
4. [T][ ] book
OOPS! How do I even find nothing??
```

## Storage format

Search is read-only and does not modify tasks.

The storage format remains unchanged:

```text
T | 0 | buy book
D | 0 | return book | 2019-12-02T18:00
E | 0 | book club | 2019-12-03T14:00 | 2019-12-03T16:00
```

No search queries, indexes, preferences, or history are saved.

## Compatibility rules

- Existing `find <keyword>` syntax remains valid.
- Existing save files remain valid without migration.
- Search remains description-only.
- Searches do not alter task state or trigger saving.
- Console and JavaFX behavior remain identical.
- Existing commands such as `mark 2` and `delete 2` continue using original task-list numbers.
- Existing no-match and empty-query messages remain unchanged.
- Existing searches become broader because matching is now case-insensitive and supports token-level partial matches.
- Existing output expectations involving search headers or result numbering must be updated.

## Acceptance criteria

1. `find` with no payload displays the existing empty-query error.
2. A one-character query is accepted.
3. Matching ignores letter case.
4. Prefix and internal substring matches work.
5. Multi-word queries require all words to match.
6. Multi-word query words may appear in any order.
7. Search considers descriptions only.
8. Date-only matches do not produce results.
9. Exact-token matches appear before partial-token matches.
10. Matching tasks preserve original task-list order within each tier.
11. Results display original task-list numbers.
12. The header explains that displayed numbers refer to the full task list.
13. No-match output remains unchanged.
14. Search does not save or modify tasks.
15. Existing save files load and save exactly as before.
16. Console UI tests cover:
    - case-insensitive matching;
    - prefix matching;
    - internal substring matching;
    - one-character queries;
    - multi-word queries in reversed order;
    - exact-before-partial ordering;
    - original-number display;
    - description-only matching;
    - no results;
    - empty queries.

## Files likely to change during implementation

Likely implementation and test changes:

- `src/main/java/gatsby/command/FindCommand.java`
  - Implement normalization, token matching, ranking, and original-number display.
- `src/test/java/gatsby/command/FindCommandTest.java`
  - Add unit tests for matching, ranking, numbering, and validation.
- `test/ui-test-plan.md`
  - Revise the existing `find` scenario and add the selected edge cases.

Files not expected to change:

- `src/main/java/gatsby/parser/Parser.java`
- `src/main/java/gatsby/storage/Storage.java`
- `src/main/java/gatsby/model/Task.java` or task subclasses
- `src/main/java/gatsby/model/TaskList.java`
- `src/main/java/gatsby/ui/MainWindow.java`
- `src/main/java/gatsby/command/HelpCommand.java`

Product documentation such as `docs/README.md` and the root `README.md` remains deferred to the next planning step.
