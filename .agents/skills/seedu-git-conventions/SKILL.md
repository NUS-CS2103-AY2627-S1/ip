---
name: seedu-git-conventions
description: Apply the SE-EDU Git conventions to commit messages, branch naming, tagging, and workflow in this project.
---

# SE-EDU Git Conventions Skill

Use this skill whenever creating, reviewing, or proposing Git commits, branches, tags, and pull requests in this repository. It is based on the [SE-EDU Git Conventions](https://se-education.org/guides/conventions/git.html).

---

## 1. Commit Message Subject Line

Every commit must have a well-formatted subject line:

- **Imperative Mood**: Use imperative mood (spoken as giving a command).
  - ✅ `Add support for JavaFX GUI`
  - ❌ `Added support for JavaFX GUI` (past tense)
  - ❌ `Adds support for JavaFX GUI` (present third-person)
  - ❌ `Adding support for JavaFX GUI` (continuous)
- **Character Limit**: Aim for **50 characters** or fewer; hard limit is **72 characters**.
- **Capitalization**: Capitalize the first letter of the subject line.
- **No Trailing Period**: Do not end the subject line with a period (`.`).
- **Optional Prefix**: Use a `<scope>:` or `<category>:` prefix when helpful to identify the modified component:
  - `Ui: Adjust dialog bubble spacing`
  - `Parser: Support optional time parameter`
  - `Docs: Update user guide with GUI instructions`
  - `Build: Upgrade shadow plugin to 9.5.1`

---

## 2. Commit Message Body

Non-trivial commits must include a body separated from the subject line by a single blank line.

- **Line Wrapping**: Wrap body lines at **72 characters**.
- **Focus on What and Why**: Explain the problem, the reason for the change, and the chosen solution. The diff shows *how* it was done; the commit message explains *why* and *what*.
- **Recommended Structure**:
  1. **Current situation**: Describe the state before this commit in present tense.
  2. **Problem/Motivation**: Explain why the current situation is problematic or what requirement motivated the change.
  3. **Action/Solution**: Describe what this commit does in imperative mood.
  4. **Rationale/Trade-offs**: Explain why this approach was chosen over alternatives if non-obvious.
- **Formatting Guidelines**:
  - Use bullet points for listing discrete sub-changes.
  - Separate paragraphs with a single blank line.
  - Avoid unnecessary filler words such as "currently", "originally", or "as we know".

### Example Commit Message:

```text
Ui: Convert dialog boxes to FXML custom components

The previous programmatic UI construction mixed layout definitions
with application controller logic in a single file, making styling
and future UI enhancements difficult to maintain.

Extract MainWindow and DialogBox layouts into separate FXML files and
implement custom controller classes. Inject the core Echo backend
into the main controller to preserve separation of concerns.

- Add MainWindow.fxml and DialogBox.fxml in src/main/resources/view/
- Implement DialogBox custom HBox with avatar display
- Bind scroll pane position to dialog container height changes
```

---

## 3. Branch Naming

- **Kebab-case**: Use lowercase alphanumeric words separated by hyphens (`-`).
- **Descriptive Keywords**: Use concise, meaningful keywords representing the feature or refactor.
  - `refactor-ui-fxml`
  - `add-find-command`
  - `fix-date-parser`
- **Issue-related Branches**: For work corresponding to an issue tracker item, prefix with the issue number:
  - `<issue-number>-<short-description-from-title>`
  - Example: `42-fix-event-duration-bug`

---

## 4. Tagging

- **Lightweight Tags**: Use lightweight tags by default (unless annotated tags are explicitly requested).
- **Version Numbering**: Format release tags with a `v` prefix followed by semantic versioning (e.g., `v0.1`, `v1.0`).

---

## 5. Commit Hygiene & Workflow

- **Atomic Commits**: Keep each commit focused on a single logical change. Do not bundle unrelated refactorings or fixes into one commit.
- **Working Code**: Ensure code compiles and all automated tests and checkstyle checks pass before committing.
- **Never Commit Generated Files**: Do not commit build artifacts (`build/`, `out/`, `*.class`, `*.jar`), temporary data (`data/*.txt`), or IDE caches.
- **User Confirmation**: Do not execute `git commit` or `git push` unless the user explicitly requests you to do so.
