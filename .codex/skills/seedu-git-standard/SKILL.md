---
name: seedu-git-standard
description: The project's mandated Git conventions (SE-EDU standard, https://se-education.org/guides/conventions/git.html), covering commit message subject/body format and branch naming. Required for all commits and branches created in this repository - consult before drafting or creating any commit message or branch name.
---

# SE-EDU Git Conventions

Source: https://se-education.org/guides/conventions/git.html

Apply this whenever drafting a commit message or naming a branch in this
repository.

## Commit message: subject line

- Limit to 50 characters where possible; 72 is the hard limit.
- Imperative mood - "Add README.md", not "Added README.md" or "Adding README.md".
- Capitalize the first letter - "Move index.html file to root", not
  "move index.html file to root".
- No period at the end - "Update sample data", not "Update sample data.".
- Optionally prefix with a scope/category when it adds clarity, e.g.
  `Person class: Remove static imports`, `bug fix: Add space after name`,
  `chore: Update release date`.

## Commit message: body

- Separate the subject from the body with one blank line.
- Wrap body text at 72 characters.
- Separate paragraphs with blank lines; use bullet points where they help.
- Explain **what** and **why**, not **how** - the diff already shows the
  how. If the "why" needs a long explanation, that's often a sign the
  commit should have been split into smaller ones.
- Don't just repeat what's already said in code comments.
- A useful shape to follow: current situation (present tense) -> why it
  needs to change -> what this commit does about it (imperative mood,
  often introduced with "Let's ...") -> why done this way -> anything
  else relevant (references, links). Avoid words like "currently" or
  "originally" - describe the present state plainly instead.

**Example:**
```
Unify variations of toSet() methods

There are several methods that convert a collection to a set. In some
cases the conversion is in-lined as a code block in another method.

Unifying all those duplicated code improves the code quality.

As a step towards such unification, let's extract those duplicated code
blocks into separate methods in their respective classes. Doing so will
make the subsequent unification easier.
```

## Branch names

- Meaningful, kebab-case, with relevant keywords - e.g. `refactor-ui-tests`.
- For issue-related branches: `issueNumber-some-keywords-from-issue-title`,
  e.g. `1234-ui-freeze-error`.
