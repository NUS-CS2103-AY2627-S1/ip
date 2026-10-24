# PROJECT.md — Project Context

Project-specific context for the AI coding agent.
Complements AGENTS.md (practices & safety) and USER.md (personal preferences).

---

## Project Overview

- **Name:** Project (Clara)
- **Purpose:** Originally Project Duke (now I named it Clara), it is an educational software project designed to take you through the steps of building a small software incrementally, while applying as many Java and SE techniques as possible along the way. The project aims to build a product named Clara, a Personal Assistant Chatbot that helps a person to keep track of various things. The name Duke was originally chosen as a placeholder name, in honor of Duke, the Java Mascot.
- **Course / Context:** CS2103/T Software Engineering

---

## Tech Stack

- **Language(s):** Java 25 (JDK 25)
- **Frameworks / Libraries:** JavaFX (GUI), JUnit 5 (unit testing), Checkstyle (style enforcement)
- **Package manager:** Gradle (Gradle Wrapper)
- **Database:** None — file-based persistence (plain text, e.g., `./data/clara.txt`)
- **Other tools:** GitHub Actions (CI), shadowJar plugin (fat JAR packaging)

---

## Commands

Exact commands only. No approximations.
(My current machine: Windows 11, no WSL; if `cmd.exe` does not work, try Windows PowerShell.)

```
# Run the project
./gradlew run

# Run tests
./gradlew test

# Build fat JAR
./gradlew shadowJar

# Run Checkstyle
./gradlew checkstyleMain

# Run text-UI tests (I/O redirection)
cd text-ui-test && runtest.bat       # Windows
```

---

## Structure

Key directories only — skip anything self-explanatory or framework-conventional.

```
[directory]/   [what lives here and why it matters]
[directory]/   [what lives here and why it matters]
```

---

## Conventions

Rules the agent would not infer from the code alone. Add only what is non-obvious.

- [e.g. All API responses use a standard envelope: { data, error, meta }]
- [e.g. Tests use pytest fixtures defined in conftest.py — do not use setUp/tearDown]

Follow the [Google Java style guide](https://google.github.io/styleguide/javaguide.html).

---

## Boundaries

Files and areas the agent must not touch without explicit instruction.

- [e.g. Do not modify anything under /generated/]
- [e.g. Do not alter the database schema directly — migrations only]

---

## Discoveries

Non-obvious gotchas and decisions accumulated during the project.
**Maintained by the agent under the following protocol:**

> When the agent discovers something non-obvious that would cause a future agent to make a
> mistake — a gotcha, an unexpected coupling, a deviation from convention — it must:
> 1. State the finding clearly.
> 2. Propose the exact line(s) to add here.
> 3. Wait for explicit approval before writing anything to this file.
>
> Do not add general documentation, things inferable from the code, or anything already
> captured elsewhere. Each entry must justify its own token cost.

<!-- Entries added here over time, e.g.:
- Gotcha: [non-obvious thing discovered, and what to do / not do because of it]
- Decision: [architectural or design decision made, and the reason]
-->
