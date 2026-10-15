---
name: seedu-git-standard
description: Git conventions and commit message guidelines based on SE-EDU standards.
---
# SE-EDU Git Standard

This skill codifies the Git conventions used in this repository. Ensure all commits and branch names adhere to these rules.

## Commit Message: Subject

* **Limit length:** Try to limit the subject line to 50 characters (hard limit: 72 chars).
* **Imperative mood:** Use the imperative mood in the subject line (e.g., `Add README.md`, not `Added` or `Adding`).
* **Capitalization:** Capitalize the first letter of the subject line (e.g., `Move index.html file to root`, not `move ...`).
* **No trailing period:** Do not end the subject line with a period (e.g., `Update sample data`, not `Update sample data.`).
* **Optional Prefix:** You may add a `<scope>:` or `<category>:` in front when applicable (e.g., `Person class: Remove static imports`, `bug fix: Add space after name`, `chore: Update release date`).

## Commit Message: Body

* **When to use:** Commit messages for non-trivial commits should have a body giving details of the commit.
* **Formatting:**
  * Separate subject from body with a single blank line.
  * Wrap the body at 72 characters.
  * Use blank lines to separate paragraphs.
  * Use bullet points as necessary.
* **Content Guidelines:**
  * Explain **WHAT** and **WHY**, not **HOW**. (The diff shows how).
  * Give an explanation detailed enough for the reader to judge if it is a good thing to do.
  * Minimize repeating information already in code comments.
* **Structure:** Structure the body as follows:
  * `{current situation}` -- use present tense (avoid terms like 'currently', 'originally' as they are implied).
  * `{why it needs to change}`
  * `{what is being done about it}` -- use imperative mood. (You can use "Let's" to indicate the beginning of this section).
  * `{why it is done that way}`
  * `{any other relevant info}`

## Branch Names

* Use a meaningful name consisting of some relevant keywords, in the kebab-case format (e.g., `refactor-ui-tests`).
* If related to an issue, use the format `issueNumber-some-keywords-from-issue-title` (e.g., `1234-ui-freeze-error`).
