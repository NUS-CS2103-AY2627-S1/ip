---
name: seedu-git-standard
description: The mandatory Git commit-message and branch-naming standard for this repository (se-education conventions). Consult BEFORE writing any commit message or creating a branch — covers subject-line form, body content, and branch naming, with a pre-commit checklist.
---

# seedu Git standard (this project)

Condensed from https://se-education.org/guides/conventions/git.html.
Applies to **every** commit and branch in this repository.

---

## 1. Commit subject line

- **Imperative mood**, as if completing "If applied, this commit will …":
  `Add README.md` — not `Added README.md`, not `Adding README.md`.
- **Capitalize the first letter**: `Move index.html to root`, not `move …`.
- **No period at the end**: `Update sample data`, not `Update sample data.`
- **≤ 50 characters** preferred; **72 is the hard limit**.
- Optional `<scope>:` / `<category>:` prefix when it adds clarity — the scope is
  Capitalized as a noun, the description after the colon still starts with a
  Capitalized imperative verb:
  - `Person class: Remove static imports`
  - `Main.java: Remove blank lines`
  - `bug fix: Add space after name`
  - `chore: Update release date`
- This repo's existing history uses the plain `Capitalized imperative` form with
  **no** conventional-commits `feat:` / `fix:` prefix (e.g. `Add Parser class`,
  `Fix Gradle build/run configuration for Pan`). Match it — use a `<scope>:`
  prefix only when it genuinely helps.

## 2. Commit body (when the change needs explaining)

- **Separate the subject from the body with one blank line.**
- **Wrap the body at 72 characters.**
- Separate paragraphs with blank lines; use **bullet points** where they read
  better than prose.
- Explain **WHAT** and **WHY**, not HOW — the current situation, why the change
  is needed, what is being done, and why it is done this way. The diff already
  shows the how.
- **Don't repeat** what the code comments already say.
- A trivial, self-evident change (a typo fix, a rename) may be subject-only.
- This project's `AGENTS.md` also asks that the body carry enough detail to
  explain the rationale — so lean towards writing one for any non-trivial change.

## 3. Branch names

- **kebab-case**, a few relevant keywords: `refactor-ui-tests`,
  `add-gradle-support`.
- If the branch addresses a tracked issue:
  `issueNumber-keywords-from-issue-title`, e.g. `1234-ui-freeze-error`.

## 4. Tags

- Use **lightweight** tags unless an annotated tag is explicitly requested
  (project rule in `AGENTS.md`).

## 5. Pre-commit checklist

- [ ] Subject in imperative mood, Capitalized, no trailing period, ≤ 50 (≤ 72 hard)
- [ ] If a body: blank line after subject, wrapped at 72, explains what/why
- [ ] One coherent change per commit — unrelated edits split into separate commits
- [ ] Branch (if new) is kebab-case with meaningful keywords
- [ ] Not committing or pushing unless the user asked
