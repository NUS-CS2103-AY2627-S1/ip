# Add a JUnit test for `Parser.parseTaskIndex`

## Decision

`Parser.parseTaskIndex` is a good first JUnit target because it is a pure,
deterministic method: the same command and prefix always produce the same
zero-based index or invalid-index result. It does not require console input,
file I/O, or shared application state.

## Test coverage

`src/test/java/ParserTest.java` covers:

- valid indexes;
- whitespace-padded indexes;
- zero and negative indexes;
- alphabetic and decimal input;
- missing and blank indexes;
- extra tokens; and
- integer overflow.

The test follows the existing default-package source layout because the
production classes have not yet been moved into a package. No production files
were modified.

## Suggested commit message

```text
Add JUnit tests for task index parsing
```

