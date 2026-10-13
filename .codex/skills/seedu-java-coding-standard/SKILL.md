---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard, based on the basic + intermediate rules, to Java source and test code in this project. Use when creating, reviewing, refactoring, or updating Java files; when checking style compliance; or when asked to follow SE-EDU, CS2103T, or project Java conventions.
---

# SE-EDU Java Coding Standard

## Overview

Use this skill whenever Java code in this repository is created or changed. Prefer the rules summarized in `references/rules.md`; for any Java style topic not covered there, fall back to the Google Java Style Guide.

## Workflow

1. Read `references/rules.md` before editing Java code.
2. Inspect changed Java files for naming, layout, statement, variable-scope, and Javadoc issues.
3. Apply the simplest style-preserving fix that does not change behavior.
4. Add or preserve Javadocs for all public classes and public nontrivial methods, except test methods, getters/setters, and overrides whose inherited documentation applies exactly.
5. Keep test method names in the `featureUnderTest_testScenario_expectedBehavior` style when underscores improve clarity.
6. Run the project’s normal validation commands after code changes when practical.

## Project Notes

- Keep package names lowercase and under the existing `baymax` root unless a broader package restructuring is explicitly requested.
- Keep public fields out of production classes unless the class is intentionally a behavior-free data holder.
- Use explicit imports only; never introduce wildcard imports.
- Use 4-space indentation, K&R braces, and wrapped-line indentation of 8 spaces relative to the parent line.
- Keep lines below 120 characters, aiming below 110 characters where readability allows.
