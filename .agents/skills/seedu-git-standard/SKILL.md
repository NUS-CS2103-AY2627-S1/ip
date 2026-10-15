---
name: seedu-git-standard
description: >-
  Use this skill whenever creating Git commits, tags, branches, or writing commit messages
  in this project to adhere to the SE-EDU Git conventions.
---

# SE-EDU Git Standard

This guide documents the Git conventions to follow when working on SE-EDU projects. Follow the rules outlined in [SE-EDU Git Conventions](https://se-education.org/guides/conventions/git.html).

---

## 1. Commit Messages

### Subject Line
Every commit must have a well-crafted subject line:
* **Imperative Mood**: Use the imperative mood (e.g., `Add ...`, `Refactor ...`, `Update ...`, `Fix ...`, `Remove ...`).
  * *Good*: `Add TaskListTest unit tests`
  * *Bad*: `Added TaskListTest`, `Adding TaskListTest`, `Adds TaskListTest`
* **Length**: Keep the subject line concise.
  * Target limit: **50 characters**
  * Hard limit: **72 characters**
* **Capitalization**: Capitalize the first letter of the subject line.
* **No Trailing Period**: Do **not** end the subject line with a period (`.`).
* **Optional Scope / Category**: You may prefix the subject line with a component name, category, or scope:
  * `TaskList: Use List interface instead of ArrayList`
  * `Parser: Fix missing deadline validation`
  * `bug fix: Handle negative task index gracefully`

### Message Body
For non-trivial commits, provide additional context and details in the commit body:
* **Separation**: Separate the subject line from the body with a **single blank line**.
* **Content**:
  * Explain the context, motivation, or problem being solved.
  * Outline the changes made using the *"Let's ..."* phrasing or clear bullet points.
  * Explain the rationale for non-obvious design choices.

### Example Commit Message
```text
Task: Adhere to SE-EDU Java coding standard

The previous codebase used ArrayList concrete types in public APIs
and lacked proper Javadoc formatting.

Let's:
* Use List<Task> interface type in TaskList and TaskStorage
* Clean up Javadoc comments to use third-person declarative summaries
* Ensure all tests pass under Java 25
```

---

## 2. Tags
* Use **lightweight tags** by default (e.g., `git tag v0.1`) unless an annotated tag is explicitly requested.

---

## 3. Branches
* Use **lowercase kebab-case** for branch names reflecting their purpose (e.g., `branch-A-JavaDoc`, `fix-parser-bug`).
* Do not include personal names in branch names.

---

## 4. General Git Guidelines for AI Agents
* **Do not commit or push unless explicitly instructed by the user.**
* When suggesting a Git command in conversation, briefly explain what it does.
* Ensure all tests pass (`./gradlew test`) before committing code changes.

