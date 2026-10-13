---
layout: default
title: Wangsa credits
---

# Credits

## AI assistance

Manutd1234 used **OpenAI Codex with [GPT-6 Astra](https://developers.openai.com/api/docs/models/gpt-6-astra)**
for help with implementation, OOP refactoring, GUI layout, error handling,
tests, JAR packaging, and documentation. AI helped write and revise substantial
parts of the project. The project author is responsible for the final work.
This includes the documentation readability and attribution review.

The optional `@ai` command is separate: it uses Groq through LangChain4j.
Its default model is `openai/gpt-oss-20b`, not GPT-6 Astra.

## Interface symbols

Wangsa uses simple arrows and a compass drawn in JavaFX code. It does not use
third-party artwork, character images, generated illustrations, or audio.
The user guide shows screenshots of the app itself. Codex assisted with capturing
the command examples from the published v0.6 JAR using a separate demo task list.
The [capture record](https://github.com/Manutd1234/ip/blob/master/docs/screenshots/README.md)
identifies the JAR and commands used.

## Starter code and guides

| Source | Where it contributed |
| --- | --- |
| [Course iP template](https://github.com/NUS-CS2103-AY2627-S1/ip), based on [SE-EDU Duke](https://github.com/se-edu/duke) | Starter repository and the course's task-manager baseline. Original template contributors are preserved in `CONTRIBUTORS.md`. |
| [OpenJFX fat-JAR guidance](https://openjfx.io/openjfx-docs/#modular) | Packaging approach in `build.gradle`, which also credits the source beside the build logic. |
| [SE-EDU AI tutorial](https://se-education.org/guides/tutorials/addingAiToJavaApp.html) | Integration approach for optional command help; credited in `AiHelper`'s Javadoc. |
| [AddressBook Level 3 User Guide](https://se-education.org/addressbook-level3/UserGuide.html) | Inspiration for the user guide's formats, examples, and command summary; credited in the guide itself. |

## Libraries and tools

### Included in the application

| Library | Version | Used for |
| --- | --- | --- |
| [JavaFX](https://openjfx.io/) | 17.0.7 | Desktop interface and platform-native libraries |
| [LangChain4j](https://github.com/langchain4j/langchain4j) | 1.10.0 | Groq integration, core types, and HTTP client modules |
| [SLF4J](https://www.slf4j.org/) | 2.0.17 | Logging API and no-operation provider |
| [Jackson](https://github.com/FasterXML/jackson) | Core/databind 2.20.1; annotations 2.20 | JSON support brought in by LangChain4j |
| [JSpecify](https://jspecify.dev/) | 1.0.0 | Nullness annotations brought in by LangChain4j |
| [JTokkit](https://github.com/knuddelsgmbh/jtokkit) | 1.1.0 | Tokenizer dependency brought in by LangChain4j |

Groq hosts the optional AI service; it is not required for ordinary task commands.
Wangsa uses the libraries above rather than claiming their implementation as its own.

### Development and documentation

| Tool | Used for |
| --- | --- |
| [JUnit](https://junit.org/) | Unit tests |
| [Gradle](https://gradle.org/) | Building and running the project |
| [Checkstyle](https://checkstyle.org/) | Checking Java style |
| [GitHub Actions](https://github.com/features/actions) | Automated build and release checks |
| [Python](https://www.python.org/) | Packaged-application smoke-test runner |
| [GitHub Pages](https://pages.github.com/), [Jekyll](https://jekyllrb.com/), and [Primer](https://github.com/pages-themes/primer) | Hosting and formatting the user guide |

Direct Java dependency versions are set in `build.gradle`; the Gradle version is
in `gradle/wrapper/gradle-wrapper.properties`. To inspect all runtime dependencies,
run `./gradlew dependencies --configuration runtimeClasspath` (use `gradlew.bat`
on Windows). Each dependency keeps its own licence; this page is an acknowledgement,
not a replacement for those licences.
Each release JAR includes a snapshot of its credits at
`META-INF/wangsa/CREDITS.md`. The website can contain newer acknowledgements
than an already-published JAR.

## Peer testing

Thanks to [@lingsongc](https://github.com/lingsongc),
[@Papangkorn-Pann](https://github.com/Papangkorn-Pann),
[@Dancodes2](https://github.com/Dancodes2), and
[@LINGSIHAN](https://github.com/LINGSIHAN) for Windows smoke testing v0.6.
Their reports, environments, and scope are recorded in the
[test record](https://github.com/Manutd1234/ip/blob/master/tests/peer-smoke-test.md)
and [issue #471](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/471).
Dancodes2's low-severity CLI encoding finding led to the ASCII-safe terminal help
rendering released in v0.7. The exact v0.7 JAR is checked by the automated
release workflow on Windows, Linux, Intel Mac, and Apple Silicon Mac. That
automation is recorded separately from the human v0.6 tester reports; the v0.7
release page links its verification run and artifact digest. The
[v0.8 release](https://github.com/Manutd1234/ip/releases/tag/v0.8) records its own
automated results separately; earlier human reports are not relabelled as v0.8 tests.

[Back to the user guide](https://manutd1234.github.io/ip/)
