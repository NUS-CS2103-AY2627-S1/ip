---
name: cli-stress-test
description: Stress-test a Java command-line application with reviewed valid and invalid input cases, detecting crashes, hangs, stack traces, and unexpected exits.
---

# CLI stress testing

Use `fuzz_cli.py` for a Java CLI that reads commands from standard input. The
The `error-cases.txt` and `valid-cases.txt` corpora send each line to the CLI exactly as written.

1. Compile the application first and identify its main class.
2. Run one of the two saved corpora:
   `python3 skills/cli-stress-test/fuzz_cli.py --classpath out --main puke.Puke --input-file skills/cli-stress-test/error-cases.txt`
   Run the valid corpus similarly with `--input-file skills/cli-stress-test/valid-cases.txt`.
3. Treat a non-zero exit, timeout, Java exception/stack trace, or missing normal termination as a failure.

The error corpus covers blank input, missing arguments, malformed task IDs, missing `/by`, `/from`, and `/to` markers, and unknown commands. The valid corpus covers normal task creation, listing, marking, unmarking, finding, and deleting.
