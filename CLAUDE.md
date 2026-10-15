@AGENTS.md

# Java coding standard

Follow the project-specific skill `seedu-java-coding-standard` for all Java code in this repository. The project must comply with the SE-EDU Java coding standard (basic + intermediate), including naming conventions, layout, statements, and documentation requirements.

## Git conventions

Follow the project-specific skill `seedu-git-standard` for all future commits in this repository. All commit subjects and bodies must comply with the SE-EDU Git conventions.

## Testing coverage target

Maintain a minimum JUnit test coverage target of 50% for the highest-value business logic in the project. JUnit tests must be reviewed and updated after each code change to keep this target and the relevant behavior checks in compliance.

# UI testing workflow

After each code update that affects user-visible CLI behavior:
1. Review whether `test/ui-test-plan.md` needs a matching update.
2. Invoke the project skill `test-ui` via:
   `python3 .codex/skills/test-ui/scripts/run-ui-tests.py --plan test/ui-test-plan.md`
3. If a case fails, stop immediately and report both actual and expected outputs.
