@AGENTS.md

After each code update, ensure `test/ui-test-plan.md` is updated if needed, then run the UI tests (prefer the `test-ui` skill; otherwise run `python3 scripts/run-ui-tests.py`). Always include the session transcript path (`_temp/ui-test-session.txt`) in the report, and stop immediately on the first failure with actual vs expected output locations.

## JUnit test coverage target (50%)

Aim for JUnit tests to cover the top ~50% highest-value methods (prioritise complex, core, or critical business logic over simple getters/formatting).

After every code change, update/add JUnit tests as needed to keep meeting this ~50% target, and run the JUnit test suite to verify it still passes.

## Java coding standard

Follow the SE-EDU Java coding standard (basic + intermediate) for all Java code changes in this repository.
Use the project skill `$seedu-java-coding-standard` at `_codex_home/skills/seedu-java-coding-standard/SKILL.md`.

## Git conventions

Follow the SE-EDU Git conventions for all future commits in this repository.
Use the project skill `$seedu-git-standard` at `_codex_home/skills/seedu-git-standard/SKILL.md`.
