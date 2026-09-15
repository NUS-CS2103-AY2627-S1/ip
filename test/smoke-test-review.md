# Smoke-test forum review

Reviewed on 2026-09-16. Scope: all 23 smoke-test request threads found across the public
forum's open and closed issues, with 103 replies (including acknowledgments and automatic tagging).
General discussion and tip threads are excluded from the findings.
Reports describe classmates' artifacts; applicability to Sumo was checked separately.

## Findings and actions

| Reported issue | Evidence from smoke-test requests | Sumo assessment and action |
| --- | --- | --- |
| `bye` leaves the window/process running; some apps still accept input | [Kibo #220](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/220), [Anaconda #202](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/202), [Stuart #212](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/212) | Fixed: preserve farewell for five seconds with a JavaFX timer, disable input, then exit JavaFX. Show `Closing in 5 seconds`. |
| Case-sensitive search misses useful matches | [Nudge #226](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/226), [TardT #214](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/214) | Improved: `find` ignores capitalization using a locale-independent comparison and preserves stored text/order. Command words remain lowercase. |
| Empty lists/searches show only a heading | [Koara #200](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/200) | Improved: explicit messages for empty `list`, `find`, and `on` results. |
| User guide differs from commands or lacks JAR launch instructions | [Jarvis #192](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/192), [Megatron #217](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/217), [Koara #200](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/200) | Updated JAR quick start, command table, date rules, search/exit behavior, storage location, and exact `sort` argument error. Removed obsolete console examples from the project README. |
| Wheel scrolling needs scrollbar interaction; newest reply is off-screen | [Remy #228](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/228), [Anaconda #202](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/202), [Megatron #217](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/217) | Existing explicit scroll-event handler and dialog-height listener address these patterns. Manual wheel/trackpad and auto-scroll checks remain in the GUI plan; no new visual verification claimed. |
| Long messages wrap poorly; controls clip when resized; low contrast | [Clarry #231](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/231), [Meowmeow #205](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/205), [TardT #214](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/214), [Stewie #223](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/223) | Sumo already wraps both message types, sizes bubbles to available width, and sets a minimum window size. No text-size slider. Visual checks on other platforms remain necessary. |
| Impossible dates or backwards event ranges accepted; events missing from date queries | [Elora #230](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/230), [Kibo #220](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/220) | Already covered by strict date/time parsing, end-after-start validation, and inclusive event date filtering. JUnit and console regressions pass. |
| Same-day events unavailable, or past dates accepted | [Nudge #226](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/226), [Sylveon #229](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/229), [Clarry #231](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/231) | Sumo supports same-day events with explicit increasing times. Past dates intentionally remain valid for overdue/historical work. Both rules are documented. |
| Extra spaces rejected; duplicate tasks accepted | [Megatron #217](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/217), [Remy #228](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/228) | Already normalized and validated, including duplicate detection independent of capitalization, spacing, and completion status. |
| Task containing `|` disappears after restart | [TardT #214](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/214) | Already rejects the reserved separator before saving. Existing tests also cover corrupt files, failed saves, external edits, and persistence. |
| JAR cannot launch: JavaFX launcher or missing platform libraries | [Stewie #223](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/223), [Kibo #220](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/220) | Manifest already targets the separate `sumo.Launcher`. Built JAR contains Windows DLL, Linux SO, macOS dylib, FXML, and avatars. Windows runtime probe passed. Native ARM packaging remains unverified; adding architectures safely requires separate artifacts/testing because native filenames can collide. |
| JavaFX/JDK warnings or FXML version mismatch | [Elora #230](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/230), [TardT #214](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/214) | Sumo's FXML and bundled JavaFX both use 17. Java 25 emitted native-access/deprecation warnings during the successful probe. Dependency migration is deferred to a separate compatibility change. |
| Repeated mark followed by undo reports the wrong state | [Request #173](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/173) | Not applicable: Sumo has no undo command. |
| Hard to reuse prior input; desired shortcuts or additional help controls | [Anaconda #202](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/202), [Verity #210](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/210), [Stewie #223](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/223) | Optional feature work deferred. Existing composer examples plus the updated command guide explain current functionality. |

## Verification

- Windows, Oracle Java 25.0.4.
- `test checkstyleMain checkstyleTest --rerun-tasks`: 87 passing tests, zero failures,
  errors, skipped tests, or Checkstyle violations.
- `java test/UiTestRunner.java --show-output`: all 14 cases passed, including immediate
  persistence, restart, invalid dates, duplicate prevention, mixed-case search, and empty results.
  Original `data/sumo.txt` was restored after the run.
- `shadowJar`: successful; artifact is `build/libs/Sumo.jar`.
- Focused programmatic JavaFX probe loaded the packaged FXML/controller from a copied JAR
  in an isolated folder. The stage was transparent; this was a runtime test, not a visual review.
  Invalid `bye now` remained usable for six seconds. A subsequent whitespace-padded `bye`
  disabled controls and terminated JavaFX in 5.04 seconds. Another JavaFX timer fired during
  the delay, confirming that the application thread was not blocked.
- Relaunching the packaged console backend from that folder listed `keep after exit`,
  confirming that the task survived the GUI shutdown.
- No macOS/Linux runtime or native ARM testing was performed. The complete manual GUI
  plan was updated but not executed; wheel/trackpad, visual layout, and manual-close checks
  still require a visible GUI session.

## Reviewed requests

[#235](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/235),
[#234](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/234),
[#233](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/233),
[#232](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/232),
[#231](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/231),
[#230](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/230),
[#229](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/229),
[#228](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/228),
[#227](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/227),
[#226](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/226),
[#223](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/223),
[#222](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/222),
[#221](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/221),
[#220](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/220),
[#217](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/217),
[#214](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/214),
[#212](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/212),
[#210](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/210),
[#205](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/205),
[#202](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/202),
[#200](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/200),
[#192](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/192),
[#173](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/173).
