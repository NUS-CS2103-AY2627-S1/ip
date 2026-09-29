# Pavanmaxxer iP Final Design

## Objective

Prepare Pavanmaxxer for its final individual-project release while preserving
its current task-management behavior. The release will satisfy the Week 6
requirements, improve handling of common invalid inputs, expand automated test
coverage, replace starter documentation, and produce a verified Java 25 fat
JAR.

Public application text and public documentation will describe only the
product and its behavior. They will not discuss development tooling or the
process used to create the product.

## Scope

### Error handling

The application will provide clear responses for:

- listing or finding tasks when there are no matching entries;
- singular and plural task counts;
- missing, repeated, misplaced, or empty `/by`, `/from`, and `/to` fields;
- extra arguments supplied to commands that do not accept them;
- descriptions or event values that would corrupt the storage format;
- unreadable, unwritable, or malformed saved data.

The application will continue warning about duplicate tasks while adding and
saving them as requested.

### Testing

Tests will cover the new validation rules and important command flows through
the application core. Storage tests will cover valid round trips and malformed
input. Existing duplicate-detection and task-list tests will remain intact.

Each production behavior change will be preceded by a failing test that proves
the missing behavior.

### User interface

The existing JavaFX layout will be retained. Only focused usability polish is
in scope: readable spacing and colours, clearly differentiated application and
user messages, a useful empty-state response, and correct product naming.

### Documentation and website

The starter `README.md` and `docs/README.md` content will be replaced with
Pavanmaxxer-specific material. The User Guide will document installation,
supported commands, examples, expected results, data storage, duplicate
handling, and common errors.

`docs/Ui.png` will show one complete Pavanmaxxer window, with the product name
visible and no pointer or cursor visible. GitHub Pages configuration itself is
performed in repository settings and will be verified separately.

### Packaging and verification

The release candidate will be built with Java 25 using:

```text
./gradlew clean test checkstyleMain checkstyleTest shadowJar
```

The generated fat JAR will then be copied to an empty temporary directory and
started with `java -jar`. Core commands and persistence will be smoke-tested.
Only after those checks will the release be considered ready for a GitHub
release containing one JAR asset.

## Architecture and data flow

The existing structure remains unchanged:

1. `MainWindow` obtains the user's command and passes it to `Pavanmaxxer`.
2. `Pavanmaxxer` classifies and coordinates the command.
3. `Parser` validates command arguments and creates domain objects.
4. `TaskList` performs collection operations.
5. `Storage` persists the updated list.
6. `Pavanmaxxer` returns a user-facing response to the GUI.

Validation that concerns command syntax belongs in `Parser`. Collection rules
belong in `TaskList`. File-format validation belongs in `Storage`. Response
wording and orchestration remain in `Pavanmaxxer`. This keeps each class at a
single level of abstraction and avoids adding new subsystems for final-release
work.

## Out of scope

- A new application architecture or command hierarchy.
- New task types or editing commands.
- Network services, accounts, or cloud storage.
- Major visual redesigns or animations.
- Automatic publication of the GitHub release without explicit approval.

## Success criteria

- At least the `A-MoreErrorHandling` and `A-MoreTesting` increments are
  demonstrably completed.
- All automated tests and Checkstyle checks pass on Java 25.
- The fat JAR starts and performs core task operations from an empty directory.
- The User Guide accurately covers all supported commands.
- `docs/Ui.png` meets the product-screenshot requirements and hides the cursor.
- Public product text contains no development-process disclosures.
- The repository is ready for, but not automatically committed, pushed, or
  released without explicit approval.
