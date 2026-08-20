# SE-EDU Java Coding Standard

Apply the intermediate-level rules from https://se-education.org/guides/conventions/java/intermediate.html

## Key rules to enforce

### Naming
- Packages: all lowercase (`mochi`, `mochi.command`, `mochi.task`)
- Classes/enums: nouns, PascalCase (`TaskList`, `AddDeadlineCommand`)
- Methods: verbs, camelCase (`getStatusIcon`, `markAsDone`)
- Variables: camelCase (`isDone`, `fullCommand`)
- Constants: SCREAMING_SNAKE_CASE (`LINE`)
- Booleans: prefix with `is`/`has`/`was` (`isDone`, `isExit`)
- Plurals for collections (`tasks`, `parts`)
- Abbreviations: lowercase in names (`exportHtmlSource`, not `exportHTMLSource`)

### Layout
- 4-space indentation (no tabs)
- Line length ≤ 120 chars (soft limit 110)
- Wrapped line indentation: 8 spaces
- K&R (Egyptian) braces
- Break after comma, before operator
- Method name stays attached to open parenthesis

### Statements
- Explicit imports only (no wildcards)
- Import ordering: static first, then java, javax, org, com, then project packages
- Every class in a package
- Array specifier on type, not variable
- Declare variables in smallest scope, initialize at declaration
- No public class variables (except constants)
- Curly brackets on all loops and conditionals, even single-statement
- Conditional body on separate line from `if`

### Comments
- English, American spelling
- Javadoc on all public classes and methods (getters/setters and overrides may omit)
- First sentence: short summary starting with verb ("Returns...", "Adds...")
- `@param` for all params or none; `@return` unless obvious
- Indented relative to code
