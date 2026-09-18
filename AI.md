# AI Assistance

OpenAI Codex was used as an AI coding collaborator for the Week 6 enhancements.

## How AI helped

* `A-BetterGui`: proposed a calmer visual hierarchy, then implemented the JavaFX message layout and reusable CSS theme.
* `A-Personality`: drafted and applied a consistent cloud-and-sky voice while keeping command outcomes clear.
* `A-MoreErrorHandling`: identified damaged storage records and whitespace input as weak points, then implemented recovery warnings and more forgiving parsing.
* `A-MoreTesting`: proposed regression scenarios and implemented command-lifecycle, persistence, storage round-trip, corrupted-record, and error-path tests.
* `A-UserGuide`: drafted the command reference, common workflows, data-recovery guidance, troubleshooting steps, and AI-use credit.
* Release preparation: checked the official Week 6 requirements, verified the full-window screenshot, expanded the fat JAR with Windows and Linux JavaFX runtimes, and smoke-tested it from an empty folder.

## Verification performed

The AI-assisted changes were checked with the Java 25 Gradle test, Checkstyle, and Shadow JAR tasks. The final suite contains 26 passing tests. The release JAR was also launched from an empty temporary folder to check that it does not rely on repository paths.

The repository owner remains responsible for reviewing and submitting the final work.
