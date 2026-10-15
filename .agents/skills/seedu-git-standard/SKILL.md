---
name: seedu-git-standard
description: Comprehensive Git conventions based on the SE-EDU Git Standard (Basic, Intermediate, and Advanced rules). Use when proposing, formatting, or creating Git commits, commit messages, and branch names in this repository.
---

# SE-EDU Git Standard

Follow these conventions when creating or proposing Git commit messages, branches, and version control workflows in this project.

---

## 1. Commit Message: Subject Line

Every commit must have a well-written, concise subject line.

* **Length limit**:
  * Soft limit: Try to keep the subject line within **50 characters**.
  * Hard limit: Must never exceed **72 characters**.
  * *Rationale*: Git tooling, log viewers, and web interfaces truncate long message subjects.
* **Imperative mood**:
  * Always use the imperative mood (spoken as a command) in the subject line.
  * Good: `Add README.md`, `Refactor TaskList class`, `Fix date parsing exception`
  * Bad: `Added README.md`, `Adding README.md`, `Adds README.md`
* **Capitalization**:
  * Capitalize the first letter of the subject line.
  * Good: `Move index.html file to root`
  * Bad: `move index.html file to root`
* **No trailing period**:
  * Do **not** end the subject line with a period (`.`).
  * Good: `Update sample data`
  * Bad: `Update sample data.`
* **Scope / Category prefix (Optional)**:
  * You may prepend an optional `<scope>:` or `<category>:` prefix when applicable.
  * Examples:
    * `Person class: Remove static imports`
    * `Main.java: Remove blank lines`
    * `bug fix: Add space after name`
    * `chore: Update release date`
  * Formats such as Conventional Commits (`feat: ...`, `fix: ...`) are also acceptable.

---

## 2. Commit Message: Body

Non-trivial commits must include a detailed commit body giving context and rationale for the changes.

* **Separation**:
  * Always separate the subject line from the body with a single **blank line**.
* **Line wrapping**:
  * Wrap every line in the commit body at **72 characters**.
* **Paragraph separation**:
  * Use a single blank line between paragraphs in the body.
* **Explain WHAT and WHY, not HOW**:
  * Explain **WHAT** the commit changes and **WHY** it was done that way.
  * The reader can inspect the diff to see **HOW** the change was implemented.
  * The description must be sufficiently detailed so a reviewer can judge whether the change is sound without needing to reverse-engineer the diff.
  * Minimize repeating information already present in code comments of the same commit.
  * If the description becomes excessively long, split the commit into smaller, finer-grained commits.
* **Structured body template**:
  When structuring non-trivial commit bodies, follow this logical flow:
  ```text
  {current situation} -- in present tense (avoid redundant words like 'currently' or 'originally')
  {why it needs to change}
  {what is being done about it} -- in imperative mood (can start with "Let's")
  {why it is done that way}
  {any other relevant info}
  ```
* **Lists and bullet points**:
  * Use bullet points (`* `) when breaking down multiple changes or distinct steps in a commit.

### Example Commit Messages

#### Example 1: Code Quality / Refactoring
```text
Person attributes classes: extract a parent class PersonAttribute

Person attribute classes (e.g. Name, Address, Age) have some common
behaviors (e.g. isValid()).

The common behaviors across person attribute classes cause code
duplication. Extracting the common behavior into a super class allows
us to use polymorphism when dealing with person attributes. For example,
validity checking can be done for all attributes of a person in one loop.

Let's pull up behaviors common to all person attribute classes into a new
parent class named PersonAttribute.

Using inheritance is preferable over composition in this situation
because the common behaviors are not composable.
```

#### Example 2: Bug Fix with Bullet Points
```text
Find command: make matching case-insensitive

Find command is case-sensitive.

A case-insensitive find is more user-friendly because users cannot be
expected to remember the exact case of the keywords.

Let's,
* update the search algorithm to use case-insensitive matching
* add a script to migrate stress tests to the new format
```

#### Example 3: Multi-commit PR step
```text
Unify variations of toSet() methods

There are several methods that convert a collection to a set. In some
cases the conversion is in-lined as a code block in another method.

Unifying all those duplicated code improves the code quality.

As a step towards such unification, let's extract those duplicated code
blocks into separate methods in their respective classes. Doing so will
make the subsequent unification easier.
```

---

## 3. Branch Naming Conventions

Maintain clean and consistent branch names:
* Use meaningful keywords in **kebab-case** (e.g., `refactor-ui-tests`, `add-deadline-command`).
* If the branch corresponds to an issue, follow the pattern:
  `issueNumber-some-keywords-from-issue-title`
  (e.g., `1234-ui-freeze-error`, `42-fix-null-pointer-on-empty-input`).

---

## 4. Git Workflow Guidelines

* **Atomic commits**: Each commit should represent a single logical change or step.
* **Lightweight tags**: Use lightweight tags unless annotated tags are explicitly requested.
* **Safety**: Do not commit or push to remote repositories unless explicitly instructed by the user.
