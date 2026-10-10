# Zsiggy v1.1 release notes

Fixes the failures reported during the v1.0 smoke tests in [forum issue 443](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/443).

- Make `bye` exit the GUI and ignore blank submissions.
- Validate reversed/repeated event markers and reject unsafe pipe/newline input.
- Show load errors without overwriting corrupt saves; roll back commands when saving fails.
- Reject duplicate tasks, support more than 100 tasks, and tolerate extra whitespace.
- Keep full-list task numbers in search results and add the missing space after status indicators.
- Highlight errors and let message widths respond to window resizing.
- Bundle both Intel and Apple Silicon Mac native libraries in one JAR and select them at startup.
- Share command handling between the CLI and GUI; expand regression tests and update the User Guide.

Requires Java 25. Download the single `zsiggy.jar` asset and run `java -jar zsiggy.jar`
from a writable folder. See the [User Guide](https://luziyue525.github.io/ip/).

Validation: 20 JUnit tests and Checkstyle pass on Java 25. The packaged CLI passes a
fresh-folder save/restart smoke test. Windows GUI startup was observed without a startup
exception. Mac native-library architecture and extraction checks pass for x86_64 and aarch64.
Interactive GUI testing and fresh Mac/Linux launches remain unverified for this version.
