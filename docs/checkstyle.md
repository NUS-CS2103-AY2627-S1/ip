# Checkstyle

## What is Checkstyle?

Checkstyle is a static analysis tool that checks Java source code against a configured set of style rules. In this project it provides fast, repeatable feedback for formatting, naming, imports, braces, whitespace, and Javadoc before code is reviewed.

## Do we need it?

The `seedu-java-coding-standard` skill and Checkstyle serve different purposes. The skill is a human-facing checklist that guides design and review; Checkstyle is an automated, objective check that can run locally or in continuous integration. Using both adds value because the skill covers judgment and explanations while Checkstyle catches mechanical violations consistently.

We should keep both rather than choose only one. Checkstyle is not a replacement for understanding the coding standard: it cannot reliably judge clarity, sensible design, or whether documentation is useful. The configuration in `config/checkstyle/checkstyle.xml` automates the enforceable parts of the SE-EDU standard.

## Running Checkstyle manually

From the project root, with Java 25 configured, run:

```powershell
.\gradlew.bat checkstyleMain checkstyleTest
```

To run Checkstyle together with all other verification tasks:

```powershell
.\gradlew.bat check
```

Reports are generated under `build/reports/checkstyle/`.
