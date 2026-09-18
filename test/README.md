# Testing Aether

The UI test runner compiles the Java sources with JDK 25, runs every scenario in
`ui-test-plan.md`, and compares the chatbot output with the expected output.

From the repository root, run:

```powershell
python test/run_ui_tests.py
```

The compiled `.class` files are written to `out/`, which is ignored by Git.
Keep the test plan updated when commands or chatbot output change.
