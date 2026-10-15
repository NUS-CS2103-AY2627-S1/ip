@AGENTS.md

Maintain JUnit coverage for the top approximately 50% highest-value methods,
prioritizing complex, core, and business-critical logic. After each code change,
update or add the relevant JUnit tests to comply with this target, then run the
Gradle test task.

After each code update, maintain `test/ui-test-plan.md` when needed and invoke `$test-ui` to run the console UI tests.
