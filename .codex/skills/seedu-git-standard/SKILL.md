---
name: seedu-git-standard
description: Apply the SE-EDU and project commit-message conventions when proposing, reviewing, or creating Git commits in this project.
---

# SE-EDU Git Standard

Use the official [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
when proposing, reviewing, or creating commit messages. The
[CS2103T standards page](https://nus-cs2103-ay2627-s1.github.io/website/admin/standardsAndConventions.html)
distinguishes course requirements from conditional guidance.

## Course-required subject conventions

Every commit must have a clear subject that:

- aims for at most 50 characters and never exceeds 72 characters;
- uses imperative mood, such as `Add deadline parsing`;
- starts with a capital letter;
- does not end with a period; and
- optionally begins with a helpful `<scope>:` or `<category>:` prefix.

## Commit bodies

The course makes the body optional. When a body is present, it must at least:

- be separated from the subject by one blank line;
- wrap lines at 72 characters; and
- use blank lines between paragraphs.

This repository has a stronger project convention: include a body for every
non-trivial commit. Explain what changed and why it was needed, leaving implementation
details to the diff. Use present tense for the existing situation and imperative
wording for the change. Avoid redundant words such as `currently` and `originally`,
use bullets when they improve clarity, and split unrelated changes into separate
commits rather than writing an overly long body.

Base every proposed message on the actual diff and commit scope. Do not create a
commit unless the user explicitly authorizes it.
