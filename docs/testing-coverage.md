# Test coverage baseline

## Status

This is a provisional Increment 7 baseline. The production source is
unchanged from commit `c387fc4`; Increment 7 changes only test infrastructure,
Gradle configuration, and documentation.

Coverage is reported for review during normal builds and is not enforced by
`check` or CI. An optional `coverageVerification` task checks the approved
thresholds when invoked explicitly.

## Measurement configuration

- Java: 25.0.3.fx-zulu
- Gradle: 9.6.1
- JaCoCo: 0.8.15
- Production source: `src/main/java`
- Test tasks: `test`, `clitest`, and `guiTest`
- Reports: HTML and XML

The aggregate report is generated with:

```text
./gradlew coverageReport
```

The HTML report is available at:

```text
build/reports/jacoco/coverageReport/html/index.html
```

## Optional threshold verification

Run the separate verification task with:

```text
./gradlew coverageVerification
```

The task checks the aggregate report against these minimums:

- Lines: 90%.
- Branches: 80%.
- Methods: 95%.

The task is not a dependency of `check`, and the GitHub Actions workflow is
unchanged.

## Provisional results

| Metric | Covered | Missed | Total | Coverage |
| --- | ---: | ---: | ---: | ---: |
| Lines | 1,424 | 146 | 1,570 | 90.70% |
| Branches | 556 | 125 | 681 | 81.64% |
| Methods | 344 | 17 | 361 | 95.29% |

The report was generated from the completed execution data with:

```text
./gradlew coverageReport -x test
```

The `-x test` workaround was necessary because the existing Increment 4 test
discrepancies cause the `test` task to fail before the dependent report task
can run. The failed tests still execute and contribute their collected
coverage data, so these figures must be refreshed after the discrepancies are
resolved.

## Verification status

- The three new output-normaliser tests pass.
- `clitest` passes.
- `guiTest` passes.
- Checkstyle and Javadoc pass.
- `test` reports 285 tests completed with the three pending Increment 8
  contract failures.
- Exact-message assertions remain unchanged.
