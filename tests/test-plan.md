# C-Tagging test plan

## Scope and automated checks

Run `./gradlew check` (`.\gradlew.bat check` on Windows) using Java 25. This runs JUnit and Checkstyle.
Tests use temporary files; never run failure or corruption scenarios against your only copy of personal data.

| Area | Scenarios | Automated coverage |
| --- | --- | --- |
| Creation and responses | Optional/repeated trailing tags; all three types; exact success output; extra spaces | `TaggingTest` |
| Name validation | 1 and 30 characters accepted; 31 rejected; allowed characters; blank, spaces, quotes, #, comma, pipe rejected | `TaskTest`, `TaggingTest` |
| Identity | Case-insensitive matching; first spelling; insertion order; removing/re-adding; same tag on different tasks; Turkish locale | `TaskTest`, `TaggingTest` |
| Existing tasks | One tag per command; missing/extra arguments; invalid numbers; duplicate/missing no-ops; completed tasks | `TaggingTest`, `TagCommandTest` |
| Search | Description-only matching; tags visible; original numbers for find and checkdate; correct target; deletion shifts numbers | `TaggingTest` |
| Storage | Old/mixed/tagged files; all types and completion states; literal /tag descriptions; exact saved lines; no rewrite on load; last tag removal | `StorageTest`, `TaggingTest` |
| Bad files | Empty tag field/items, bad names, extra fields; line-specific error; no partial load; blocked saves and commands; repair/restart | `StorageTest`, `TaggingTest` |
| Save failure | Inject failure after temporary write, before replacement; preserve file, task count, tag order/spelling; clean temporary file; no success response | `TagCommandTest` |
| Regression | Existing parsing, search, help, completion, deletion, and app persistence | Existing JUnit suites |

Validation precedence: command arity, task number, tag validity, then duplicate/missing status. Creation validates
the entire tag section before parsing description/dates. No invalid command may produce a partial change.

## Manual GUI acceptance checklist

These checks require an interactive app run; unchecked items are not claims of verification.
Use a disposable working directory/data file or back up and restore your existing data outside the app.

- [ ] Start the GUI, run `todo sleep /tag personal`, and compare the response with the user guide.
- [ ] Add `todo prepare slides /tag CS2103T /tag school`, a tagged deadline, and a tagged event.
- [ ] Run `list`, `find slides`, and `checkdate` for an event/deadline date. Tags appear once, after dates.
- [ ] Add enough unrelated tasks to produce gaps in search numbering. Tag the displayed number and verify only that task changes.
- [ ] Run `tag 2 School` twice, then `untag 2 SCHOOL` and `untag 2 missing`. Check spelling and no-change messages.
- [ ] Remove and re-add a tag. Confirm the tag moves to the end; remove all tags and confirm no leftover brackets/spaces.
- [ ] Mark, unmark, and delete tagged tasks. Confirm their response text includes tags and other task data stays correct.
- [ ] Try invalid syntax from the user guide. Confirm a useful error, no partial updates, and the next valid command still works.
- [ ] Test several 30-character tags at a narrow window width. Check wrapping, readability, scrolling, and no clipped text.
- [ ] Open **Commands** and type `help`. Both show concise command syntax and examples, including creation-time tags,
  `tag`, and `untag`. Detailed validation, search numbering, and compatibility rules remain in the user guide.
- [ ] Restart the app. Verify tags, dates, order, and completion status survive.
- [ ] With the app closed and disposable data, introduce an invalid tag on line 3. Restart and verify the startup error is visible.
- [ ] In that blocked state, attempt `list`, `todo`, `tag`, and `delete`; confirm the file is unchanged. `help` and `bye` still work.
- [ ] Repair the file and restart. Confirm normal operation resumes.

## Failure checks

JUnit injects a deterministic atomic-replacement failure after a temporary file has been written. This checks both
disk preservation and in-memory rollback without depending on OS-specific permission settings. No non-atomic
replacement fallback is permitted. All modifying commands restore their in-memory state after a failed save;
see [More error handling](more-error-handling.md) for the additional failure and input checks.

Run the same Gradle checks in CI on Ubuntu, macOS, and Windows. Local success alone does not establish that all
three CI jobs have passed. GUI appearance and real file-system restrictions remain manual/environment checks.
