# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner
* IDE and level of expertise: IntelliJ - Beginner

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

**All Git operations must follow the SEEdu Git Conventions** from https://se-education.org/guides/conventions/git.html

### Commit Message Standards

**Subject Line:**
- Length: 50 characters preferred (72 max)
- Use imperative mood: "Add feature" not "Added feature"
- Capitalize first letter
- No period at end
- Optional scope prefix: `Person class:`, `bug fix:`

**Body (when needed):**
- Separate from subject with blank line
- Wrap at 72 characters
- Explain WHAT and WHY, not HOW
- Structure: current situation → why change needed → what's done → why this way → other info
- Use imperative mood for changes
- Provide enough detail to understand without reading diff

**Example:**
```
Add user authentication feature

Users previously had unrestricted access to all features, creating
security risks for sensitive data.

Implement JWT-based authentication with login/logout endpoints.
Users must authenticate before accessing protected resources.

JWT approach chosen over sessions for stateless scaling. Tokens
stored in localStorage and validated on each protected route.

Co-Authored-By: Claude Sonnet 4.5 <noreply@anthropic.com>
```

### Branch Naming

- Use kebab-case: `add-dark-mode`, `fix-memory-leak`
- Include meaningful keywords describing purpose
- For issues: `issueNumber-keywords` (e.g., `123-ui-freeze-error`)

### Commit Practices

- **Atomic commits**: One logical change per commit
- **Never commit**: Generated files, build artifacts, IDE configs (unless team-wide), sensitive data
- **Always include**: Co-Authored-By line for AI assistance
- Use lightweight tags unless annotated tag requested
- Do not commit or push unless explicitly asked

### Enforcement

The `seedu-git-standard` skill can verify compliance before commits.

## Coding Standards

**All Java code must follow the SEEdu Java Coding Standard (Intermediate Level)** from https://se-education.org/guides/conventions/java/intermediate.html

### Key Requirements

**Naming:**
- Classes: PascalCase nouns (e.g., `Task`, `TodoCommand`)
- Methods: camelCase verbs (e.g., `getTasks()`, `markAsDone()`)
- Variables: camelCase (e.g., `taskList`, `description`)
- Constants: UPPER_CASE_WITH_UNDERSCORES (e.g., `MAX_TASKS`)
- Boolean methods/variables: Use `is`, `has`, `was` prefix (e.g., `isDone()`, `hasNext()`)
- Collections: Plural forms (e.g., `List<Task> tasks`)

**Formatting:**
- Indentation: 4 spaces (never tabs)
- Line length: Soft limit 110 characters, hard limit 120 characters
- Braces: K&R/Egyptian style (opening brace on same line)
- Always use braces for if/for/while, even single-statement blocks

**Code Structure:**
- No wildcard imports (use explicit imports like `import java.util.ArrayList;`)
- Variables should be private unless it's a data class; provide getters/setters
- Arrays: `int[] array` not `int array[]`
- Initialize variables where declared

**Documentation:**
- JavaDoc required for all public classes and methods (except simple getters/setters, overridden methods with same semantics, test classes)
- Use American English spelling in all comments

### Enforcement

When writing or modifying Java code:
1. Follow all SEEdu coding standards
2. Check compliance before committing
3. The `seedu-java-coding-standard` skill can be used to verify compliance

## Testing

### Test Coverage Target

Maintain **~50% test coverage** focusing on the **highest-value methods**:
- Prioritize complex business logic, core functionality, and critical operations
- Focus on methods with multiple branches, edge cases, or error handling
- Avoid testing simple getters/setters unless they contain important logic
- Examples of high-value methods: command execution, parsing, storage operations, task list management

### JUnit Testing

After making any code changes to the application (especially to Gunna.java, task-related classes, command classes, Storage, Parser, or TaskList):

1. **Update JUnit tests**: Add or modify JUnit tests in `src/test/java/` to maintain the 50% coverage target for highest-value methods:
   - If you add new business logic, create corresponding test cases
   - If you modify existing methods, update their tests
   - If you add edge cases, add tests for those scenarios
   - Test files should mirror the structure: `src/main/java/gunna/Foo.java` → `src/test/java/gunna/FooTest.java`

2. **Run JUnit tests**: Execute all JUnit tests using:
   ```bash
   ./gradlew test
   ```

3. **Fix failures immediately**: If any JUnit tests fail, fix the issues before proceeding. Do not leave the codebase in a broken state.

### UI Testing

After making any code changes that affect user-facing behavior:

1. **Update test plan**: Review and update `test/ui-test-plan.md` if the code changes affect user-facing behavior or add new features. Add new test cases for new functionality or modify existing ones if behavior changed.

2. **Run UI tests**: Execute the test-ui skill by running:
   ```bash
   python3 .claude/skills/test-ui/scripts/run-ui-tests.py
   ```

3. **Fix failures immediately**: If any UI tests fail, fix the issues before proceeding.

### Test Reporting

Always show the user the test results, including:
- Number of tests passed/failed
- Details of any failures
- The console input/output for transparency

### When to Skip Testing

- Documentation-only changes (README, comments)
- Configuration file changes that don't affect functionality
- Test file updates themselves (but still run tests to verify they pass)

**IMPORTANT**: Always run both JUnit and UI tests before committing code changes to ensure the application works as expected and maintains the coverage target.
