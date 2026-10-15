# Test UI

A Claude Code project skill for this repository. Runs the console test cases
recorded in `test/ui-test-plan.md` against the compiled `Coco` chatbot and
verifies the actual output matches the expected output.

## Use

```bash
python3 .claude/skills/test-ui/scripts/run_ui_tests.py . test/ui-test-plan.md Coco
```

Run from the repository root. The script compiles `src/main/java/*.java`,
then for each test case in the plan file runs the program fresh with that
test case's input and diffs the captured output against what's expected. It
stops at the first failing test case and reports the mismatch.

## Repository layout

- `SKILL.md` — instructions for using the skill.
- `scripts/run_ui_tests.py` — the test-plan parser and runner (standard
  library only).

See `../../../test/ui-test-plan.md` for the test cases themselves.
