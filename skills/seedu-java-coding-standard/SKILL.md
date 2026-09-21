---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard to this project when writing or reviewing Java code.
---

# SE-EDU Java Coding Standard

Use this project-specific skill before writing or reviewing Java. Read
[the local course reference](../java-coding-standards/references/course-java-standard.md)
and apply it as the primary authority; use Google Java Style only where the
course standard is silent.

Run the project's deterministic checker before and after Java changes:

```bash
python3 skills/java-coding-standards/scripts/check_java_format.py
```

Keep every class in a lowercase package rooted at `janet`, use explicit and
consistently ordered imports, and preserve four-space indentation with a
120-character hard line limit. Add descriptive Javadoc to public classes and
methods unless the course standard exempts it. Do not reformat unrelated code.

The source standard is the SE-EDU [Java coding convention](https://se-education.org/guides/conventions/java/intermediate.html).
