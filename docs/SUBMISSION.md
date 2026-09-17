# Week 6 submission checklist

Deadline: 18 September 2026, 23:59 Singapore time.
Requirements: https://nus-cs2103-ay2627-s1.github.io/website/schedule/week6/project.html

## Prepared locally

- A-BetterGui: responsive message widths, visible Orbit heading, error styling and input polish.
- A-MoreErrorHandling: whitespace handling, repeated parameter rejection, saved-record validation,
  visible load/save warnings, protection against overwriting unreadable data, and temporary-file saves.
- A-MoreTesting: five isolated regression tests covering malformed commands, malformed records,
  corrupt-file preservation, write failure recovery, and persistence round trips.
- A-UserGuide: complete docs/README.md with commands, examples, limitations and recovery instructions.
- docs/Ui.png: real JavaFX rendering of the entire client window, with the product name visible.
  Native desktop capture returned black in this environment, so this uses the application's scene snapshot.
- build/libs/orbit.jar: Java 25 fat JAR with Windows/macOS/Linux JavaFX dependencies.
- Validation: 22 passing tests, Checkstyle main/test passing, packaged java -jar startup from a fresh
  folder, and GUI command execution/persistence in an isolated folder.

## Release and website

The submission uses lightweight increment tags A-MoreErrorHandling, A-BetterGui,
A-MoreTesting, and A-UserGuide, with each increment branch retained after merging
into master. The final release uses tag v0.2 and has one asset: orbit.jar.

- Product website: https://elljaykei.github.io/ip/
- Screenshot: https://elljaykei.github.io/ip/Ui.png
- Release: https://github.com/Elljaykei/ip/releases/tag/v0.2
- GitHub Pages source: master branch, /docs folder.

## Student verification remaining

1. Ask a teammate on another OS to run java -jar orbit.jar, exercise the commands,
   and confirm tasks reload after restart.
2. Check the course progress dashboard records the required increment tags and
   screenshot, including the existing Git Standard requirement.

The GitHub code, online guide and released JAR form the final submission. There
is no Canvas upload. Cross-platform dependencies are packaged; other operating
systems have not been tested here.
