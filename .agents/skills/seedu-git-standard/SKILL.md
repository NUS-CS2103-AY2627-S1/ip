---
name: seedu-git-standard
description: >-
  Enforces SE-EDU Git Conventions based on se-education.org guidelines.
  Mandates commit message structure, imperative subject lines (<=50 chars), body formatting (<=72 chars), and rationale.
---

# SE-EDU Git Conventions

Reference: [SE-EDU Git Conventions](https://se-education.org/guides/conventions/git.html)

This skill mandates Git version control conventions and commit message formatting for SE-EDU projects.

---

## 1. Commit Message Structure

Every non-trivial commit must follow the two-part structure:

```text
<Subject line: <= 50 characters, imperative mood, no ending period>

<Body: wrapped at 72 characters, explaining WHAT changed and WHY>
```

---

## 2. Subject Line Rules

* **Imperative Mood**: Use imperative verbs (e.g. `Add`, `Fix`, `Refactor`, `Update`, `Remove`), as if giving a command.
  * Good: `Add TaskList class to manage tasks`
  * Bad: `Added TaskList class` / `Adding TaskList class`
* **Length Limits**:
  * **Target limit**: $\le 50$ characters.
  * **Hard limit**: $\le 72$ characters.
* **Capitalization**: Capitalize the first letter.
* **Punctuation**: Do **NOT** end the subject line with a period (`.`).
* **Optional Prefix**: Can optionally prefix with a scope or category:
  * e.g. `Storage: Fix file encoding issue`
  * e.g. `TaskList: Add findTasksOn method`

---

## 3. Body Rules & Structure

* **Separation**: Always leave exactly one blank line between the subject and the body.
* **Line Wrapping**: Hard wrap all body lines at **72 characters**.
* **Paragraphs**: Separate distinct paragraphs or sections with a blank line.

### Required 5-Part Body Structure:
1. **{Current situation}**: State the situation in the **present tense**. Avoid words like "currently" or "originally" (they are implied).
2. **{Why it needs to change}**: Explain the problem, limitation, or rationale necessitating the change.
3. **{What is being done about it}**: Use **imperative mood**. The phrase `Let's ...` can be used to introduce this section.
4. **{Why it is done that way}**: Justify design choices or alternatives considered.
5. **{Any other relevant info}**: Provide references, links, or side effects (if applicable).


---

## 4. Commit Scope & Best Practices

* **Atomic Commits**: Each commit should be a cohesive, standalone increment of work. Avoid bundling unrelated fixes or features together.
* **Tags**: Use lightweight tags for milestones (e.g. `v0.1`, `Level-8`) unless explicitly asked for annotated tags.
* **Working Tree**: Keep builds and tests passing on every commit.
