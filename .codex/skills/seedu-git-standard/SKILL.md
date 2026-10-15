---
name: seedu-git-standard
description: Enforce the SE-EDU Git conventions for this project, including imperative commit subjects, informative bodies, and professional commit hygiene.
---

# SE-EDU Git conventions

Follow the SE-EDU Git conventions for this project whenever creating or updating git history. Use the guide at https://se-education.org/guides/conventions/git.html as the required baseline.

## Commit message requirements

1. Every commit must have a subject line written in the imperative mood.
   - Examples: `Add README.md`, `Update task parser`, `Fix task list limit`
   - Avoid past tense or gerund subjects like `Added README.md` or `Adding README.md`

2. Capitalize the first letter of the subject line.

3. Do not end the subject with a period.

4. Keep the subject concise: ideally under 50 characters and never over 72 characters.

5. Use a scope or category prefix only when it improves clarity.
   - Example: `Parser: Handle invalid task ranges`

6. For non-trivial commits, include a body separated from the subject by a blank line.
   - Keep the body within about 72 characters per line
   - Use bullet points when helpful
   - Explain what changed and why, not how it was implemented in detail

7. Structure the body to explain the motivation, impact, and intent of the change.

## Project requirement

All future git commits in this repository must follow this standard. Do not create commit messages that violate the SE-EDU Git conventions.
