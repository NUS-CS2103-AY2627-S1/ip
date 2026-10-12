---
name: seedu-git-standard
description: Enforce the SE-EDU Git commit message and branch conventions from https://se-education.org/guides/conventions/git.html for all Git commits and branches.
---

# SE-EDU Git Conventions Standard

All Git commits and branch operations in this repository must strictly adhere to the [SE-EDU Git Conventions](https://se-education.org/guides/conventions/git.html).

---

## 1. Commit Message Subject Line

Every commit must have a well-formed subject line:

- **Length**:
  - Limit to **50 characters** where possible (hard limit: **72 characters**).
- **Mood**:
  - **Use the imperative mood** (e.g. `Add README.md`, `Refactor Ui class`, `Fix date parsing bug`).
  - Never use past tense or continuous tense (`Added`, `Adding`, `Adds`, `Fixed`, `Fixing`).
- **Capitalization**:
  - Capitalize the first letter of the subject line (e.g. `Move index.html to root`).
- **No Trailing Punctuation**:
  - Do not end the subject line with a period (`.`).
- **Prefixes / Scopes**:
  - An optional `<scope>:` or `<category>:` prefix may be used when helpful:
    - `Ui: Refactor display methods`
    - `Storage: Handle missing parent directories`
    - `fix: Correct off-by-one error in delete`
    - `docs: Add Javadoc to DateTimeUtil`

---

## 2. Commit Message Body

Non-trivial commits must include an explanatory body:

- **Separation**:
  - Separate the subject from the body with exactly **one blank line**.
- **Line Wrapping**:
  - Wrap every line of the body at **72 characters**.
- **Paragraph Separation**:
  - Use blank lines to separate paragraphs.
- **Content Focus (WHAT and WHY, not HOW)**:
  - Explain **WHAT** the commit changes and **WHY** it was done that way.
  - The reader can examine the diff to understand HOW the code was implemented.
- **Recommended Body Structure**:
  ```text
  {current situation} -- in present tense

  {why it needs to change}

  {what is being done about it} -- in imperative mood (can use "Let's ...")

  {why it is done that way}

  {any other relevant info}
  ```
- **Bullet Lists**:
  - Bullet points (`*`) are encouraged when summarizing multiple distinct actions or sub-components.

---

## 3. Branch Naming Conventions

- Use lowercase `kebab-case` with descriptive keywords (e.g. `refactor-ui-tests`, `add-more-oop`).
- If addressing an issue: `issueNumber-keywords` (e.g. `123-fix-storage-bug`).
