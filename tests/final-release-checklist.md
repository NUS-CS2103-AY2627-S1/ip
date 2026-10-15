# Final release checklist

## Local automated checks

- [ ] Java 25 selected: `java -version`.
- [ ] `./gradlew clean test checkstyleMain checkstyleTest shadowJar` succeeds.
- [ ] Review the actual GUI; automated unit tests do not establish visual correctness.

## Manual smoke test (project owner)

Copy the JAR into a new empty folder and run `java -jar wwaffle.jar` there.
Do not test against the only copy of personal saved tasks.

- [ ] First launch shows no tasks; create todo, deadline, and event.
- [ ] `list`, `find`, `mark`, `unmark`, `sort name`, `sort status`, `delete`, and `clear` work.
- [ ] Bad dates, missing arguments, repeated delimiters, and invalid task numbers show errors.
- [ ] Narrow/wide windows wrap descriptions and complete lyrics without clipping.
- [ ] Goodbye closes; restart reloads the same tasks and statuses.
- [ ] Images/fonts load with only the JAR present.
- [ ] Test on Windows x64, Linux x64, and macOS Apple Silicon with Java 25.
- [ ] Intel Mac is NOT yet verified: packaged macOS native binaries target Apple Silicon.

## Website and public release (project owner)

- [ ] Replace `docs/Ui.png` with a fresh full-window screenshot of the final app.
- [ ] Confirm User Guide matches final behavior.
- [ ] Complete original artwork source credits / public asset selection.
- [ ] Inspect `git status`, current branch, and remotes before committing.
- [ ] Commit changes and apply required increment tags using course naming rules.
- [ ] Push code/tags; enable GitHub Pages from the master branch `/docs` folder.
- [ ] Check the site and `/Ui.png` in a private/incognito browser window.
- [ ] Create the release and upload exactly one evaluated `.jar` asset, not a ZIP.
- [ ] Check the public release and course dashboard (including Git Standard).

Do not claim cross-platform verification until another machine has actually run the JAR.
