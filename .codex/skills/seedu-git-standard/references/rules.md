# SE-EDU Git Convention Rules

Source: <https://se-education.org/guides/conventions/git.html>

This project follows the SE-EDU Git conventions for commit messages and branch names.

## Commit Subject

- Every commit must have a well-written subject line.
- Keep the subject near 50 characters when practical; 72 characters is the hard limit.
- Use the imperative mood, such as `Add README.md` rather than `Added README.md`.
- Capitalize the first letter of the subject.
- Do not end the subject with a period.
- Add a `<scope>:` or `<category>:` prefix when it improves clarity, such as `Parser: Reject empty task numbers` or `chore: Update Gradle wrapper`.

## Commit Body

- Add a body for nontrivial commits.
- Separate the subject and body with one blank line.
- Wrap body lines at 72 characters.
- Use blank lines between paragraphs.
- Use bullet points when they make the message easier to read.
- Explain what changed and why it changed, not how the code implements it.
- Include enough detail for a reviewer to judge the change without reading the diff first.
- Avoid repeating information already clear from code comments or the diff.
- Split the commit into smaller commits if the body becomes too long or covers unrelated topics.

## Body Structure

Use this structure when it fits the change:

1. Current situation, in present tense.
2. Why it needs to change.
3. What is being done, using imperative mood.
4. Why this approach is used.
5. Any other relevant context.

Avoid words such as `currently` and `originally` when describing the current situation because the timing is implied.

## Branch Names

- Use meaningful branch names made of relevant keywords.
- Use kebab-case, such as `refactor-ui-tests`.
- For issue-related branches, use `issueNumber-some-keywords-from-issue-title`, such as `1234-ui-freeze-error`.
