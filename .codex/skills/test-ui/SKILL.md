---
name: test-ui
description: Run the project's console UI regression tests after Java behavior changes, using the checked-in command and expected-output plan.
---

# Test UI

Use this skill after changing Java code that affects console behavior. Keep
`test/ui-test-plan.md` up to date before testing.

Each test case in the plan must have a title, an aim, a multi-line `Input`
block, and an `Expected output` block. The expected output is the program's
stdout, so it does not repeat terminal input echo.

Run the test plan from the project root:

```powershell
& .\.codex\skills\test-ui\scripts\run-ui-tests.ps1
```

The runner requires Java 25. It compiles the program, runs each test case as
a fresh session, prints its input and output transcript, and stops at the
first mismatch. Do not substitute another Java version when Java 25 is
unavailable; report the environment issue instead.
