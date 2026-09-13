# Git Conventions

This is a project summary of the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html). Consult it before
creating branches or commits.

## Branch names

- Use a meaningful, keyword-based name in kebab case, such as `refactor-ui-tests`.
- For an issue-related branch, use `issueNumber-keywords-from-issue-title`, such as `1234-ui-freeze-error`.

## Commit subject

- Every commit needs a clear subject line.
- Use imperative mood: `Add storage support`, not `Added storage support`.
- Capitalize the first word and do not end the subject with a period.
- Aim for 50 characters; never exceed 72 characters.
- An optional scope or category prefix may improve clarity, for example `Storage: Save task
  updates` or `bug fix: Reject empty descriptions`.

## Commit body

- Give non-trivial commits a body, separated from the subject by one blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines. Use bullets when helpful.
- Explain **what** changed and **why**; leave implementation details for the diff.
- A useful structure is: current situation, why it should change, the imperative change being
  made, why that approach was chosen, and any other relevant context.
- Use present tense for the current situation and imperative mood for the proposed change. Avoid
  redundant words such as “currently” or “originally”.
- Do not repeat information that is already clear from code comments in the same change.
- If the explanation is becoming long, consider splitting the change into smaller, focused commits.

Use natural prose rather than literal labels. For example:

```text
Add validation for empty task descriptions

Task creation accepts empty descriptions, which produces unusable tasks.
Reject empty descriptions so every task remains identifiable.
Add input validation at task creation because it keeps invalid state out of
the task list.
```
