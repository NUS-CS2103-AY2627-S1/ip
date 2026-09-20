# Week 6 Final Polish Design

## Purpose

Complete the CS2103/T Week 6 individual-project milestone for Nexus. The final
product will implement all four optional increments (`A-BetterGui`,
`A-Personality`, `A-MoreErrorHandling`, and `A-MoreTesting`), provide a concise
product website, and produce a Java 25-compatible release JAR.

## Product Direction

Nexus remains a focused command-driven task manager for todos, deadlines, and
events. The final polish must improve usability and resilience without adding
unnecessary commands or changing the existing persisted-data format.

Nexus's voice is encouraging, calm, and concise. It behaves like a productivity
companion rather than another human participant: it acknowledges actions,
explains mistakes constructively, and avoids lengthy or distracting replies.

## User Interface

The JavaFX interface will use a compact, responsive conversation layout:

- The window is resizable with sensible minimum dimensions.
- The conversation and input area grow with the window.
- User messages appear on the right; Nexus messages appear on the left.
- Avatars are small and rounded so message content receives most of the space.
- User, Nexus, and error messages have visually distinct bubble styles.
- Errors use a red-tinted treatment to attract attention without reducing text
  contrast.
- A short command hint and input prompt improve discoverability.
- A calm blue/indigo palette, consistent spacing, and readable typography give
  the application a cohesive appearance.

Message appearance will be selected from response metadata rather than by
searching the displayed text for words such as `OOPS`.

## Response Model

Command processing will return an immutable response object containing:

- the text shown to the user;
- whether the response represents an error; and
- whether the application should exit after displaying it.

`Nexus#getResponse(String)` will remain available as a compatibility helper for
the console interface and existing callers. The GUI will use the richer
response object to choose the message style and exit behavior.

## Command Parsing and Validation

Existing commands and their successful effects remain unchanged:

- `todo DESCRIPTION`
- `deadline DESCRIPTION /by YYYY-MM-DD`
- `event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD`
- `list`
- `find KEYWORD`
- `sort`
- `mark NUMBER`
- `unmark NUMBER`
- `delete NUMBER`
- `bye`

Leading and trailing whitespace is ignored. Repeated whitespace between a
command word and its first parameter is accepted. Whitespace within task
descriptions is normalized to single spaces so accidental spacing does not
create visually different duplicates.

Nexus will report specific, readable errors for:

- blank input;
- unknown commands;
- missing task descriptions or search keywords;
- missing, repeated, or incorrectly ordered date markers;
- invalid or impossible ISO dates;
- an event end date that is not after its start date;
- task numbers that are non-numeric, zero, negative, or outside the list;
- malformed saved records; and
- files that cannot be read or written.

Validation must not expose Java exception text or terminate the GUI.

## Storage Recovery

The existing `data/nexus.txt` format remains compatible. A missing file starts
an empty task list. When a file contains malformed records, valid records are
loaded and Nexus reports that some data could not be restored. If the whole
file is unreadable, Nexus starts safely with an empty list and reports the
problem in its greeting. Save failures are returned to the user as command
errors and do not crash the application.

## Testing

Production changes will follow a red-green-refactor cycle. Automated tests will
cover:

- accepted whitespace variants;
- every missing or repeated parameter case;
- impossible dates and invalid event date ordering;
- invalid task indices and empty searches;
- structured success, error, and exit responses;
- recoverable malformed storage data and storage I/O failures;
- personality text for greeting, success, failure, and farewell paths;
- responsive FXML constraints and message-style selection; and
- packaging of every required JavaFX, stylesheet, and image resource.

All tests and Checkstyle checks must pass using Java 25.0.3.

## Product Website

`docs/README.md` will clearly state the Nexus name at the top and provide short,
user-oriented guidance for every important feature. It will include command
formats, examples, validation rules, data-file behavior, and an embedded image
of the application.

`docs/Ui.png` will be a single screenshot of the complete running GUI window,
with the Nexus name visible and representative user, success, and error
messages in the conversation.

## Packaging and Release

Gradle's Shadow plugin will create exactly one cross-platform fat JAR named
`nexus.jar`. The release workflow will:

1. activate Java 25.0.3;
2. run the complete test and Checkstyle suite;
3. run `./gradlew clean shadowJar`;
4. copy the JAR into a newly-created empty temporary directory;
5. launch it there to verify that resources and paths are self-contained;
6. create a lightweight release tag;
7. push the completed commits and tag only after independent subagent reviews;
8. enable or verify GitHub Pages from `master` and `/docs`; and
9. publish a GitHub release containing only `nexus.jar` as its JAR asset.

The product website and release will then be checked through their public URLs.

## Scope Boundaries

The final polish will not add new task categories, graphical task editors,
accounts, networking, reminders, or a new storage format. Those additions are
not required for Week 6 and would increase release risk without improving the
required learning outcomes.

## Completion Criteria

The work is complete only when:

- all four optional increments are evident in the code and tests;
- every existing and newly-added automated check passes on Java 25.0.3;
- the GUI has been visually inspected at its minimum and enlarged sizes;
- `docs/README.md` documents every supported command;
- `docs/Ui.png` meets the course filename and screenshot rules;
- the fat JAR passes the isolated-directory smoke test;
- independent subagents find no unresolved blocking issue;
- the pushed repository and public GitHub Pages site contain the final docs;
  and
- the latest public GitHub release contains exactly one intended JAR asset.
