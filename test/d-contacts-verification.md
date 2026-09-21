# D-Contacts acceptance report

The final local verification covers the agreed D-Contacts scope on `codex/d-contacts`:
add, list, find, and delete contacts through Friday's shared GUI/console response API.
Names are unique ignoring case; a Singapore phone number, email address, or both are required.
Editing, sorting, extra fields, task linking, and external verification are outside this increment.

## Reproduce the checks

From the repository root, using Java **25.0.3.fx-zulu**:

```bash
sdk use java 25.0.3.fx-zulu
./gradlew --no-daemon --console=plain clean build test check
./gradlew test --tests friday.ConsoleUiRegressionTest
git diff --check
```

Final local results: **359 JUnit tests passed**, with zero failures, errors, or skipped tests;
this includes **all 46 console UI cases**. The clean build compiled production and test sources,
ran tests, and built the JAR and distribution archives. Gradle `check` and whitespace checks passed.
JUnit HTML results are generated at `build/reports/tests/test/index.html`, with XML under
`build/test-results/test/`. The console regression test reports the failing named case and its expected
and actual output when a comparison fails.

## Acceptance evidence

| Criterion | Result | Evidence |
| --- | --- | --- |
| Add contacts with phone only, email only, or both, with fields in either order | PASS | `ContactTest`, `ContactParserTest`; **Contact lifecycle and restart**, **Contact escaped text and field order** |
| Require names and details; reject invalid phones/emails, empty or repeated prefixes, unknown prefixes, and control characters | PASS | `ContactTest` field boundaries and control-character cases; `ContactParserTest` structure and precedence cases; **Contact invalid inputs preserve state** |
| Enforce trimmed, case-insensitive unique names while retaining internal spaces | PASS | `ContactListTest.add_duplicateName_rejectsWithoutChangingList` and shared-detail cases; **Contact searches are literal and read only** |
| List insertion order, display missing fields as `-`, and renumber after deletion | PASS | `ContactListTest` deletion cases; exact response assertions in `FridayTest`; **Contact lifecycle and restart** |
| Search every field using case-insensitive literal substrings and original contact numbers | PASS | `ContactTest`, `ContactListTest`, `ContactParserTest`; **Contact searches are literal and read only** |
| Reject missing, malformed, overflowing, and out-of-range contact numbers without mutation | PASS | `ContactParserTest` number cases, `ContactListTest` invalid-number cases, `FridayTest` unchanged-state checks |
| Produce the specified confirmations, errors, list/search headings, and help text | PASS | Exact `FridayTest` response assertions; **Help command**, **Contact invalid inputs preserve state**, and other contact UI cases |
| Persist additions and deletions across restarts using separate contact storage | PASS | `FridayTest.getResponse_contactLifecycle_returnsExactResponsesAndPersistsDeletion`; **Contact lifecycle and restart** |
| Preserve UTF-8 names, pipe/backslash escapes, and empty optional fields in four-field records | PASS | `ContactStorageTest.saveAndLoad_optionalFieldsAndEscapes_preservesSpecifiedRecords`; **Contact escaped text and field order** |
| Treat missing/empty files as empty, reject malformed files completely, and preserve original bytes | PASS | `ContactStorageTest` missing/empty, malformed-record, duplicate-name, and invalid-UTF-8 cases |
| Disable contact saving after a load failure but allow in-memory contact operations | PASS | `FridayTest.getResponse_corruptContactFile_disablesOnlyContactSaving`; **Corrupt contacts do not block task saving**, **Contact read failure** |
| Retain memory after write failure and retry the complete snapshot on the next valid mutation | PASS | `FridayTest.getResponse_contactSaveFailure_retainsMemoryAndRetriesFullSnapshot`; **Contact write failure keeps session usable** |
| Keep task and contact storage/recovery independent | PASS | `FridayTest` corruption tests in both directions and explicit-path test; both cross-collection corruption UI cases |
| Avoid saving for read-only or invalid commands | PASS | `FridayTest.getResponse_readOnlyContactCommands_doNotAttemptSaving`, unchanged-file tests; **Contact searches are literal and read only** |
| Preserve previous snapshots and clean temporary files on failed writes | PASS | `ContactStorageTest` duplicate snapshot, blocked destination, parent-is-file, and retry cases |
| Preserve existing task behavior apart from the agreed help addition | PASS | Existing task/parser/storage JUnit suites and all pre-existing console cases, with only the help expectation updated |

## Review and limits

- The final diff is confined to contact implementation, its shared-engine integration, tests,
  console fixtures, and documentation. No GUI layout, existing task wire format, or build configuration changes.
- Java changes were reviewed against the SE-EDU standard, including imports, names, comments,
  and line lengths. Checkstyle is **not configured**, so no Checkstyle result is claimed.
- The shared response API used by JavaFX is covered by JUnit. Native GUI interaction was not
  exercised in this verification; the console plan distinguishes the optional manual GUI smoke check.
- Linux/Windows execution and GitHub Actions results await a push. These results are from local macOS.
- Atomic replacement has a regular-move fallback on unsupported filesystems. Tests exercise local
  replacement and failure integrity; they do not force an unsupported-atomic-move filesystem.
- The implementation is split into `c560418` (model/storage), `8e13a2c` (command integration),
  and a final documentation/verification increment. After approval, tag the final increment
  `BCD-Extension`; pushing or merging is a separate step.
