---
name: test-ui
description: Run the project's Java console chatbot against the command-and-output cases in test/ui-test-plan.md. Use after every code update, when validating Furina's interactive behavior, or when manually checking console input and output.
---

# Test UI

Run the project-specific console test plan and show the complete input/output record for each case.

## Workflow

1. Read `test/ui-test-plan.md` before testing. Each case must include an aim, an input block, and an expected output block.
2. From the repository root, run `python .agents\\skills\\test-ui\\scripts\\run_ui_tests.py` using any available Python 3 interpreter.
3. The runner locates a Java 25 JDK, compiles all files under `src/main/java`, runs `Furina`, and compares output exactly with the plan.
4. Review the printed console session, which includes the supplied input and captured output.
5. Stop at the first failure and report the test name, actual output, and expected output.
6. When behavior changes, update `test/ui-test-plan.md` before invoking the runner.

## Failure handling

Treat compilation errors, missing Java 25, non-zero program exit, and output mismatches as failures. Do not alter source code merely to make a test pass; fix the behavior or revise the plan only when the requirement has changed.

## Resources

The `scripts/run_ui_tests.py` runner contains the deterministic compile, run, compare, and failure-reporting workflow.

### scripts/
Executable code (Python/Bash/etc.) that can be run directly to perform specific operations.

**Examples from other skills:**
- PDF skill: `fill_fillable_fields.py`, `extract_form_field_info.py` - utilities for PDF manipulation
- DOCX skill: `document.py`, `utilities.py` - Python modules for document processing

**Appropriate for:** Python scripts, shell scripts, or any executable code that performs automation, data processing, or specific operations.

**Note:** Scripts may be executed without loading into context, but can still be read by Codex for patching or environment adjustments.

### references/
Documentation and reference material intended to be loaded into context to inform Codex's process and thinking.

**Examples from other skills:**
- Product management: `communication.md`, `context_building.md` - detailed workflow guides
- BigQuery: API reference documentation and query examples
- Finance: Schema documentation, company policies

**Appropriate for:** In-depth documentation, API references, database schemas, comprehensive guides, or any detailed information that Codex should reference while working.

### assets/
Files not intended to be loaded into context, but rather used within the output Codex produces.

**Examples from other skills:**
- Brand styling: PowerPoint template files (.pptx), logo files
- Frontend builder: HTML/React boilerplate project directories
- Typography: Font files (.ttf, .woff2)

**Appropriate for:** Templates, boilerplate code, document templates, images, icons, fonts, or any files meant to be copied or used in the final output.

---

**Not every skill requires all three types of resources.**
