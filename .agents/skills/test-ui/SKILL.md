---
name: test-ui
description: Run automated UI/IO test cases for the Braun application against test/ui-test-plan.md, verifying expected console outputs, printing full session logs, and failing fast on mismatches. Use when asked to run UI tests, test user input/output, or execute the UI test plan.
---

# UI Testing Skill (`test-ui`)

Execute automated text-based console UI tests for the Braun chatbot application. This skill verifies that user inputs produce the exact expected outputs, generates interactive console session transcripts, and immediately halts with a detailed diagnostic diff upon encountering any discrepancy.

## Quick Start

From the repository root (`ip/`), execute:

```bash
python3 .agents/skills/test-ui/scripts/run-ui-tests.py
```

To run a custom or alternate test plan:

```bash
python3 .agents/skills/test-ui/scripts/run-ui-tests.py --plan path/to/custom-plan.md
```

To skip recompilation of Java sources:

```bash
python3 .agents/skills/test-ui/scripts/run-ui-tests.py --no-compile
```

---

## Test Plan Structure (`test/ui-test-plan.md`)

All UI test cases are recorded in [ui-test-plan.md](file:///Users/andyang/Desktop/iP_CS2103T/ip/test/ui-test-plan.md). Each test case must specify:
1. **Case Header**: `### Test Case <Number>: <Title>`
2. **Aim**: `**Aim**: <Description of what is being tested>`
3. **Input**: `**Input**:` followed by a fenced code block containing the exact command string (or empty for startup).
4. **Expected Output**: `**Expected Output**:` followed by a fenced code block containing the exact expected console response (including divider lines and indentation).

### Example Test Case Format

````markdown
### Test Case 2: List Empty Tasks
**Aim**: Verify that the `list` command displays an empty task list header when no tasks have been added.

**Input**:
```
list
```

**Expected Output**:
```
    ____________________________________________________________
     Here are the tasks in your list:
    ____________________________________________________________
```
````

---

## Testing Workflow & Execution Behavior

1. **Compilation**:
   - The test runner automatically locates Java 25 / JDK compilers and builds `src/main/java/*.java`.
2. **Sequential Session Execution**:
   - Braun is launched in an interactive subprocess.
   - For startup (Test Case 1), the initial banner and greeting are validated.
   - For each subsequent test case, the input command is written to standard input, and the response block (enclosed by divider lines) is parsed and compared against the expected output.
3. **Fail-Fast on Mismatches**:
   - If any test case output does not match the expected output, testing terminates **immediately**.
   - The runner prints the failing Test Case number, title, aim, input, and a unified colored diff highlighting the exact line differences.
   - The process exits with return code `1`.
4. **Session Log & Transcript**:
   - When all test cases pass, a summary is displayed followed by the complete console session transcript showing all interleaved user prompts (`> <command>`) and Braun's replies.

---

## Resources & Helper Scripts

- **Runner Script**: [run-ui-tests.py](file:///Users/andyang/Desktop/iP_CS2103T/ip/.agents/skills/test-ui/scripts/run-ui-tests.py)
- **Default Test Plan**: [ui-test-plan.md](file:///Users/andyang/Desktop/iP_CS2103T/ip/test/ui-test-plan.md)
