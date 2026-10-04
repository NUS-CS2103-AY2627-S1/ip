---
name: seedu-git-standard
description: >-
  Enforces the SE-EDU Git Conventions based on https://se-education.org/guides/conventions/git.html.
  Use when formulating, proposing, reviewing, or generating Git commit messages, branch names, and tags.
---

# SE-EDU Git Conventions

This skill provides the mandatory Git rules and conventions defined by the SE-EDU initiative for commit messages, branch names, and version control workflows.

---

## 1. Commit Message Subject Line

Every commit must have a well-crafted subject line following these rules:

* **Imperative Mood**: Use imperative mood in the subject (e.g., `Add README.md`, `Fix date parser`, `Refactor UI`).
  * ✅ Good: `Add TaskList class`
  * ❌ Bad: `Added TaskList class`, `Adding TaskList class`, `Adds TaskList class`
* **Length Limits**: Target $\le 50$ characters (hard limit: 72 characters).
* **Capitalization**: Capitalize the first letter of the subject line.
* **No Trailing Period**: Do not end the subject line with a period.
* **Optional Prefix**: You may prepend `<scope>:` or `<category>:` when applicable:
  * e.g., `Parser: Add date validation`, `Storage: Create parent directories`, `bug fix: Handle empty input`, `chore: Update release date`.

---

## 2. Commit Message Body

Non-trivial commits must include a detailed message body explaining **WHAT** and **WHY** (not HOW):

* **Blank Line Separation**: Separate the subject line from the body with exactly one blank line.
* **Line Wrapping**: Hard wrap all lines in the body at **72 characters**.
* **Paragraph & List Structure**: Separate paragraphs with a blank line. Use bullet points (`* `) where appropriate.
* **Content Focus**:
  * Explain **WHAT** the commit changes and **WHY** it was done.
  * The code diff shows *HOW*; the commit message explains the intent and rationale.
  * Avoid repeating information already obvious in code comments.
* **Standard Body Structure**:
  1. `{current situation}` &mdash; Describe in present tense without redundant filler like *"currently"* or *"originally"*.
  2. `{why it needs to change}` &mdash; Explain the shortcoming, limitation, or requirement.
  3. `{what is being done about it}` &mdash; Use imperative mood or start with *"Let's ..."*.
  4. `{why it is done that way}` &mdash; Explain design choices or tradeoffs.
  5. `{any other relevant info}` &mdash; Reference issues or links if applicable.

### Example Commit Message:
```text
Person attributes: extract parent class PersonAttribute

Person attribute classes (e.g. Name, Address) share common behaviors
such as isValid(). This duplication causes maintenance issues and
prevents polymorphic handling.

Extracting common behavior into a super class allows polymorphism
when validating person attributes across collections.

Let's,
* pull up common validation behaviors into PersonAttribute
* make attribute classes inherit from PersonAttribute

Inheritance is preferred over composition here because the common
behaviors are not independently composable.
```

---

## 3. Branch Naming Conventions

* Use **kebab-case** consisting of meaningful keywords (e.g., `branch-A-Packages`, `refactor-ui-tests`, `add-gradle-support`).
* For issue-specific branches, prefix with the issue number: `<issueNumber>-<keywords-from-title>` (e.g., `123-fix-date-parsing`).

---

## 4. Tags

* Use lightweight tags (e.g., `git tag v0.1`, `git tag Level-8`) unless an annotated tag is explicitly requested.
