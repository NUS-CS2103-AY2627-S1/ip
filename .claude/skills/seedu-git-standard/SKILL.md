---
name: seedu-git-standard
description: The mandatory Git commit and branch conventions for this project, derived from the se-education.org Git conventions. Apply whenever writing a commit message or naming a branch so that subject lines, bodies, and branch names all conform.
---

# se-education Git conventions

This is the authoritative Git standard for this project. It is derived from the
se-education.org Git conventions (https://se-education.org/guides/conventions/git.html).
Every commit made in this repository must follow it. These conventions are
graded, so apply them from the first commit rather than fixing history later.

This skill covers **how to write commits and name branches**. The
project-specific Git *policy* — which tag type to use, and when Claude may
commit or push — lives in [AGENTS.md](../../../AGENTS.md), not here.

## How to use this skill

1. Before writing any commit message, follow the **Subject line** and **Body**
   rules below.
2. Before creating a branch, follow the **Branch names** rules.
3. Self-check the commit against the **Review checklist** at the end before
   committing.

---

## 1. Subject line

- **Imperative mood** — write the subject as a command: `Add README.md`, not
  `Added README.md` or `Adding README.md`. A good test: the subject should
  complete the sentence "If applied, this commit will _______".
- **Capitalize the first letter** of the subject line.
- **No trailing period.**
- **Length** — aim for **50 characters**; hard limit **72**.
- **Optional scope/category prefix** — you may prefix the subject with the scope
  or category it touches, followed by a colon:
  - `Person class: Remove static imports`
  - `bug fix: Add space after name`

## 2. Body

- **Non-trivial commits need a body** giving the details of the commit.
- **Separate the subject from the body with one blank line.**
- **Wrap the body at 72 characters.**
- Use blank lines to separate paragraphs; use bullet points where they aid
  clarity.
- **Explain WHAT changed and WHY, not HOW** — the diff already shows how. Give
  enough detail that a reader can judge whether the change is appropriate without
  reading the diff.
- **Do not repeat** information already present in the code comments of the same
  commit.
- A useful body arc: the current situation (present tense) → why it needs to
  change → what this commit does about it (imperative) → why this approach → any
  other relevant notes.
- When describing the current situation, avoid words such as `currently` and
  `originally`; the timing is already implied.
- You may use `Let's` to introduce the section that describes the change made
  by the commit.

## 3. Branch names

- Use a **meaningful, kebab-case** name built from relevant keywords, e.g.
  `refactor-ui-tests`.
- **Issue branches** use the form `issueNumber-some-keywords-from-issue-title`,
  e.g. `1234-ui-freeze-error`.

---

## Example

```text
Person class: Remove static imports

The Person class used a wildcard static import for its constants,
which obscured where each constant was defined and risked name
clashes as the class grew.

Import each constant explicitly instead, so a reader can trace every
name to its source and the compiler flags any future collision.
```

## Review checklist

Run through this before committing:

- [ ] Subject is in imperative mood, capitalized, ≤ 72 chars (aim 50), no period.
- [ ] Optional scope prefix uses `Scope: Subject` form if present.
- [ ] Non-trivial commit has a body, separated from the subject by a blank line.
- [ ] Body is wrapped at 72 chars and explains WHAT and WHY, not HOW.
- [ ] Current situation uses present tense without redundant timing words such
      as `currently` or `originally`.
- [ ] Body does not merely repeat the code comments in the same commit.
- [ ] Any new branch is kebab-case (issue branches: `issueNumber-keywords`).
