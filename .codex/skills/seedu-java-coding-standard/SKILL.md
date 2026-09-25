---
name: seedu-java-coding-standard
description: >-
  Apply the SE-EDU basic and intermediate Java coding conventions when writing,
  editing, reviewing, or testing Java code in this project.
---

# SE-EDU Java coding standard

Use this skill for every Java source or test change in this repository.

## Required reference

Before acting, read the project checklist in
[`docs/java-coding-standard.md`](../../../docs/java-coding-standard.md). It
summarizes the authoritative
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
and records this project's package, Java-version, and validation requirements.

If guidance is not covered there, follow the Google Java Style Guide. Preserve
explicit project requirements when they are more specific.

## Apply the standard

- Check package and import organization, naming, layout, whitespace, braces,
  variable scope, encapsulation, control flow, and comments in every affected
  Java file.
- Use the three-part `feature_scenario_expectedBehavior` convention when a
  descriptive JUnit test name benefits from underscores.
- Add Javadoc where the checklist requires it. Do not add comments that merely
  repeat self-explanatory code.
- Keep behavior unchanged during a style-only task unless the user explicitly
  requests a behavior change.
- Limit cleanup to affected code unless the user requests a repository-wide
  audit.

## Validate Java changes

Use Java 25. Run the relevant tests and, for a completed project-wide audit,
run:

```bash
./gradlew test
javadoc -quiet -Xdoclint:all -d /tmp/shan-javadocs \
    -sourcepath src/main/java -subpackages shan
git diff --check
```
