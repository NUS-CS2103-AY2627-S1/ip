---
name: test-ui
description: Use when changing or verifying HABI's console commands, prompts, task output, or other text-based user interactions.
---

# Test UI

Run the recorded console scenarios exactly. Treat a changed expected output as a requirements change, not a way to hide a failure.

1. Read `test/ui-test-plan.md`. Every test case needs an aim, an input block, and an expected-output block.
2. Update that plan when a code change affects console behavior or needs a new scenario.
3. Run `python3 .codex/skills/test-ui/scripts/run_ui_tests.py`.
4. Read the printed session record. The runner stops at the first failure and shows the input, expected output, and actual output. Do not claim success after a failure.

The runner requires Java 25. If Java 25 is unavailable, use `--allow-non-25` only for local feedback and report the fallback runtime.

## Test-plan format

```markdown
## Test case: brief name

**Aim:** What the interaction proves.

**Input**
```text
command
```

**Expected output**
```text
program output only
```
```

Expected output must match standard output exactly, including line breaks and spaces. The runner displays input separately because piped Java input is not echoed.
