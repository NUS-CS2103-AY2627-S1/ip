---
name: specify-before-implementing
description: "Guide non-trivial feature work through discovery, specification, review, implementation, verification, and optional commit stages before code changes."
---

# Specify Before Implementing

Use this skill for a new feature, a behavior change, or any request where product decisions could materially affect the design. It is especially useful when command syntax, validation, output, persistence, compatibility, tests, or documentation are not already settled. Do not impose it on a trivial typo, a clearly scoped one-line fix, or work that already has an approved implementation-ready specification unless the user asks for the workflow.

When this skill is applicable, proactively suggest it to the user in the commentary and identify the current stage. A concise suggestion is: “This feature is a good fit for `$specify-before-implementing`; we can start with Discovery to settle the product decisions before editing code.” If the user explicitly invokes the skill, follow the stage gates below.

## Stage selection

Infer the stage from the user's request and supplied material:

1. A feature request without settled decisions starts in **Discovery**.
2. User decisions or notes start **Specification**.
3. A draft specification starts **Review**.
4. An approved specification starts **Implementation**.
5. A request to check completed work starts **Verification**.
6. A request to commit completed and verified work starts **Commit**.

If the supplied material mixes stages, complete the earliest incomplete stage. Do not silently treat an unreviewed draft as approved. If the user explicitly names a stage, honor that stage while preserving its read-only or approval boundary.

## Stage 1: Discovery — read-only

Inspect the existing project, relevant tests, user-facing documentation, persistence format, and repository instructions. Ask every product question whose answer could change the implementation or user experience, including questions about:

- user commands, syntax, aliases, and help text;
- normal behavior, ordering, state transitions, and exact display output;
- valid, invalid, boundary, and conflicting inputs;
- error messages and validation precedence;
- storage format, migration, failure handling, and backward compatibility;
- interactions with existing commands and behavior;
- JUnit, UI/end-to-end tests, documentation, and acceptance criteria.

Ask focused questions in one grouped message when possible. Separate evidence discovered in the codebase from decisions that require the user's choice. Do not change files, generate code, update tests, or create a specification file in this stage.

## Stage 2: Specification — read-only

Using the user's decisions and repository evidence, write an implementation-ready specification in the response. It must include:

- scope and explicit non-goals;
- user commands and grammar, with valid and invalid examples;
- exact output for success, errors, help, and relevant edge cases;
- state and storage changes, including format and compatibility rules;
- acceptance criteria that can be tested independently;
- JUnit, UI/end-to-end, and documentation updates;
- files or modules likely to change;
- assumptions, unresolved questions, and any risk that still blocks approval.

Prefer the smallest useful scope. Mark inferred behavior as an assumption rather than presenting it as a user decision. Do not edit files unless the user explicitly asks to save the specification as an artifact.

## Stage 3: Review — read-only

Critically compare the specification with the current codebase. Identify ambiguities, contradictory requirements, missed edge cases, compatibility hazards, test gaps, and unnecessary complexity. Recommend concrete amendments and a smallest useful scope. End with a clear approval checklist or a revised specification suitable for approval. Do not modify implementation, tests, documentation, or configuration.

## Stage 4: Implementation — approved scope only

Implement only the approved specification. Before editing, state any necessary deviation and wait for approval if it changes product behavior, scope, compatibility, storage, or user-visible output. Preserve unrelated work in a dirty worktree and do not commit or push unless separately requested.

Follow all repository instructions and applicable coding skills. For this project, Java changes must follow `.codex/skills/seedu-java-coding-standard/SKILL.md`; use Java 25 for build and application tasks. Update relevant JUnit tests. If behavior visible through the CLI changes, review and update `test/ui-test-plan.md`, then use `.codex/skills/test-ui/SKILL.md` as required by the repository instructions. Update the relevant project documentation, using the paths that actually exist rather than assuming a particular README or test-plan location.

## Stage 5: Verification — no unrequested fixes

Verify the implementation against the approved specification. Run relevant JUnit tests, Checkstyle, build checks, and end-to-end/UI CLI checks required by the repository. Report every acceptance criterion as **Pass** or **Fail**, with the command, evidence, and any failure details. Include the complete UI-test transcript when the project instructions require it. Distinguish code failures from environment or tooling failures. Do not make further changes during this stage unless the user explicitly asks for fixes; a failed check should be reported, not silently repaired.

## Stage 6: Commit — explicit request only

Only create a commit after the user explicitly requests it. Review the final diff for unrelated or unintended changes and confirm that it matches the approved specification and verification results. Follow the repository's Git conventions, including branch and commit-message rules; for this project, read `.codex/skills/seedu-git-standard/SKILL.md` before preparing the commit. Create one focused commit unless the user requests another arrangement. Do not push.

## Handoff format

At each stage, lead with the outcome and state the current gate:

- Discovery: decisions needed, grouped questions, and relevant codebase evidence.
- Specification: the complete proposed contract and approval status.
- Review: findings, recommended amendments, and approval checklist.
- Implementation: files changed, scope adherence, tests, and deviations.
- Verification: acceptance-criteria matrix with pass/fail evidence and transcripts.
- Commit: commit hash, subject, and concise summary of what and why.

Never claim a later stage is complete when an earlier approval gate is unresolved.
