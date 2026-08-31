---
name: test-ui
description: Run and verify scripted command-line UI tests for this project's Java Bibi chatbot. Use after changing user-visible commands, responses, task formatting, or input parsing, and whenever asked to validate interactive console behaviour.
---

# Test UI

Update test/ui-test-plan.md when a user-visible behaviour changes. Every case must include an aim, console input, and expected output.

Configure JDK 25, then run:

~~~powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.4"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
python .codex/skills/test-ui/scripts/run_ui_tests.py --project-root . --plan test/ui-test-plan.md
~~~

The script compiles the application, runs each case, checks expected output fragments in order, and records console input and output. Stop at the first failed case; report its expected and actual output without changing the test plan to hide the failure.

Expected-output blocks contain ordered, non-empty output fragments. Do not include the You: prompt because piped test input is not echoed by the console.

Because Bibi saves tasks to `data/bibi.txt`, each case starts from a known save file:

- By default the save file is deleted before the case runs, so cases stay independent.
- An optional `### Saved data` text block writes that content to `data/bibi.txt` first, which is how a case checks loading or a corrupted file.
- A `<<restart>>` line inside the input block closes Bibi and starts it again, which is how a case checks that tasks survive between sessions.
