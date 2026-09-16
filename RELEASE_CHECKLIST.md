# Final submission checklist

The course's [final submission instructions](https://nus-cs2103-ay2627-s1.github.io/website/schedule/week6/project.html#4-submit-the-final-version)
require the GitHub code, published User Guide, and one downloadable release JAR.
There is no separate Canvas submission.

## Build and technical checks

Use Java 25. Run `java -version`, then `./gradlew clean check shadowJar`.
Distribute `build/libs/arin-all.jar`, not the thin JAR. The fat JAR must include
JavaFX, the FXML/CSS layout, the avatar, and all supported platform libraries.

The `Release checks` workflow builds one candidate and tests that same JAR
with Java 25 on Windows x64, Linux x64, Intel macOS, and Apple-silicon macOS.
It runs the production GUI's controls from isolated folders and checks
command entry, task operations, errors, help, resizing, and restart loading.
This automated check does not replace a teammate's test drive.

For a local GUI smoke test after building, copy the JAR to an empty folder,
open a terminal in that folder, and run:

```sh
java -jar arin-all.jar
```

Also check with Java 25 that does not have JavaFX installed. A JDK that
already includes JavaFX can mask missing dependencies in the JAR.

## Teammate test drive

Send a teammate the release link and ask them to record their OS, CPU
architecture, Java version, and the results of these checks:

- [ ] Download the JAR into a new folder and launch with `java -jar arin-all.jar`.
- [ ] Enter `todo read book` using Enter, and `deadline submit report /by 2026-09-18` using Send.
- [ ] Add `event meeting /from 2pm /to 4pm`, then run `list`.
- [ ] Run `mark 1`, `unmark 1`, and `find book`.
- [ ] Run `sort deadline` and `sort name`; confirm the new full-list numbering.
- [ ] Run `mark 0` and an invalid date; confirm helpful errors and no task changes.
- [ ] Click Help, resize the window, and scroll the conversation.
- [ ] Run `delete 1`, then `bye`; reopen from the same folder and confirm saved tasks.
- [ ] Report any failure with the command and error, without sharing private task data.

Run only one instance per data folder. Back up real data before testing.

## Publication and course checks

- [ ] Confirm the [grading criteria](https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-grading.html), including the weekly participation and peer-review requirements.
- [ ] Check your own masked row on the [daily course dashboard](https://nus-cs2103-ay2627-s1.github.io/dashboards/contents/ip-progress.html), especially **Git Standard**.
- [ ] Ensure the final code and lightweight increment tags are pushed; this release adds `A-BetterGui` and `A-MoreErrorHandling`.
- [ ] Verify the [published User Guide](https://goh-wei-chong.github.io/ip/) matches the JAR and has no broken formatting.
- [ ] Verify [Ui.png](https://goh-wei-chong.github.io/ip/Ui.png) shows one complete, current GUI window.
- [ ] Confirm original sources for reused work, including the owner-supplied robot illustration.
- [ ] Publish one non-draft GitHub release with **exactly one `.jar` asset**, not a ZIP.
- [ ] Open the [release page](https://github.com/Goh-Wei-Chong/ip/releases/latest) without signing in, download its JAR, and check it matches the tested file.

These boxes are a reusable release procedure, not a claim that historical
participation, teammate testing, or source attribution has been verified.
