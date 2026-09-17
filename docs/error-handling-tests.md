# Error-handling regression checks

Use Zulu Java `25.0.3.fx-zulu` for every build and application run. The complete
JUnit suite includes the JavaFX window tests, so it needs a graphical macOS
session.

```sh
export JAVA_HOME="$HOME/.sdkman/candidates/java/25.0.3.fx-zulu"
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew clean test checkstyleMain checkstyleTest
python3 test/run_ui_plan.py --plan test/ui-test-plan.md -- \
  "$JAVA_HOME/bin/java" -cp "$PWD/build/classes/java/main" glennon.Glennon
```

The console command uses `test/run_ui_tests.py` and `test/ui-test-plan.md`, both
included in the repository. It requires Python 3.9 or newer on macOS or Linux,
with no additional Python packages or Codex skills. Keep the plan up to date
when command behavior changes.

## Coverage

| Area | Checks |
| --- | --- |
| Command parsing | Blank input, whitespace, parameter order and repetition, missing values, extra arguments, control characters, integer bounds, impossible dates, and event ordering |
| Task identity | Type, case-sensitive description, internal spacing, Unicode edge spaces, schedule boundaries, completion status, deletion and re-addition, and equivalent date-only deadlines |
| Task validation | Blank descriptions including nonbreaking spaces, unsafe descriptions, missing dates, reversed or equal event timestamps, Unicode, punctuation, and valid all-day events |
| Saved data | Malformed dates, invalid UTF-8 and Base64, wrong fields, invalid status, duplicate records, invalid event ranges, missing files, and access failures |
| Save failures | Add, delete, mark, unmark, and sort preserve count, identity, order, status, and saved data; retries apply the intended change once |
| File replacement | Replacement failure, interruption, unsupported atomic operations, denied access, temporary-file cleanup, directories, and symbolic links |
| Application integration | Filtered result numbers target the same missions for updates and deletion; startup errors block mutations; duplicates remain rejected after restart; GUI error presentation and correction after rejected commands |

The JUnit tests exercise the complex parsing, collection, command, storage,
and session methods through their public behavior. Assertions cover both
reported errors and state preservation.

## Console storage-failure fixtures

`test/run_ui_plan.py` delegates exact output comparison and fail-fast process
handling to the repository's shared runner. Every case gets a fresh temporary
working directory, so tests never modify the user's `data/glennon.txt`.

A case can add these lines below its aim:

```text
- Block storage before command: 2
- Restore storage before command: 4
```

After the initial task has been saved, the fixture moves the data file aside
and creates a directory at the same path. The next save must fail. The case
then checks the unchanged task list. Before retrying, the fixture verifies the
saved bytes are unchanged and restores the file. The retry's response and
resulting task list are compared exactly.

The plan's coverage matrix identifies at least seven distinct positive and
three negative cases for each changed feature family. The runner also rejects
duplicate complete input sequences.
