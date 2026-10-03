# Git convention checklist

This checklist summarizes the official [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html), reviewed on 2026-08-27, with the course policy from `GitStandards.pdf` applied where it is more specific.

## Commit subject: required for every commit

- Write a well-formed subject for every commit.
- Aim for 50 characters or fewer. Never exceed 72 characters.
- Use imperative mood: `Add README.md`, not `Added README.md` or `Adding README.md`.
- Capitalize the first letter of the subject summary.
- Do not end the subject with a period.
- Optionally prefix the summary with `<scope>:` or `<category>:` when useful. Examples: `Parser: Improve error message` and `chore: Update release date`.
- Conventional Commits is an optional alternative convention, not a project requirement.

## Commit body: optional; basic rules required when supplied

The course policy makes a body optional even for nontrivial commits. If a body is present:

- Separate it from the subject with one blank line.
- Wrap every body line at 72 characters.
- Separate paragraphs with blank lines; use bullet points when they improve clarity.

## Commit body: intermediate recommendations

When a fuller explanation is useful:

- Explain what the change is and why it is needed or designed that way. Let the diff show how it was implemented.
- Give enough context for a reader to judge the change without first reading the diff, while avoiding repetition of code comments.
- If the body becomes too long, split the work into smaller cohesive commits.

## Commit body: optional advanced structure

For a detailed body, use this sequence when it improves clarity:

1. Describe the existing situation in present tense.
2. Explain why it needs to change.
3. Describe the change in imperative mood.
4. Explain why that approach was chosen.
5. Add other relevant information.

Avoid `currently` and `originally` when they merely restate that the situation exists before this commit. `Let's` may introduce the section describing the change.

## Branch names

- Use a meaningful name made from relevant keywords in lowercase kebab-case, such as `refactor-ui-tests`.
- For a branch related to an issue, use `issueNumber-keywords-from-issue-title`, such as `1234-ui-freeze-error`.
- Do not use spaces, underscores, or uppercase letters.

## Complete example

```text
Parser: Clarify invalid task errors

Invalid task numbers produce messages that do not identify the failed input.

Clarifying the message helps users correct commands without guessing.

Let's include the invalid number in the parser error message.
```
