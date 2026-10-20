# Release verification

Verified on October 20, 2026 with Java 25.

- `clean check build`: successful; 55 JUnit tests, no failures, Checkstyle passes.
- CLI: 46 commands in five isolated cases pass exact response comparisons.
- Windows GUI: FXML load, Enter, Send, blank-input handling, task persistence,
  displayed list content, goodbye, and full-window screenshot pass.
- Linux GUI: [GitHub Actions run](https://github.com/sheeppocket/ip/actions/runs/39450675564)
  passes the same smoke test under Xvfb.
- Public [v1.0 release](https://github.com/sheeppocket/ip/releases/tag/v1.0): one
  `yapper.jar` asset, 10,739,593 bytes. Downloaded without authentication;
  SHA-256 matches the locally tested build:

```text
cccf3c3a8f381c3c4b06ea19be1cefd72b7c4d2c5f34422257348b4d9fe7cfdd
```

- The downloaded JAR's classes and FXML pass GUI interaction tests. Its normal
  `java -jar yapper.jar` entry point opens the Yapper window from an isolated
  folder containing only the downloaded artifact.
- Public guide, screenshot, and linked build instructions return HTTP 200.
- PRs #3, #4, and #5 were independently prepared before the first merge;
  subsequent master integrations passed checks before each PR merge.

JavaFX 17 emits native-access and deprecated-API warnings on Java 25; these
checks completed successfully despite those warnings. Windows and Linux were
exercised; macOS and ARM64 have not been tested. ARM64 native libraries are not
bundled. Personal course participation is tracked separately in the
[remaining checks](remaining-course-work.md).
