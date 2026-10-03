---
name: seedu-git-standard
description: Use when proposing, writing, reviewing, or creating Git commit messages or branch names in the Verity project.
---

# SE-EDU Git Standard

## Overview

Apply this project's required Git conventions whenever a commit message or branch name is involved. Read [references/git-rules.md](references/git-rules.md) before proposing or creating either artifact.

The checklist combines the [official SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) with the course-specific policy in `GitStandards.pdf`: every commit subject must follow the official subject rules; a body is optional, but a supplied body must follow at least the basic body rules.

## Workflow

1. Inspect the actual diff or described change to understand its purpose and rationale.
2. Draft a subject that states the cohesive change in imperative mood.
3. Validate the subject and any optional body against every required item in `references/git-rules.md`; use intermediate guidance as recommended and advanced guidance only when useful.
4. When naming a branch, use the branch section of the same checklist.
5. Present the compliant text without committing, branching, tagging, or pushing unless the user explicitly authorizes that action.

## Boundaries

- Never reject a commit solely because it has no body; the course makes bodies optional.
- Do not require Conventional Commits. A scope or category prefix is permitted, not mandatory.
- A lowercase category prefix such as `chore:` is valid when the summary after the colon starts with a capital letter.
- Do not rewrite or create unrelated commits while applying this skill.

## Common mistakes

- Using past tense (`Fixed`) or a gerund (`Fixing`) instead of imperative mood (`Fix`).
- Treating the 50-character target as the hard limit; 72 characters is the hard limit.
- Ending the subject with a period.
- Requiring a body despite the course-specific optional-body policy.
- Treating the intermediate WHAT/WHY guidance or advanced body structure as mandatory basic rules.
- Inventing `issue-42-fix-parser`; the official issue-related format is `42-fix-parser`.
