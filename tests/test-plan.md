# Penny Test Plan

This document outlines the testing strategy, test cases, and verification procedures for Penny, with a focus on the `C-BetterSearch` flexible search feature.

---

## 1. Automated Testing Strategy

Penny employs automated unit and regression testing with JUnit 5:
* **`TaskListTest`**: Validates task management logic, index validation, assertions, and keyword filtering.
* **`ParserTest`**: Validates command syntax parsing, argument extraction, and domain exception handling.
* **`UiTest`**: Validates text formatting for lists, confirmation messages, and search query results.

All tests are verified via Gradle:
```bash
.\gradlew.bat test --rerun
```

Code style and formatting compliance are verified via Checkstyle:
```bash
.\gradlew.bat checkstyleMain checkstyleTest
```

---

## 2. Test Plan: `C-BetterSearch` (Flexible Keyword Search)

### 2.1 Automated Test Cases (`TaskListTest`)

| Test Method | Input Query | Target Tasks | Expected Outcome | Pass Criteria |
|---|---|---|---|---|
| `findTasksByKeyword_matchingKeyword_returnsMatchingTasks` | `"book"` | `[T] read book`, `[D] submit assignment` | Returns `[T] read book` | 1 match returned |
| `findTasksByKeyword_caseInsensitive_returnsMatchingTasks` | `"BOOK"` | `[T] read book` | Returns `[T] read book` | Case-insensitive match |
| `findTasksByKeyword_partialKeyword_matches` | `"sub assign"` | `[D] submit assignment` | Returns `[D] submit assignment` | Matches partial sub-words |
| `findTasksByKeyword_multipleKeywords_matchesConjunctive` | `"read book"`, `"book read"` | `[T] read book`, `[D] submit assignment` | Returns `[T] read book` for both | Word-order independent conjunctive (AND) match |
| `findTasksByKeyword_partialMismatch_returnsEmptyList` | `"read assignment"` | `[T] read book`, `[D] submit assignment` | Returns empty list | Requires all keywords to be present in the same task |
| `findTasksByKeyword_nonMatchingKeyword_returnsEmptyList` | `"nonexistent"` | `[T] read book`, `[D] submit assignment` | Returns empty list | 0 matches |
| `findTasksByKeyword_whitespaceOnly_returnsEmptyList` | `"   "` | `[T] read book` | Returns empty list | Graceful handling without false matches |
| `findTasksByKeyword_nullKeyword_assertionError` | `null` | Any | Throws `AssertionError` | Precondition enforcement |

### 2.2 Parser & Error Handling Test Cases (`ParserTest`)

| Test Method | Input Command | Expected Outcome |
|---|---|---|
| `parse_findValidKeyword_findCommandCreated` | `find book` | Returns `FindCommand` instance |
| `parse_findEmptyKeyword_exceptionThrown` | `find   ` | Throws `PennyException`: *"Please specify a keyword to search for"* |
| `parse_findReservedDelimiterPipe_exceptionThrown` | `find book \| novel` | Throws `PennyException`: *"Search keyword cannot contain the '\|' character"* |

---

## 3. Manual Testing Guide

1. **Launch the application:**
   ```bash
   .\gradlew.bat run
   ```

2. **Add sample tasks:**
   ```text
   todo read biology textbook
   deadline submit math homework /by 2026-10-15
   event biology consultation /from 2026-10-20 1400 /to 2026-10-20 1500
   ```

3. **Execute search test scenarios:**
   * **Test Scenario 1 — Multi-word search (AND matching):**
     - Command: `find biology textbook`
     - Expected: Displays only `[T][ ] read biology textbook`.
   * **Test Scenario 2 — Word-order independence:**
     - Command: `find textbook biology`
     - Expected: Displays `[T][ ] read biology textbook`.
   * **Test Scenario 3 — Partial word matching:**
     - Command: `find bio text`
     - Expected: Displays `[T][ ] read biology textbook` (matches `bio` in `biology` and `text` in `textbook`).
   * **Test Scenario 4 — Multiple matches:**
     - Command: `find bio`
     - Expected: Displays both `read biology textbook` and `biology consultation`.
   * **Test Scenario 5 — No match:**
     - Command: `find chemistry`
     - Expected: Displays `No matching tasks found for keyword: 'chemistry'.`
   * **Test Scenario 6 — Empty query error:**
     - Command: `find`
     - Expected: Displays error message prompting for a keyword.
